package sph.sphfluidsimulation;

public class Neighbor {
    Particle particleA, particleB;
    double distance, weight;
    double nx, ny;

    /*
    setParticle: sets the two particles and calculates the properties of the neighbor relationship between them.

    Parameters:
   - particleA (Particle): The first particle in the neighbor relationship.
   - particleB (Particle): The second particle in the neighbor relationship.
     */
    //credit: Mitchell Sayer: https://github.com/mitchellsayer/Smoothed-Particle-Hydrodynamics/blob/master/Neighbor.java
    public void setNeighbor(Particle particleA, Particle particleB) {
        this.particleA = particleA;
        this.particleB = particleB;
        this.nx = particleA.x - particleB.x;
        this.ny = particleA.y - particleB.y;
        this.distance = Math.sqrt(Math.pow(nx, 2) + Math.pow(ny, 2));
        this.weight = 1 - this.distance / Physics.range;
        double tmp = Math.pow(this.weight, 3);
        particleA.density += tmp;
        particleB.density += tmp;
        if (this.distance != 0) {
            tmp = 1 / this.distance;
            this.nx *= tmp;
            this.ny *= tmp;
        }
    }

    //calculateForce: calculates the forces between the two particles in the neighbor relationship.
    //credit: Mitchell Sayer: https://github.com/mitchellsayer/Smoothed-Particle-Hydrodynamics/blob/master/Neighbor.java
    public void calculateForce() {
        double pressureWeight = this.weight * (particleA.pressure + particleB.pressure) / (particleA.density + particleB.density) * Physics.airPressure;
        //exploding hotfix
        pressureWeight = 0.72 * pressureWeight;
        double viscosityWeight = this.weight / (particleA.density + particleB.density) * Physics.viscosity;
        particleA.forceX += this.nx * pressureWeight;
        particleA.forceY += this.ny * pressureWeight;
        particleB.forceX -= this.nx * pressureWeight;
        particleB.forceY -= this.ny * pressureWeight;
        double rvx = particleB.velocityX - particleA.velocityX;
        double rvy = particleB.velocityY - particleA.velocityY;
        particleA.forceX += rvx * viscosityWeight;
        particleA.forceY += rvy * viscosityWeight;
        particleB.forceX -= rvx * viscosityWeight;
        particleB.forceY -= rvy * viscosityWeight;
    }


}
