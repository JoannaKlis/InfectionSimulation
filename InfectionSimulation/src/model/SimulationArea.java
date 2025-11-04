package model;

import implementation.HealthState;
import java.util.*;

public class SimulationArea {
    private static final Random random = new Random();

    // wymiar obszaru
    private final double width;
    private final double height;
    // lista osobników
    private final List<Individual> individuals;

    // deklaracja mapy: (Osobnik -> Czas Bliskości (Double))
    private final Map<Individual, Map<Individual, Double>> proximityTimers;

    // konstruktor obszaru z osobnikami
    public SimulationArea(double width, double height, int count) {
        this.width = width;
        this.height = height;
        this.individuals = new ArrayList<>();
        this.proximityTimers = new HashMap<>();

        // inicjalizacja "i" osobników
        for (int i = 0; i < count; i++) {
            Individual newIndividual = new Individual(
                    random.nextDouble() * width,
                    random.nextDouble() * height
            );
            // ustalenie początkowego stanu zdrowia
            if (i == 0) {
                newIndividual.setHealthState(HealthState.INFECTED_ASYMPTOMATIC);
            } else if (random.nextDouble() < 0.2) { // 20% odpornych na start
                newIndividual.setHealthState(HealthState.IMMUNE);
            } else {
                newIndividual.setHealthState(HealthState.SUSCEPTIBLE_HEALTHY);
            }
            individuals.add(newIndividual);
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

        Individual newIndividual = new Individual(x, y);

        // prawdopodobieństwo zakażenia się wirusem (10%) przy wkraczaniu
        if (random.nextDouble() < SimulationConstants.PROB_ENTRY_INFECTION) {
            // losowo z objawami lub bez
            HealthState state = random.nextBoolean() ?
                    HealthState.INFECTED_SYMPTOMATIC :
                    HealthState.INFECTED_ASYMPTOMATIC;
            newIndividual.setHealthState(state);
        } else {
            // 50% odpornych, 50% wrażliwych na wkraczaniu
            HealthState state = random.nextBoolean() ?
                    HealthState.IMMUNE :
                    HealthState.SUSCEPTIBLE_HEALTHY;
            newIndividual.setHealthState(state);
        }

        individuals.add(newIndividual);
        System.out.println("  [NOWY OSOBNIK] wkracza na granicę: " + newIndividual);
    }

    // zarządzanie wkraczaniem nowych osobników
    private void managePopulationEntry(double deltaTime) {
        double baseEntryChance = SimulationConstants.ENTRY_FREQUENCY * deltaTime;
        if (individuals.size() < SimulationConstants.TARGET_POPULATION) {
            baseEntryChance *= 2.0;
        }

        if (random.nextDouble() < baseEntryChance) {
            addNewIndividual();
        }
    }

    // odległość euklidesowa między dwoma osobnikami
    private double calculateDistance(Individual i1, Individual i2) {
        double dx = i1.getPosition().getX() - i2.getPosition().getX();
        double dy = i1.getPosition().getY() - i2.getPosition().getY();
        return Math.sqrt(dx * dx + dy * dy);
    }

    // logika przekazywania wirusa
    private void handleVirusTransmission(double deltaTime) {

        // aktualizacja mapy dla osobników, którzy opuścili obszar
        proximityTimers.keySet().retainAll(individuals);
        for (Map<Individual, Double> innerMap : proximityTimers.values()) {
            innerMap.keySet().retainAll(individuals);
        }

        for (int i = 0; i < individuals.size(); i++) {
            Individual i1 = individuals.get(i);

            // zapewnienie istnienia zewnętrznej mapy dla i1
            proximityTimers.putIfAbsent(i1, new HashMap<>());
            Map<Individual, Double> i1Timers = proximityTimers.get(i1);

            // aktualizacja timera zakażenia i zdrowienie (przeniesione na górę pętli,
            // bo powinno być niezależne od interakcji)
            i1.updateInfectionState(deltaTime);

            for (int j = i + 1; j < individuals.size(); j++) {
                Individual i2 = individuals.get(j);

                double dist = calculateDistance(i1, i2);

                boolean isClose = dist <= SimulationConstants.INFECTION_RADIUS;

                // pobierz lub inicjalizuj czas kontaktu
                double contactTime = i1Timers.getOrDefault(i2, 0.0);

                if (isClose) {
                    contactTime += deltaTime;

                    // zapisz dla i1
                    i1Timers.put(i2, contactTime);

                    // zapisz symetrycznie dla i2s
                    proximityTimers.putIfAbsent(i2, new HashMap<>());
                    proximityTimers.get(i2).put(i1, contactTime);

                    if (contactTime >= SimulationConstants.REQUIRED_CONTACT_TIME) {
                        tryInfect(i1, i2);
                        tryInfect(i2, i1);
                    }
                } else {
                    // jeśli nie są już blisko, resetuj timery
                    // usunięcie dla i1
                    i1Timers.remove(i2);

                    // usunięcie symetryczne dla i2 (jeśli mapa i2 istnieje)
                    Map<Individual, Double> i2Timers = proximityTimers.get(i2);
                    if (i2Timers != null) {
                        i2Timers.remove(i1);
                    }
                }
            }
        }
    }

    // próba zakażenia osobnika 'target' od osobnika 'source'
    private void tryInfect(Individual source, Individual target) {
        // cel: tylko osobnik zdrowy i wrażliwy może się zarazić
        if (!target.isSusceptibleHealthy() || !source.isCurrentlyInfected()) {
            return;
        }

        double probInfection;
        if (source.isSymptomatic()) {
            probInfection = SimulationConstants.PROB_SYMPTOMATIC_INFECTION; // 100%
        } else {
            probInfection = SimulationConstants.PROB_ASYMPTOMATIC_INFECTION; // 50%
        }

        if (random.nextDouble() < probInfection) {
            // zakażenie następuje, ustalenie czy objawowe czy bezobjawowe
            HealthState newState = random.nextBoolean() ?
                    HealthState.INFECTED_SYMPTOMATIC :
                    HealthState.INFECTED_ASYMPTOMATIC;

            target.setHealthState(newState);
            System.out.println("  [ZAKAŻENIE] Osobnik na pozycja (" + String.format("%.2f", target.getPosition().getX()) +
                    ") ZAKAŻONY! Stan: " + newState);
        }
    }

    // jeden krok symulacji
    public void step(double deltaTime) {
        handleVirusTransmission(deltaTime); // obsługa zakażeń i zdrowienia
        managePopulationEntry(deltaTime); // wprowadzanie nowych osobników

        Iterator<Individual> iterator = individuals.iterator();
        while (iterator.hasNext()) {
            Individual individual = iterator.next();

            individual.applyRandomChange(); // losowa zmiana prędkości

            individual.updatePosition(deltaTime); // aktualizacja pozycji

            // sprawdzenie granic i obsługa opuszczania/zawracania
            int boundaryAxis = individual.checkBoundary(width, height);
            if (boundaryAxis != 0) {
                if (random.nextDouble() < SimulationConstants.PROB_TURN_BACK) {
                    individual.turnBack(boundaryAxis);
                    individual.updatePosition(deltaTime * 0.1);
                } else {
                    System.out.println("  [OPUSZCZA] Osobnik opuścił obszar. Aktualna populacja: " + (individuals.size() - 1));
                    iterator.remove();
                    // Usunięcie z głównej mapy
                    proximityTimers.remove(individual);
                    // Usunięcie wszystkich referencji do tego osobnika w wewnętrznych mapach
                    for(Map<Individual, Double> innerMap : proximityTimers.values()){
                        innerMap.remove(individual);
                    }
                }
            }
        }
    }

    // aktualny stan osobników
    public void displayState() {
        long immune = individuals.stream().filter(i -> i.getHealthState() == HealthState.IMMUNE).count();
        long healthy = individuals.stream().filter(i -> i.getHealthState() == HealthState.SUSCEPTIBLE_HEALTHY).count();
        long infectedAsym = individuals.stream().filter(i -> i.getHealthState() == HealthState.INFECTED_ASYMPTOMATIC).count();
        long infectedSym = individuals.stream().filter(i -> i.getHealthState() == HealthState.INFECTED_SYMPTOMATIC).count();

        System.out.println("\n--- STAN POPULACJI: " + individuals.size() + " ---");
        System.out.println("  Odporni: " + immune);
        System.out.println("  Zdrowi/Wrażliwi: " + healthy);
        System.out.println("  Zakażeni (Bez Objawów): " + infectedAsym);
        System.out.println("  Zakażeni (Z Objawami): " + infectedSym);

        for (int i = 0; i < individuals.size(); i++) {
            System.out.println("  Osobnik " + i + ": " + individuals.get(i));
        }
        System.out.println("--------------------------------\n");
    }
}