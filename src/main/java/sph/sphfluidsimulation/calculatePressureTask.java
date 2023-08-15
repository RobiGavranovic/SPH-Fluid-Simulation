package sph.sphfluidsimulation;

import java.util.ArrayList;
import java.util.List;

public class calculatePressureTask implements Runnable {
    double density = Physics.density;
    List<Particle> subParticles;

    public calculatePressureTask(int from, int to) {
        subParticles = SphController.particles.subList(from, to);
    }

    @Override
    public void run() {
        //calculatePressure: Calculates the pressure for each particle in the simulation based on its density.
        for (Particle particle : subParticles) {
            if (particle.density < density) particle.density = density;
            particle.pressure = particle.density - density;
        }
    }
}
