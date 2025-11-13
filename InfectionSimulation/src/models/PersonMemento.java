package models;

import implementation.Vector2D;

// pamiątka dla pojedynczego osobnika
public class PersonMemento {
    public final Vector2D position;
    public final Vector2D velocity;
    public final InfectionStatus status;
    public final int illnessDurationSteps;
    public final boolean shouldBeRemoved;

    public PersonMemento(Vector2D position, Vector2D velocity, InfectionStatus status, int illnessDurationSteps, boolean shouldBeRemoved) {
        // wektor jest traktowany jako immutable
        this.position = new Vector2D(position.getComponents()[0], position.getComponents()[1]);
        this.velocity = new Vector2D(velocity.getComponents()[0], velocity.getComponents()[1]);
        this.status = status;
        this.illnessDurationSteps = illnessDurationSteps;
        this.shouldBeRemoved = shouldBeRemoved;
    }
}