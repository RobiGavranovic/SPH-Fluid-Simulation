package sph.sphfluidsimulation;

import java.util.List;
import java.util.concurrent.Callable;

public class UpdateGridsTask implements Callable<Void> {
    int from, to;
    int gridSize;
    double width, height;

    public UpdateGridsTask(int from, int to, SimulationContext simulationContext, Physics physics) {
        this.from = from;
        this.to = to;
        this.gridSize = Physics.gridSize;
        this.width = simulationContext.width;
        this.height = simulationContext.height;
    }

    @Override
    public Void call() {
        //updateGrids: updates grids used for particle interaction
        //credit: Mitchell Sayer
        List<Particle> subParticles = SphController.particles.subList(this.from, this.to);
        for (Particle particle : subParticles) {
            particle.forceX = particle.forceY = particle.density = 0;
            particle.gridX = (int) Math.floor(particle.x / gridSize);
            particle.gridY = (int) Math.floor(particle.y / gridSize);
            if (particle.gridX < 0) particle.gridX = 0;
            if (particle.gridY < 0) particle.gridY = 0;
            if (particle.gridX > (width / gridSize) - 1)
                particle.gridX = (int) (width / gridSize) - 1;
            if (particle.gridY > (height / gridSize) - 1)
                particle.gridY = (int) (height / gridSize) - 1;
            SphController.grid[particle.gridY][particle.gridX].addParticle(particle);
        }
        return null;
    }
}
