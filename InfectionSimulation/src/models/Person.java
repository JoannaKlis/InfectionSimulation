package models;

import constants.AreaConstants;
import constants.SimulationConstants;
import implementation.Vector2D;
import interfaces.InfectionState;
import states.HealthySusceptibleState;

import java.util.concurrent.ThreadLocalRandom;

public class Person {
    private Vector2D position;
    private Vector2D velocity;
    // Zmieniamy z InfectionStatus na InfectionState
    private InfectionState currentState;

    // Usuwamy zmienne zarządzane przez stany:
    // private int illnessDurationSteps;
    // private int stepsSinceInfection;

    public Person(Vector2D initialPosition, InfectionStatus initialStatus) {
        this.position = initialPosition;
        this.velocity = new Vector2D(0, 0);

        // Zastąpienie bezpośredniego ustawienia statusu, stanem początkowym
        this.currentState = new HealthySusceptibleState(); // Domyślny stan

        // Jeśli chcemy, aby initialStatus miało znaczenie, musimy to zaimplementować,
        // ale zgodnie z logiką 'createInitialPerson()' zawsze zaczynamy jako Susceptible.

        setRandomVelocity();
    }

    // Dodanie settera dla stanu, używanego przez obiekty stanu
    public void setState(InfectionState newState) {
        this.currentState = newState;
    }

    // tworzenie nowego osobnika na wejściu (modyfikacja logiki zakażeń)
    public static Person createNewEntryPerson() {
        // ... (Kod do wyboru granicy i pozycji bez zmian)
        double x;
        double y;
        int boundary = ThreadLocalRandom.current().nextInt(4);

        if (boundary == 0) { // góra (Y = M)
            x = ThreadLocalRandom.current().nextDouble(AreaConstants.N_WIDTH_METERS);
            y = AreaConstants.M_HEIGHT_METERS;
        } else if (boundary == 1) { // dół (Y = 0)
            x = ThreadLocalRandom.current().nextDouble(AreaConstants.N_WIDTH_METERS);
            y = 0;
        } else if (boundary == 2) { // lewo (X = 0)
            x = 0;
            y = ThreadLocalRandom.current().nextDouble(AreaConstants.M_HEIGHT_METERS);
        } else { // prawo (X = N)
            x = AreaConstants.N_WIDTH_METERS;
            y = ThreadLocalRandom.current().nextDouble(AreaConstants.M_HEIGHT_METERS);
        }

        Vector2D position = new Vector2D(x, y);

        // Tworzenie nowej osoby domyślnie ze stanem SUSCEPTIBLE_HEALTHY
        Person newPerson = new Person(position, InfectionStatus.SUSCEPTIBLE_HEALTHY);

        if (ThreadLocalRandom.current().nextDouble() < SimulationConstants.NEW_PERSON_INFECTION_PROBABILITY) {
            // 10% szans na bycie chorym - wywołanie metody zakażającej, która ustawi stan
            boolean hasSymptoms = ThreadLocalRandom.current().nextBoolean();
            newPerson.infect(hasSymptoms); // Użycie metody z nową sygnaturą
        }

        return newPerson;
    }

    // osobnik dla populacji początkowej (zdrowy i wrażliwy)
    public static Person createInitialPerson() {
        double randX = ThreadLocalRandom.current().nextDouble(AreaConstants.N_WIDTH_METERS);
        double randY = ThreadLocalRandom.current().nextDouble(AreaConstants.M_HEIGHT_METERS);
        Vector2D position = new Vector2D(randX, randY);

        // użycie konstruktora, który ustawia domyślny stan na HealthySusceptibleState
        return new Person(position, InfectionStatus.SUSCEPTIBLE_HEALTHY);
    }

    // aktualizacja stanu osobnika w jednym kroku (delegacja do stanu)
    public void update() {
        move();

        // Delegacja ewolucji stanu zakażenia do obiektu stanu
        currentState.update(this);

        // losowa zmiana kierunku co jakiś czas (np. co sekundę)
        if (ThreadLocalRandom.current().nextInt(SimulationConstants.STEPS_PER_SECOND) == 0) {
            setRandomVelocity();
        }
    }

    // ... (metoda move() bez zmian)
    private void move() {
        // obliczenie przesunięcia na dany krok (delta_t = 1 / STEPS_PER_SECOND)
        double stepTime = 1.0 / SimulationConstants.STEPS_PER_SECOND;
        double dx = velocity.getComponents()[0] * stepTime;
        double dy = velocity.getComponents()[1] * stepTime;

        double newX = position.getComponents()[0] + dx;
        double newY = position.getComponents()[1] + dy;

        // odbijanie od granic
        boolean bounced = false;
        if (newX < 0 || newX > AreaConstants.N_WIDTH_METERS) {
            velocity = new Vector2D(-velocity.getComponents()[0], velocity.getComponents()[1]);
            newX = Math.max(0, Math.min(AreaConstants.N_WIDTH_METERS, newX));
            bounced = true;
        }
        if (newY < 0 || newY > AreaConstants.M_HEIGHT_METERS) {
            velocity = new Vector2D(velocity.getComponents()[0], -velocity.getComponents()[1]);
            newY = Math.max(0, Math.min(AreaConstants.M_HEIGHT_METERS, newY));
            bounced = true;
        }

        position = new Vector2D(newX, newY);

        // jeśli się odbił, losowo zmieniamy też kierunek, żeby nie utknął w kącie
        if (bounced) {
            setRandomVelocity();
        }
    }

    // ... (metoda setRandomVelocity() bez zmian)
    private void setRandomVelocity() {
        double maxSpeed = SimulationConstants.MAX_SPEED_M_PER_S;
        double speed = ThreadLocalRandom.current().nextDouble(maxSpeed * 0.5, maxSpeed);
        double angle = ThreadLocalRandom.current().nextDouble(0, 2 * Math.PI);

        double vx = speed * Math.cos(angle);
        double vy = speed * Math.sin(angle);
        this.velocity = new Vector2D(vx, vy);
    }

    // Modyfikacja metody infect, aby używała stanu
    public void infect(boolean hasSymptoms) {
        // Delegacja logiki zakażenia do obiektu stanu
        currentState.infect(this, hasSymptoms);
    }

    // Modyfikacja metody infect, aby utrzymać kompatybilność z SimulationPanel.java
    public void infect() {
        // Losowe przypisanie objawów dla nowo zakażonego (jak w oryginalnej logice)
        boolean hasSymptoms = ThreadLocalRandom.current().nextBoolean();
        currentState.infect(this, hasSymptoms);
    }

    // Zmiana implementacji metod sprawdzających status (delegacja)
    public boolean isSusceptible() {
        return currentState.getStatus() == InfectionStatus.SUSCEPTIBLE_HEALTHY;
    }

    public boolean isIll() {
        return currentState.getStatus() == InfectionStatus.SUSCEPTIBLE_ILL_ASYMPTOMATIC ||
                currentState.getStatus() == InfectionStatus.SUSCEPTIBLE_ILL_SYMPTOMATIC;
    }

    public boolean hasSymptoms() {
        return currentState.getStatus() == InfectionStatus.SUSCEPTIBLE_ILL_SYMPTOMATIC;
    }

    // getry
    public Vector2D getPosition() {
        return position;
    }

    // Getry statusu zwracają teraz status z obiektu stanu
    public InfectionStatus getStatus() {
        return currentState.getStatus();
    }
}