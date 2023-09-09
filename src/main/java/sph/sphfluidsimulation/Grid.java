package sph.sphfluidsimulation;

import java.util.ArrayList;

public class Grid {

    private final ArrayList<Particle> particlesInGrid;

    public Grid() {
        particlesInGrid = new ArrayList<>();
    }

    public void addParticle(Particle particle) {
        if (!particlesInGrid.contains(particle)) particlesInGrid.add(particle);
    }

    public void clearGrid() {
        particlesInGrid.clear();
    }

    public ArrayList<Particle> getParticlesInGrid() {
        return particlesInGrid;
    }
}
