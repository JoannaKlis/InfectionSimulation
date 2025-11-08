package states;

import constants.SimulationConstants;
import interfaces.InfectionState;
import models.InfectionStatus;
import models.Person;

public class IllAsymptomaticState implements InfectionState {
    private int illnessDurationSteps;

    public IllAsymptomaticState() {
        // Ustawienie czasu trwania choroby przy wejściu w stan
        this.illnessDurationSteps = SimulationConstants.getRandomIllnessDurationSteps();
    }

    @Override
    public InfectionStatus getStatus() {
        return InfectionStatus.SUSCEPTIBLE_ILL_ASYMPTOMATIC;
    }

    @Override
    public void update(Person person) {
        illnessDurationSteps--;

        if (illnessDurationSteps <= 0) {
            // Zmiana na stan odporności po wyzdrowieniu
            person.setState(new HealthyImmuneState());
        }
    }

    @Override
    public void infect(Person person, boolean hasSymptoms) {
        // Już chory, nie można zakazić ponownie
    }
}
