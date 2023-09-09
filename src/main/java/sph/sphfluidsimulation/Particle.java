package sph.sphfluidsimulation;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import static sph.sphfluidsimulation.SphController.random;

public class Particle extends Circle {
    public double velocityX, velocityY;
    public double x, y;
    public int gridX, gridY;
    public double forceX, forceY;
    public double pressure, density;

    public Particle(double x, double y, double radius, SimulationContext simulationContext) {
        this.x = x;
        this.y = y;
        this.setRadius(radius);
        this.setCenterX(x);
        this.setCenterY(simulationContext.height - y);
        this.setFill(Color.CADETBLUE);

        velocityX = random.nextInt(1 + 1) - 1;
        velocityY = random.nextInt(1 + 1) - 1;
<<<<<<< HEAD
=======
        ;
>>>>>>> b90b9e376493bce5cd9d3d59236455ba705d2efe
    }

    //move: Updates the position and velocity of the particle based on the applied forces and other conditions.
    //credit: Mitchell Sayer: https://github.com/mitchellsayer/Smoothed-Particle-Hydrodynamics/blob/master/Particle.java
    public void move(SimulationContext simulationContext) {
        double radius = this.getRadius();
        this.velocityY -= Physics.gravity;
        this.velocityX += this.forceX;
        this.velocityY += this.forceY;
        this.x += this.velocityX;
        this.y += this.velocityY;

        if (this.x < 0)
            this.velocityX += (radius - this.x) * 0.5 - this.velocityX * 0.5;
        if (this.y < 0)
            this.velocityY += (radius - this.y) * 0.5 - this.velocityY * 0.5;
        if (this.x > simulationContext.width)
            this.velocityX += (simulationContext.width - this.x) * 0.5 - this.velocityX * 0.5;
        if (this.y > simulationContext.height)
            this.velocityY += (simulationContext.height - this.y) * 0.5 - this.velocityY * 0.5;

        setCenterX(this.x);
<<<<<<< HEAD
        setCenterY(simulationContext.height - this.y);
=======
        setCenterY(simulationContext.height - this.y - 3); // 3 is number from trial and error -> issue: particles were going through the bottom a bit for some reason

>>>>>>> b90b9e376493bce5cd9d3d59236455ba705d2efe
    }
}
