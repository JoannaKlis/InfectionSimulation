package states;

import interfaces.InfectionState;
import models.InfectionStatus;
import models.Person;

public class HealthySusceptibleState implements InfectionState {

    @Override
    public InfectionStatus getStatus() {
        return InfectionStatus.SUSCEPTIBLE_HEALTHY;
    }

    @Override
    public void update(Person person) {
        // osobnik wrażliwy, zdrowa nie zmienia stanu samoczynnie
    }

    @Override
    public void infect(Person person, boolean hasSymptoms) {
        if (hasSymptoms) {
            person.setState(new IllSymptomaticState());
        } else {
            person.setState(new IllAsymptomaticState());
        }
    }

    @Override
    public int getRemainingSteps() {
        return 0;
    }
}