package simulation;

import models.PersonMemento;
import java.util.List;

// pamiątka dla całej symulacji
public class SimulationMemento {
    private final int stepCounter;
    private final List<PersonMemento> populationMementos;

    public SimulationMemento(int stepCounter, List<PersonMemento> populationMementos) {
        this.stepCounter = stepCounter;
        this.populationMementos = populationMementos;
    }

    public int getStepCounter() {
        return stepCounter;
    }

    public List<PersonMemento> getPopulationMementos() {
        return populationMementos;
    }
}