package sph.sphfluidsimulation;

import javafx.scene.layout.Pane;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class Physics {
    //constants
    public static final double range = 12;
    public static final double gravity = 0.55;
    public static final double airPressure = 1;
    public static final double viscosity = 0.07;
    public static final double density = 1;
    public static final int gridSize = 20;

    //window varaiables

    public int numberOfNeighbors;
    ArrayList<Neighbor> neighbors;
    final Object NEIGHBORS_LOCK;

    public SimulationContext simulationContext;
    public ExecutorService executorService;

    public Physics(SimulationContext simulationContext) {
        this.simulationContext = simulationContext;

        numberOfNeighbors = 0;
        neighbors = new ArrayList<>();
        NEIGHBORS_LOCK = new Object();

        executorService = Executors.newFixedThreadPool(simulationContext.threadCount);
    }

    public void mergeNeighbor(List<Neighbor> subNeighbor) {
        synchronized (NEIGHBORS_LOCK) {
            neighbors.addAll(subNeighbor);
            numberOfNeighbors += subNeighbor.size();
        }
    }

    //could do parallel
    //calculateForce: Calculates the forces between particles based on their neighboring relationships.
    public void calculateForce() {
        for (Neighbor neighbor : neighbors) neighbor.calculateForce();
    }

    /*
    drawParticles: Adds particles to the pane.

    Parameters:
    - pane (Pane): Display pane
    - particles (List<Particle>): List of particles within the simulation
     */
    public void drawParticles(Pane pane, List<Particle> particles) {
        pane.getChildren().clear();
        for (Particle particle : particles) pane.getChildren().add(particle);
    }


    public void moveParticles(List<Particle> particles, SimulationContext simulationContext) throws InterruptedException {
        //clear grid for a new iteration
        for (Grid[] grids : SphController.grid) for (Grid grid : grids) grid.clearGrid();

        int totalParticles = SphController.particles.size();
        int subsection = totalParticles / this.simulationContext.threadCount;

        List<Callable<Void>> updateGridTasks = new ArrayList<>();
        List<Callable<Void>> findNeighborsTasks = new ArrayList<>();
        List<Callable<Void>> calculatePressureTasks = new ArrayList<>();

        for (int i = 0; i < simulationContext.threadCount; i++) {
            int from = i * subsection;
            int to = (i == simulationContext.threadCount - 1) ? totalParticles - 1 : from + subsection;

            updateGridTasks.add(new UpdateGridsTask(from, to, simulationContext, this));
        }
        executorService.invokeAll(updateGridTasks);

        //Todo - most time taken per Thread - findNeighborsTasks - fix smth
        for (int i = 0; i < simulationContext.threadCount; i++) {
            int from = i * subsection;
            int to = (i == simulationContext.threadCount - 1) ? totalParticles - 1 : from + subsection;

            findNeighborsTasks.add(new FindNeighborsTask(from, to,simulationContext, this));
        }
        //clear neighbors for a new iteration
        neighbors.clear();
        executorService.invokeAll(findNeighborsTasks);


        for (int i = 0; i < simulationContext.threadCount; i++) {
            int from = i * subsection;
            int to = (i == simulationContext.threadCount - 1) ? totalParticles - 1 : from + subsection;

            calculatePressureTasks.add(new CalculatePressureTask(from, to));
        }
        executorService.invokeAll(calculatePressureTasks);



        calculateForce();
        //could do parallel  as calculateForce() task at the end of the thread's calculation work.
        for (Particle particle : particles) particle.move(simulationContext);
    }
}
