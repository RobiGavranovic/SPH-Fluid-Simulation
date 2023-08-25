package sph.sphfluidsimulation;

import java.util.List;

public class CalculatePressureTask implements Runnable {
    double density = Physics.density;
    List<Particle> subParticles;

    //sublist that a single thread works on
    public CalculatePressureTask(int from, int to) {
        subParticles = SphController.particles.subList(from, to);
    }

    @Override
    public void run() {
        //calculatePressure: Calculates the pressure for each particle in the simulation based on its density.
        //credit: Mitchell Sayer
        for (Particle particle : subParticles) {
            if (particle.density < density) particle.density = density;
            particle.pressure = particle.density - density;
        }
    }
}
