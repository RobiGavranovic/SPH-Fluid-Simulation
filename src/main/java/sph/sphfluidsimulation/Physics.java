package sph.sphfluidsimulation;

import javafx.scene.layout.Pane;
import java.util.ArrayList;
import java.util.List;

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

    //updateGrids: Clears and updates grids used for particle interaction
    static void updateGrids() {
        for (Grid[] grids : SphController.grid) for (Grid grid : grids) grid.clearGrid();

        for (Particle particle : SphController.particles) {
            particle.forceX = particle.forceY = particle.density = 0;
            particle.gridX = (int) Math.floor(particle.x / gridSize);
            particle.gridY = (int) Math.floor(particle.y / gridSize);
            if (particle.gridX < 0) particle.gridX = 0;
            if (particle.gridY < 0) particle.gridY = 0;
            if (particle.gridX > (Physics.width / gridSize) - 1) particle.gridX = (int)(Physics.width / gridSize) - 1;
            if (particle.gridY > (Physics.height / gridSize) - 1) particle.gridY = (int)(Physics.height / gridSize)- 1;
            SphController.grid[particle.gridY][particle.gridX].addParticle(particle);
        }
    }

    //findNeighbors: Clears previous neighbors list and finds current neighbors for each particle in the simulation
    static void findNeighbors() {
        neighbors.clear();
        numberOfNeighbors = 0;
        
        for (Particle particle : SphController.particles) {
            int gridX = particle.gridX;
            int gridY = particle.gridY;

            findNeighborsInGrid(particle, SphController.grid[gridY][gridX]);
            try {
                int maxX = (int) (Physics.width / gridSize) - 1;
                int maxY = (int) (Physics.height / gridSize) - 1;

                if (gridX < maxX) findNeighborsInGrid(particle, SphController.grid[gridY][gridX + 1]);
                if (gridY > 0) findNeighborsInGrid(particle, SphController.grid[gridY - 1][gridX]);
                if (gridX > 0) findNeighborsInGrid(particle, SphController.grid[gridY][gridX - 1]);
                if (gridY < maxY) findNeighborsInGrid(particle, SphController.grid[gridY + 1][gridX]);
                if (gridX > 0 && gridY > 0) findNeighborsInGrid(particle, SphController.grid[gridY - 1][gridX - 1]);
                if (gridX > 0 && gridY < maxY) findNeighborsInGrid(particle, SphController.grid[gridY + 1][gridX - 1]);
                if (gridX < maxX && gridY > 0) findNeighborsInGrid(particle, SphController.grid[gridY - 1][gridX + 1]);
                if (gridX < maxX && gridY < maxY) findNeighborsInGrid(particle, SphController.grid[gridY + 1][gridX + 1]);
            } catch (ArrayIndexOutOfBoundsException e) {
            }
        }
    }

    /*
    findNeighborsInGrid: Finds neighboring particles within a specific grid cell for a given particle.

    Parameters:
    - Particle (Particle): Particle within the grid cell
    - gridCell (Grid): Grid cell that contains particle
     */
    static void findNeighborsInGrid(Particle particle, Grid gridCell) {
        for (Particle particleA : gridCell.getParticlesInGrid()) {
            if (particle.equals(particleA)) continue;
            double distance = Math.pow(particle.x - particleA.x, 2) + Math.pow(particle.y - particleA.y, 2);
            if (distance < range * range) {
                if (neighbors.size() == numberOfNeighbors) neighbors.add(new Neighbor());
                neighbors.get(numberOfNeighbors++).setParticle(particle, particleA);
            }
        }
    }

    //calculatePressure: Calculates the pressure for each particle in the simulation based on its density.
    public static void calculatePressure() {
        for (Particle particle : SphController.particles) {
            if (particle.density < density) particle.density = density;
            particle.pressure = particle.density - density;
        }
    }

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
        updateGrids();
        findNeighbors();
        calculatePressure();
        calculateForce();
        for (Particle particle : particles) particle.move();
    }

}
