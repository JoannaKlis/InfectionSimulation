package models;

import implementation.Vector2D;

// Klasa Pamiątka (Memento) dla pojedynczego osobnika
public class PersonMemento {
    public final Vector2D position;
    public final Vector2D velocity;
    public final InfectionStatus status;
    public final int illnessDurationSteps; // Dodatkowy stan dla IllState
    public final boolean shouldBeRemoved;

    public PersonMemento(Vector2D position, Vector2D velocity, InfectionStatus status, int illnessDurationSteps, boolean shouldBeRemoved) {
        // Wektor jest traktowany jako immutable, więc kopia jest prosta
        this.position = new Vector2D(position.getComponents()[0], position.getComponents()[1]);
        this.velocity = new Vector2D(velocity.getComponents()[0], velocity.getComponents()[1]);
        this.status = status;
        this.illnessDurationSteps = illnessDurationSteps;
        this.shouldBeRemoved = shouldBeRemoved;
    }
}