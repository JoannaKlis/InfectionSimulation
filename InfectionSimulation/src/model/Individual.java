package model;

import java.util.Random;

public class Individual {
    private static final double MAX_SPEED = 2.5;
    private static final Random random = new Random();

    // wymiary obszaru symulacji w metrach
    private double x;
    private double y;

    // prędkość w m/s
    private double vx;
    private double vy;

    // konstruktor inicjalizacji osobnika na losowej pozycji
    public Individual(double maxX, double maxY) {
        this.x = random.nextDouble() * maxX;
        this.y = random.nextDouble() * maxY;

        setRandomVelocity();
    }

    // losowa prędkość z limitem MAX_SPEED
    private void setRandomVelocity() {
        this.vx = (random.nextDouble() * 2 * MAX_SPEED) - MAX_SPEED;
        this.vy = (random.nextDouble() * 2 * MAX_SPEED) - MAX_SPEED;

        limitSpeed();
    }

    // ograniczenie prędkości
    private void limitSpeed() {
        double currentSpeed = Math.sqrt(vx * vx + vy * vy);
        if (currentSpeed > MAX_SPEED) {
            double scaleFactor = MAX_SPEED / currentSpeed;
            this.vx *= scaleFactor;
            this.vy *= scaleFactor;
        }
    }

    // zmiana pozycji osobnika
    public void updatePosition(double deltaTime, double areaWidth, double areaHeight) {
        this.x += this.vx * deltaTime;
        this.y += this.vy * deltaTime;

        // zmiana kierunku wzdłuż osi X
        if (x < 0 || x > areaWidth) {
            this.vx *= -1;
            if (x < 0) x = 0;
            if (x > areaWidth) x = areaWidth;
        }

        // zmiana kierunku wzdłuż osi Y
        if (y < 0 || y > areaHeight) {
            this.vy *= -1;
            if (y < 0) y = 0;
            if (y > areaHeight) y = areaHeight;
        }
    }

    public void applyRandomChange() {
        double deltaVx = (random.nextDouble() * 0.2) - 0.1;
        double deltaVy = (random.nextDouble() * 0.2) - 0.1;

        this.vx += deltaVx;
        this.vy += deltaVy;

        limitSpeed();
    }

    @Override
    public String toString() {
        return String.format("Pozycja: (%.2f, %.2f)m | Prędkość: %.2f m/s (vx:%.2f, vy:%.2f)",
                x, y, Math.sqrt(vx * vx + vy * vy), vx, vy);
    }
}
