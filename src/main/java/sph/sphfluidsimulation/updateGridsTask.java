package sph.sphfluidsimulation;

import java.util.List;

public class UpdateGridsTask implements Runnable {
    int from, to;

    public UpdateGridsTask(int from, int to) {
        this.from = from;
        this.to = to;
    }

    @Override
    public void run() {
        //updateGrids: updates grids used for particle interaction
        //credit: Mitchell Sayer
        List<Particle> subParticles = SphController.particles.subList(this.from, this.to);
        for (Particle particle : subParticles) {
            particle.forceX = particle.forceY = particle.density = 0;
            particle.gridX = (int) Math.floor(particle.x / Physics.gridSize);
            particle.gridY = (int) Math.floor(particle.y / Physics.gridSize);
            if (particle.gridX < 0) particle.gridX = 0;
            if (particle.gridY < 0) particle.gridY = 0;
            if (particle.gridX > (Physics.width / Physics.gridSize) - 1)
                particle.gridX = (int) (Physics.width / Physics.gridSize) - 1;
            if (particle.gridY > (Physics.height / Physics.gridSize) - 1)
                particle.gridY = (int) (Physics.height / Physics.gridSize) - 1;
            SphController.grid[particle.gridY][particle.gridX].addParticle(particle);
        }
    }
}
