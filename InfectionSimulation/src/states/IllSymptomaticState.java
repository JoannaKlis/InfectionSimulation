package states;

import constants.SimulationConstants;
import interfaces.InfectionState;
import models.InfectionStatus;
import models.Person;

public class IllSymptomaticState implements InfectionState {
    private int illnessDurationSteps;

    // konstruktor przy pierwszym zakażeniu
    public IllSymptomaticState() {
        this.illnessDurationSteps = SimulationConstants.getRandomIllnessDurationSteps();
    }

    // konstruktor do odtworzenie stanu
    public IllSymptomaticState(int remainingSteps) {
        if (remainingSteps > 0) {
            this.illnessDurationSteps = remainingSteps;
        } else {
            this.illnessDurationSteps = SimulationConstants.getRandomIllnessDurationSteps();
        }
    }

    @Override
    public InfectionStatus getStatus() {
        return InfectionStatus.SUSCEPTIBLE_ILL_SYMPTOMATIC;
    }

    @Override
    public void update(Person person) {
        illnessDurationSteps--;

        if (illnessDurationSteps <= 0) {
            // zmiana na stan odporności po wyzdrowieniu
            person.setState(new HealthyImmuneState());
        }
    }

    @Override
    public void infect(Person person, boolean hasSymptoms) {
        // już chory
    }

    @Override
    public int getRemainingSteps() {
        return illnessDurationSteps;
    }
}