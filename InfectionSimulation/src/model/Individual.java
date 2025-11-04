package model;

import java.util.Random;
import implementation.Vector2D;

public class Individual {
    private static final double MAX_SPEED = 2.5;
    private static final Random random = new Random();

    // Wektor pozycji (w metrach)
    private Vector2D position;
    // Wektor prędkości (w m/s)
    private Vector2D velocity;

    private boolean isInfected;

    // konstruktor inicjalizacji osobnika na losowej pozycji
    public Individual(double x, double y, boolean isInfected) {
        this.position = new Vector2D(x, y);
        this.velocity = new Vector2D(0, 0); // początkowo 0, póżniej losowa
        this.isInfected = isInfected;
        setRandomVelocity();
    }

    // losowa prędkość z limitem MAX_SPEED
    private void setRandomVelocity() {
        double speed = random.nextDouble() * MAX_SPEED; // losowa szybkość (0, 2.5]
        double angle = random.nextDouble() * 2 * Math.PI; // losowy kierunek (0, 2*PI)

        double vx = speed * Math.cos(angle);
        double vy = speed * Math.sin(angle);

        this.velocity.setComponents(vx, vy);
        limitSpeed();
    }

    // ograniczenie prędkości
    private void limitSpeed() {
        double currentSpeed = velocity.abs();
        if (currentSpeed > MAX_SPEED) {
            // skalowanie wektora, aby prędkość wynosiła dokładnie MAX_SPEED
            double scaleFactor = MAX_SPEED / currentSpeed;
            velocity.scale(scaleFactor);
        }
    }

    // zmiana pozycji osobnika
    public void updatePosition(double deltaTime) {
        // obliczenie wektora przesunięcia: DeltaP = V * DeltaT
        Vector2D deltaP = velocity.copy().scale(deltaTime);

        // aktualizacja pozycji: P_new = P_old + DeltaP
        this.position.add(deltaP);
    }

    // losowa, niewielka zmiana w komponentach prędkości (np. -0.1 do 0.1)
    public void applyRandomChange() {
        double deltaVx = (random.nextDouble() * 0.2) - 0.1;
        double deltaVy = (random.nextDouble() * 0.2) - 0.1;

        double newVx = velocity.getX() + deltaVx;
        double newVy = velocity.getY() + deltaVy;

        velocity.setComponents(newVx, newVy);

        limitSpeed();
    }

    // sprawdzenie czy osobnik dotknął granicy obszaru
    public int checkBoundary(double areaWidth, double areaHeight) {
        if (position.getX() <= 0 || position.getX() >= areaWidth) {
            return 1;
        }
        if (position.getY() <= 0 || position.getY() >= areaHeight) {
            return 2;
        }
        return 0;
    }

    // zawracanie osobnika do środka obszaru
    public void turnBack(int boundaryAxis) {
        if (boundaryAxis == 1) { // granica X
            velocity.setComponents(-velocity.getX(), velocity.getY());
        } else if (boundaryAxis == 2) { // granica Y
            velocity.setComponents(velocity.getX(), -velocity.getY());
        }
    }

    // gettery i settery
    public Vector2D getPosition() { return position; }
    public boolean isInfected() { return isInfected; }
    public void setInfected(boolean infected) { this.isInfected = infected; }

    @Override
    public String toString() {
        String status = isInfected ? "**ZAKAŻONY**" : "Zdrowy";
        return String.format("Pozycja: (%.2f, %.2f)m | Prędkość: %.2f m/s | Status: %s",
                position.getX(), position.getY(), velocity.abs(), status);
    }
}
