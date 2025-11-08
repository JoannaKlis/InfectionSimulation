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
        // Osoba odporna nie zmienia stanu samoczynnie (można by dodać utratę odporności)
    }

    @Override
    public void infect(Person person, boolean hasSymptoms) {
        // Już odporny, nie można zakazić
    }
}