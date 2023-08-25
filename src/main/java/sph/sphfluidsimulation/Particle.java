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

    public Particle(double x, double y, double radius) {
        this.x = x;
        this.y = y;
        this.setRadius(radius);
        this.setCenterX(x);
        this.setCenterY(Physics.height - y);
        this.setFill(Color.CADETBLUE);

        velocityX = random.nextInt(1 + 1) - 1;
        velocityY = random.nextInt(1 + 1) - 1;;
    }

    //move: Updates the position and velocity of the particle based on the applied forces and other conditions.
    //credit: Mitchell Sayer: https://github.com/mitchellsayer/Smoothed-Particle-Hydrodynamics/blob/master/Particle.java
    public void move() {
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
        if (this.x > Physics.width)
            this.velocityX += (Physics.width - this.x) * 0.5 - this.velocityX * 0.5;
        if (this.y > Physics.height)
            this.velocityY += (Physics.height - this.y) * 0.3 - this.velocityY * 0.3;

        setCenterX(this.x);
        setCenterY(Physics.height - this.y - 3); // 3 is number from trial and error -> issue: particles were going through the bottom a bit for some reason
    }
}
