package sph.sphfluidsimulation;

import javafx.scene.layout.Pane;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class Physics {
    public static final double range = 12;
    public static final double gravity = 0.55;
    public static final double airPressure = 1;
    public static final double viscosity = 0.07;
    public static final double density = 1;

    public static double width = SphApplication.scene.getWidth();
    public static double height = 0;
    public static int gridSize = 20;

    public static int numberOfNeighbors = 0;
    static ArrayList<Neighbor> neighbors = new ArrayList<>();
    private static final Object NEIGHBORS_LOCK = new Object();

    public static int numOfNewThreads = SphApplication.numOfNewThreads;
    public static ExecutorService executorService = Executors.newFixedThreadPool(numOfNewThreads);

    public static void mergeNeighbor(List<Neighbor> subNeighbor) {
        synchronized (NEIGHBORS_LOCK) {
            neighbors.addAll(subNeighbor);
            numberOfNeighbors += subNeighbor.size();
        }
    }

    //could do parallel
    //calculateForce: Calculates the forces between particles based on their neighboring relationships.
    public static void calculateForce() {
        for (Neighbor neighbor : neighbors) neighbor.calculateForce();
    }

    /*
    drawParticles: Adds particles to the pane.

    Parameters:
    - pane (Pane): Display pane
    - particles (List<Particle>): List of particles within the simulation
     */
    public static void drawParticles(Pane pane, List<Particle> particles) {
        pane.getChildren().clear();
        for (Particle particle : particles) pane.getChildren().add(particle);
    }

    public static void moveParticles(List<Particle> particles) {
        int totalParticles = SphController.particles.size();
        int subsection = totalParticles / numOfNewThreads;

        List<Callable<Void>> updateGridTasks = new ArrayList<>();
        List<Callable<Void>> findNeighborsTasks = new ArrayList<>();
        List<Callable<Void>> calculatePressureTasks = new ArrayList<>();

        for (int i = 0; i < numOfNewThreads; i++) {
            int from = i * subsection;
            int to = (i == numOfNewThreads - 1) ? totalParticles - 1 : from + subsection;

            updateGridTasks.add(() -> {
                new UpdateGridsTask(from, to).run();
                return null;
            });

            findNeighborsTasks.add(() -> {
                new FindNeighborsTask(from, to).run();
                return null;
            });

            calculatePressureTasks.add(() -> {
                new CalculatePressureTask(from, to).run();
                return null;
            });
        }

        //clear grid for a new iteration
        for (Grid[] grids : SphController.grid) for (Grid grid : grids) grid.clearGrid();

        try {
            executorService.invokeAll(updateGridTasks);
            //clear neighbors for a new iteration
            neighbors.clear();
            executorService.invokeAll(findNeighborsTasks);
            executorService.invokeAll(calculatePressureTasks);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        calculateForce();
        //could do parallel  as calculateForce() task at the end of the thread's calculation work.
        for (Particle particle : particles) particle.move();
    }
}
