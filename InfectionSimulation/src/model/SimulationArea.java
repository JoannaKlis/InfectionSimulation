package model;

import java.util.ArrayList;
import java.util.List;

public class SimulationArea {
    // wymiar obszaru
    private final double width;
    private final double height;
    // lista osobników
    private final List<Individual> individuals;

    // konstruktor obszaru z osobnikami
    public SimulationArea(double width, double height, int count) {
        this.width = width;
        this.height = height;
        this.individuals = new ArrayList<>();

        // inicjalizacja "i" osobników
        for (int i = 0; i < count; i++) {
            individuals.add(new Individual(width, height));
        }

        System.out.println("Utworzono obszar " + width + "x" + height + "m z " + count + " osobnikami.");
    }

    // jeden krok symulacji
    public void step(double deltaTime) {
        for (Individual individual : individuals) {
            // losowa zmiana prędkości i kierunku
            individual.applyRandomChange();

            // akutalizacja pozycji
            individual.updatePosition(deltaTime, width, height);
        }
    }

    public void displayState() {
        for (int i = 0; i < individuals.size(); i++) {
            System.out.println("Osobnik " + i + ": " + individuals.get(i));
        }
    }
}
