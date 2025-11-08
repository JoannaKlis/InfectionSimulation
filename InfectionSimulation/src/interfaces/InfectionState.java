package interfaces;

import models.InfectionStatus;
import models.Person;

public interface InfectionState {
    InfectionStatus getStatus(); // zwraca stałą statusu osobnika
    void update(Person person); // aktualizacja stanu osobnika
    void infect(Person person, boolean hasSymptoms); // zakażenie osobnika
}