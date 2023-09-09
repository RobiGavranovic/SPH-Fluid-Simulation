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
<<<<<<< HEAD:src/main/java/sph/sphfluidsimulation/CalculatePressureTask.java
        //credit: Mitchell Sayer: https://github.com/mitchellsayer/Smoothed-Particle-Hydrodynamics/blob/master
=======
        //credit: Mitchell Sayer
>>>>>>> b90b9e376493bce5cd9d3d59236455ba705d2efe:src/main/java/sph/sphfluidsimulation/calculatePressureTask.java
        for (Particle particle : subParticles) {
            if (particle.density < density) particle.density = density;
            particle.pressure = particle.density - density;
        }
        return null;
    }
}
