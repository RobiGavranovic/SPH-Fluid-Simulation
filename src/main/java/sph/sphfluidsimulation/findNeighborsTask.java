package sph.sphfluidsimulation;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class FindNeighborsTask implements Callable<Void> {
    int gridSize;
    double range;
    double width, height;

    //sub lists that a single thread works on
    ArrayList<Neighbor> subNeighbor = new ArrayList<>();
    SimulationContext simulationContext;
    Particle particle;
    List<Neighbor> neighbors;
    Physics physics;

    public FindNeighborsTask(SimulationContext simulationContext, Physics physics, Particle particle, List<Neighbor> neighbors) {
        this.simulationContext = simulationContext;
        this.range = Physics.range;
        this.gridSize = Physics.gridSize;
        this.width = simulationContext.width;
        this.height = simulationContext.height;
        this.particle = particle;
        this.neighbors = neighbors;
        this.physics = physics;
    }

    @Override
    //credit: Mitchell Sayer: https://github.com/mitchellsayer/Smoothed-Particle-Hydrodynamics/blob/master/
    public Void call() {
        //findNeighbors: finds current neighbors for each particle
        int gridX = particle.gridX;
        int gridY = particle.gridY;

        findNeighborsInGrid(particle, SphController.grid[gridY][gridX]);
        try {
            int maxX = (int) (width / gridSize) - 1;
            int maxY = (int) (height / gridSize) - 1;

            if (gridX < maxX) findNeighborsInGrid(particle, SphController.grid[gridY][gridX + 1]);
            if (gridY > 0) findNeighborsInGrid(particle, SphController.grid[gridY - 1][gridX]);
            if (gridX > 0) findNeighborsInGrid(particle, SphController.grid[gridY][gridX - 1]);
            if (gridY < maxY) findNeighborsInGrid(particle, SphController.grid[gridY + 1][gridX]);
            if (gridX > 0 && gridY > 0) findNeighborsInGrid(particle, SphController.grid[gridY - 1][gridX - 1]);
            if (gridX > 0 && gridY < maxY) findNeighborsInGrid(particle, SphController.grid[gridY + 1][gridX - 1]);
            if (gridX < maxX && gridY > 0) findNeighborsInGrid(particle, SphController.grid[gridY - 1][gridX + 1]);
            if (gridX < maxX && gridY < maxY)
                findNeighborsInGrid(particle, SphController.grid[gridY + 1][gridX + 1]);
        } catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
        }

        synchronized(physics.neighbors) {
            physics.neighbors.addAll(subNeighbor);
        } //synchronised writing
        return null;
    }

    /*
    findNeighborsInGrid: Finds neighboring particles within a specific grid cell for a given particle.

    Parameters:
    - Particle (Particle): Particle within the grid cell
    - gridCell (Grid): Grid cell that contains particle
     */
    //credit: Mitchell Sayer: https://github.com/mitchellsayer/Smoothed-Particle-Hydrodynamics/blob/master/
    void findNeighborsInGrid(Particle particle, Grid gridCell) {
        for (Particle particleA : gridCell.getParticlesInGrid()) {
            if (particle.equals(particleA)) continue;
            double distance = Math.pow(particle.x - particleA.x, 2) + Math.pow(particle.y - particleA.y, 2);
            if (distance < range * range) {
                Neighbor newNeighbor = new Neighbor();
                newNeighbor.setNeighbor(particle, particleA);
                subNeighbor.add(newNeighbor);
            }
        }
    }
}
