package sph.sphfluidsimulation;

import java.util.ArrayList;
import java.util.List;

public class findNeighborsTask implements Runnable {
    int gridSize;
    double range;
    List<Particle> subParticles;
    ArrayList<Neighbor> subNeighbor = new ArrayList<>();

    public findNeighborsTask(int from, int to) {
        subParticles = SphController.particles.subList(from, to);
    }

    @Override
    public void run() {
        //findNeighbors: finds current neighbors for each particle in the sublist
        this.gridSize = Physics.gridSize;
        this.range = Physics.range;

        for (Particle particle : subParticles) {
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
                if (gridX < maxX && gridY < maxY)
                    findNeighborsInGrid(particle, SphController.grid[gridY + 1][gridX + 1]);
            } catch (ArrayIndexOutOfBoundsException e) {
            }
        }
        Physics.mergeNeighbor(subNeighbor);
    }

    /*
    findNeighborsInGrid: Finds neighboring particles within a specific grid cell for a given particle.

    Parameters:
    - Particle (Particle): Particle within the grid cell
    - gridCell (Grid): Grid cell that contains particle
     */
    void findNeighborsInGrid(Particle particle, Grid gridCell) {
        for (Particle particleA : gridCell.getParticlesInGrid()) {
            if (particle.equals(particleA)) continue;
            double distance = Math.pow(particle.x - particleA.x, 2) + Math.pow(particle.y - particleA.y, 2);
            if (distance < range * range) {
                Neighbor newNeighbor = new Neighbor();
                newNeighbor.setParticle(particle, particleA);
                subNeighbor.add(newNeighbor);
            }
        }
    }
}
