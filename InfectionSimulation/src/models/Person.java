package models;

import constants.AreaConstants;
import constants.SimulationConstants;
import implementation.Vector2D;
import interfaces.InfectionState;
import states.HealthyImmuneState;
import states.HealthySusceptibleState;

import java.util.concurrent.ThreadLocalRandom;

public class Person {
    private Vector2D position;
    private Vector2D velocity;
    private InfectionState currentState;
    private boolean shouldBeRemoved = false;

    // konstruktor dla i populacji początkowej i nowych wkraczających osobników
    public Person(Vector2D initialPosition, InfectionStatus initialStatus) {
        this.position = initialPosition;
        this.velocity = new Vector2D(0, 0);

        // stan początkowy na podstawie podanego InfectionStatus
        this.currentState = stateFromStatus(initialStatus, 0);

        setRandomVelocity();
    }

    // konstruktor dla wzorca pamiątka do odtworzenia stanu
    public Person(PersonMemento memento) {
        this.position = memento.position;
        this.velocity = memento.velocity;
        this.shouldBeRemoved = memento.shouldBeRemoved;
        this.currentState = stateFromStatus(memento.status, memento.illnessDurationSteps);

        setRandomVelocity();
    }

    // metoda pomocnicza do tworzenia stanu z InfectionStatus (dla konstruktora i pamiątki-memento)
    private static InfectionState stateFromStatus(InfectionStatus status, int illnessDurationSteps) {
        switch (status) {
            case HEALTHY_IMMUNE:
                return new HealthyImmuneState();
            case SUSCEPTIBLE_ILL_ASYMPTOMATIC:
                // użycie konstruktora stanów ILL z czasem trwania (dla pamiątki)
                return new states.IllAsymptomaticState(illnessDurationSteps);
            case SUSCEPTIBLE_ILL_SYMPTOMATIC:
                return new states.IllSymptomaticState(illnessDurationSteps);
            case SUSCEPTIBLE_HEALTHY:
            default:
                return new HealthySusceptibleState();
        }
    }

    // setter dla stanu, używanego przez obiekty stanu
    public void setState(InfectionState newState) {
        this.currentState = newState;
    }

    // tworzenie pamiątki
    public PersonMemento saveState() {
        return new PersonMemento(this.position, this.velocity, this.currentState.getStatus(),
                this.currentState.getRemainingSteps(), this.shouldBeRemoved);
    }

    // tworzenie nowego osobnika na wejściu
    public static Person createNewEntryPerson() {
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

        // dla wersji 2: możliwość bycia odpornym (zielony)
        if (SimulationConstants.SIMULATION_VERSION == 2) {
            if (ThreadLocalRandom.current().nextDouble() < SimulationConstants.NEW_PERSON_IMMUNE_PROBABILITY) {
                return new Person(position, InfectionStatus.HEALTHY_IMMUNE);
            }
        }

        // wrażliwi na zakażenie
        Person newPerson = new Person(position, InfectionStatus.SUSCEPTIBLE_HEALTHY);

        if (ThreadLocalRandom.current().nextDouble() < SimulationConstants.NEW_PERSON_INFECTION_PROBABILITY) {
            // 10% szans na bycie chorym (wywołanie metody zakażającej)
            boolean hasSymptoms = ThreadLocalRandom.current().nextBoolean();
            newPerson.infect(hasSymptoms);
        }

        return newPerson;
    }

    // osobnik dla populacji początkowej
    public static Person createInitialPerson() {
        double randX = ThreadLocalRandom.current().nextDouble(AreaConstants.N_WIDTH_METERS);
        double randY = ThreadLocalRandom.current().nextDouble(AreaConstants.M_HEIGHT_METERS);
        Vector2D position = new Vector2D(randX, randY);

        // wersja 1: zawsze wrażliwy (żółty)
        if (SimulationConstants.SIMULATION_VERSION == 1) {
            return new Person(position, InfectionStatus.SUSCEPTIBLE_HEALTHY);
        }
        // wersja 2: Cczęść może być odporna (zielony)
        else {
            if (ThreadLocalRandom.current().nextDouble() < SimulationConstants.INITIAL_IMMUNE_PROBABILITY) {
                return new Person(position, InfectionStatus.HEALTHY_IMMUNE);
            } else {
                return new Person(position, InfectionStatus.SUSCEPTIBLE_HEALTHY);
            }
        }
    }

