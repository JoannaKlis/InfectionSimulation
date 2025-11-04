package model;

import java.util.Random;

import implementation.HealthState;
import implementation.Vector2D;

public class Individual {
    private static final double MAX_SPEED = 2.5;
    private static final Random random = new Random();

    // timery zakażenia w sekundach
    private static final double INFECTION_DURATION_MIN = 20.0;
    private static final double INFECTION_DURATION_MAX = 30.0;

    // Wektor pozycji (w metrach)
    private Vector2D position;
    // Wektor prędkości (w m/s)
    private Vector2D velocity;

    // logika zdrowia
    private HealthState healthState;
    private double infectionTimer = 0.0;
    private double requiredInfectionTime;

    // konstruktor inicjalizacji osobnika na losowej pozycji
    public Individual(double x, double y) {
        this.position = new Vector2D(x, y);
        this.velocity = new Vector2D(0, 0); // początkowo 0, póżniej losowa
        setRandomVelocity();
        this.healthState = HealthState.SUSCEPTIBLE_HEALTHY;
    }

    // ustawienie stanu zdrowia
    public void setHealthState(HealthState newState) {
        this.healthState = newState;

        if (isCurrentlyInfected()) {
            this.infectionTimer = 0.0;
            this.requiredInfectionTime = INFECTION_DURATION_MIN + random.nextDouble() * (INFECTION_DURATION_MAX - INFECTION_DURATION_MIN);
        }
    }

    // aktualizacja timera zakażenia
    public void updateInfectionState(double deltaTime) {
        if (!isCurrentlyInfected()) {
            return;
        }

        infectionTimer += deltaTime;

        if (infectionTimer >= requiredInfectionTime) {
            // zakażony osobnik zdrowieje (uzyskuje odporność)
            System.out.println("  [ZDROWIE] Osobnik na pozycji (" + String.format("%.2f", position.getX()) +
                    ") wyzdrowiał i uzyskał odporność.");
            this.healthState = HealthState.IMMUNE;
            this.infectionTimer = 0.0;
        }
    }

    // metody pomocnicze do stanu zdrowia
    public HealthState getHealthState() { return healthState; }
    public boolean isImmune() { return healthState == HealthState.IMMUNE; }
    public boolean isSusceptibleHealthy() { return healthState == HealthState.SUSCEPTIBLE_HEALTHY; }
    public boolean isCurrentlyInfected() {
        return healthState == HealthState.INFECTED_ASYMPTOMATIC ||
                healthState == HealthState.INFECTED_SYMPTOMATIC;
    }
    public boolean isSymptomatic() { return healthState == HealthState.INFECTED_SYMPTOMATIC; }

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

    @Override
    public String toString() {
        String status;
        if (healthState == HealthState.IMMUNE) {
            status = "Odporny (IMN)";
        } else if (healthState == HealthState.SUSCEPTIBLE_HEALTHY) {
            status = "Zdrowy (SUS)";
        } else if (healthState == HealthState.INFECTED_ASYMPTOMATIC) {
            status = "**ZAKAŻONY** (ASY, " + String.format("%.1f/%.1f", infectionTimer, requiredInfectionTime) + "s)";
        } else {
            status = "**ZAKAŻONY** (SYM, " + String.format("%.1f/%.1f", infectionTimer, requiredInfectionTime) + "s)";
        }

        return String.format("Pos: (%.2f, %.2f)m | Speed: %.2f m/s | Status: %s",
                position.getX(), position.getY(), velocity.abs(), status);
    }
}
