package sph.sphfluidsimulation;

import javafx.application.Platform;
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

    ArrayList<Neighbor> neighbors;
    final Object NEIGHBORS_LOCK;

    public SimulationContext simulationContext;
    public ExecutorService executorService;
    CountDownLatch latch;

    public Physics(SimulationContext simulationContext) {
        this.simulationContext = simulationContext;

        neighbors = new ArrayList<>();
        NEIGHBORS_LOCK = new Object();

        executorService = Executors.newFixedThreadPool(simulationContext.threadCount);
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
        //force run on JavaFX thread
        Platform.runLater(() -> {
            int paneParticleCount = pane.getChildren().size();
            int particleCount = particles.size();
            //if new particles were added
            if (particleCount > paneParticleCount) {
                // add new particles to the pane
                for (int i = paneParticleCount; i < particleCount; i++) {
                    pane.getChildren().add(particles.get(i));
                }
            }
        });
    }

    public void moveParticles(List<Particle> particles, SimulationContext simulationContext) throws InterruptedException {
        //clear grid for a new iteration
        for (Grid[] grids : SphController.grid) for (Grid grid : grids) grid.clearGrid();

        //clear neighbors for a new iteration
        neighbors.clear();

        int totalParticles = SphController.particles.size();
        int subsection = totalParticles / this.simulationContext.threadCount;

        List<Callable<Void>> updateGridTasks = new ArrayList<>();
        List<Callable<Void>> calculatePressureTasks = new ArrayList<>();

        //make smaller chunks for threads
        for (int i = 0; i < simulationContext.threadCount; i++) {
            int from = i * subsection;
            int to = (i == simulationContext.threadCount - 1) ? totalParticles - 1 : from + subsection;

            updateGridTasks.add(new UpdateGridsTask(from, to, simulationContext));
        }
        executorService.invokeAll(updateGridTasks);

        //make smaller chunks for threads
        //latch -> make sure all threads have finished, before calculating pressure
        latch = new CountDownLatch(SphController.particles.size());
        for (Particle particle : particles) {
            executorService.submit(() -> {
                try {
                    new FindNeighborsTask(simulationContext, this, particle, neighbors).call();
                } finally {
                    // latch count-- when thread is done
                    latch.countDown();
                }
            });
        }
        latch.await();

        //make smaller chunks for threads
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