    // aktualizacja stanu osobnika w jednym kroku
    public void update() {
        if (shouldBeRemoved) {
            return; // nie aktualizujemy usuniętych osobników
        }

        move();

        currentState.update(this);

        if (ThreadLocalRandom.current().nextInt(SimulationConstants.STEPS_PER_SECOND) == 0) {
            setRandomVelocity();
        }
    }

    // poruszanie się osobników
    private void move() {
        // obliczenie przesunięcia na dany krok
        double stepTime = 1.0 / SimulationConstants.STEPS_PER_SECOND;
        double dx = velocity.getComponents()[0] * stepTime;
        double dy = velocity.getComponents()[1] * stepTime;

        double newX = position.getComponents()[0] + dx;
        double newY = position.getComponents()[1] + dy;

        boolean hitBoundary = false;

        // sprawdzenie, czy osobnik wyszedł poza granicę X
        if (newX < 0 || newX > AreaConstants.N_WIDTH_METERS) {
            hitBoundary = true;
        }
        // sprawdzenie, czy osobnik wyszedł poza granicę Y
        if (newY < 0 || newY > AreaConstants.M_HEIGHT_METERS) {
            hitBoundary = true;
        }

        if (hitBoundary) {
            // logika wychodzenia/odbijania od granicy (50% szans)
            if (ThreadLocalRandom.current().nextDouble() < 0.5) {
                // 50% szans: osobnik opuszcza obszar -> jest usuwany
                this.shouldBeRemoved = true;
                return;
            } else {
                // 50% szans: odbicie i powrót do obszaru

                // korygowanie pozycji do granicy (aby osobnik nie utknął poza granicą)
                newX = Math.max(0, Math.min(AreaConstants.N_WIDTH_METERS, newX));
                newY = Math.max(0, Math.min(AreaConstants.M_HEIGHT_METERS, newY));

                // odwrócenie składowej prędkości
                if (position.getComponents()[0] <= 0 || position.getComponents()[0] >= AreaConstants.N_WIDTH_METERS) {
                    velocity = new Vector2D(-velocity.getComponents()[0], velocity.getComponents()[1]);
                }
                if (position.getComponents()[1] <= 0 || position.getComponents()[1] >= AreaConstants.M_HEIGHT_METERS) {
                    velocity = new Vector2D(velocity.getComponents()[0], -velocity.getComponents()[1]);
                }

                setRandomVelocity(); // losowa zmiana kierunku po odbiciu
            }
        }

        position = new Vector2D(newX, newY);
    }

    // losowa prędkość
    private void setRandomVelocity() {
        double maxSpeed = SimulationConstants.MAX_SPEED_M_PER_S;
        double speed = ThreadLocalRandom.current().nextDouble(maxSpeed * 0.5, maxSpeed);
        double angle = ThreadLocalRandom.current().nextDouble(0, 2 * Math.PI);

        double vx = speed * Math.cos(angle);
        double vy = speed * Math.sin(angle);
        this.velocity = new Vector2D(vx, vy);
    }

    // czy osobnik ma objawy (logika zakażeń)
    public void infect(boolean hasSymptoms) {
        currentState.infect(this, hasSymptoms);
    }

    // losowe przypisane objawów dla nowo zakażonego osobnika
    public void infect() {
        boolean hasSymptoms = ThreadLocalRandom.current().nextBoolean();
        currentState.infect(this, hasSymptoms);
    }

    // zmiana status na wrażliwy i zdrowy
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

    public InfectionStatus getStatus() {
        return currentState.getStatus();
    }

    public boolean shouldBeRemoved() {
        return shouldBeRemoved;
    }
}