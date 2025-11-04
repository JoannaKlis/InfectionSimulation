package model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class SimulationArea {
    private static final Random random = new Random();

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
            individuals.add(new Individual(
                    random.nextDouble() * width,
                    random.nextDouble() * height,
                    false
            ));
        }

        System.out.println("Utworzono obszar " + width + "x" + height + "m z " + count + " osobnikami.");
    }

    // dodanie nowego osobnika na losowej granicy obszaru
    private void addNewIndividual() {
        int border = random.nextInt(4);
        double x, y;

        switch (border) {
            case 0: x = 0; y = random.nextDouble() * height; break; // lewa
            case 1: x = width; y = random.nextDouble() * height; break; // prawa
            case 2: x = random.nextDouble() * width; y = 0; break; // dolna
            case 3: x = random.nextDouble() * width; y = height; break; // górna
            default: return;
        }

        // prawd. zakażenia się wirusem (10%)
        boolean isInfected = random.nextDouble() < SimulationConstants.PROB_INFECTION;

        Individual newIndividual = new Individual(x, y, isInfected);
        individuals.add(newIndividual);
        System.out.println("  [NOWY OSOBNIK] wkracza na granicę: " + newIndividual);
    }

    // zarządzanie wkraczaniem nowych osobników
    private void managePopulationEntry(double deltaTime) {
        // jeśli populacja jest mniejsza niż docelowa, zwiększamy szansę na wkraczanie
        double baseEntryChance = SimulationConstants.ENTRY_FREQUENCY * deltaTime;

        // zwiększenie szansy wkraczania, jeśli populacja jest mała
        if (individuals.size() < SimulationConstants.TARGET_POPULATION) {
            baseEntryChance *= 2.0;
        }

        if (random.nextDouble() < baseEntryChance) {
            addNewIndividual();
        }
    }

    // jeden krok symulacji
    public void step(double deltaTime) {
        managePopulationEntry(deltaTime); // wprowadzanie nowych osobników

        // iterator do bezpiecznego usuwania elementów w trakcie iteracji
        Iterator<Individual> iterator = individuals.iterator();
        while (iterator.hasNext()) {
            Individual individual = iterator.next();

            individual.applyRandomChange(); // losowa zmiana prędkości
            individual.updatePosition(deltaTime); // aktualizacja pozycji

            // sprawdzenie granic i obsługa opuszczania/zawracania
            int boundaryAxis = individual.checkBoundary(width, height);
            if (boundaryAxis != 0) {
                // osobnik dotarł do granicy
                if (random.nextDouble() < SimulationConstants.PROB_TURN_BACK) {
                    // zawróć (50%)
                    individual.turnBack(boundaryAxis);
                    // odsuń lekko od granicy, aby nie opuścił w następnym kroku
                    individual.updatePosition(deltaTime * 0.1);
                } else {
                    // opuść obszar (50%)
                    System.out.println("  [OPUSZCZA] Osobnik opuścił obszar. Aktualna populacja: " + (individuals.size() - 1));
                    iterator.remove();
                }
            }
        }
    }

    // aktualny stan osobników
    public void displayState() {
        int infectedCount = (int) individuals.stream().filter(Individual::isInfected).count();
        System.out.println("\n--- STAN POPULACJI: " + individuals.size() + " ---");
        System.out.println("  Zakażeni: " + infectedCount + ", Zdrowi: " + (individuals.size() - infectedCount));
        for (int i = 0; i < individuals.size(); i++) {
            System.out.println("  Osobnik " + i + ": " + individuals.get(i));
        }
        System.out.println("--------------------------------\n");
    }
}
