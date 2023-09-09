package sph.sphfluidsimulation;

import java.util.List;
import java.util.concurrent.Callable;

public class CalculatePressureTask implements Callable<Void> {
    double density = Physics.density;
    List<Particle> subParticles;

    //sublist that a single thread works on
    public CalculatePressureTask(int from, int to) {
        subParticles = SphController.particles.subList(from, to);
    }

    @Override
    public Void call() {
        //calculatePressure: Calculates the pressure for each particle in the simulation based on its density.
        //credit: Mitchell Sayer: https://github.com/mitchellsayer/Smoothed-Particle-Hydrodynamics/blob/master
        for (Particle particle : subParticles) {
            if (particle.density < density) particle.density = density;
            particle.pressure = particle.density - density;
        }
        return null;
    }
}
