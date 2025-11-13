package states;

import interfaces.InfectionState;
import models.InfectionStatus;
import models.Person;

public class HealthyImmuneState implements InfectionState {

    @Override
    public InfectionStatus getStatus() {
        return InfectionStatus.HEALTHY_IMMUNE;
    }

    @Override
    public void update(Person person) {
        // osobnik odporny nie zmienia stanu samoczynnie
    }

    @Override
    public void infect(Person person, boolean hasSymptoms) {
        // już odporny, nie można zakazić
    }

    @Override
    public int getRemainingSteps() {
        return 0;
    }
}