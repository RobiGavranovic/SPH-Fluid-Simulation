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

    static void updateGrids() {
        for (Grid[] grids : SphController.grid) for (Grid grid : grids) grid.clearGrid();

        for (Particle particle : SphController.particles) {
            particle.forceX = particle.forceY = particle.density = 0;
            particle.gridX = (int) Math.floor(particle.x / gridSize);
            particle.gridY = (int) Math.floor(particle.y / gridSize);
            if (particle.gridX < 0) particle.gridX = 0;
            if (particle.gridY < 0) particle.gridY = 0;
            if (particle.gridX > (Physics.width / gridSize) - 2) particle.gridX = (int)(Physics.width / gridSize) - 2;
            if (particle.gridY > (Physics.height / gridSize) - 2) particle.gridY = (int)(Physics.height / gridSize) - 2;
            SphController.grid[particle.gridY][particle.gridX].addParticle(particle);
        }
    }

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

    public static void calculatePressure() {
        for (Particle particle : SphController.particles) {
            if (particle.density < density) particle.density = density;
            particle.pressure = particle.density - density;
        }
    }

    public static void calculateForce() {
        for (Neighbor neighbor : neighbors) neighbor.calculateForce();
    }


    public static void drawParticles(Pane pane, List<Particle> particles) {
        pane.getChildren().clear();
        for (Particle particle : particles) pane.getChildren().add(particle);
    }

    public static void moveParticles(List<Particle> particles) {
        SphController.updateGridSize();
        updateGrids();
        findNeighbors();
        calculatePressure();
        calculateForce();
        if (particles != null) for (Particle particle : particles) particle.move();
    }

}
