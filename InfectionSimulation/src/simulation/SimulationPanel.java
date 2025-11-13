package simulation;

import constants.AreaConstants;
import constants.SimulationConstants;
import implementation.Vector2D;
import models.Area;
import models.Person;
import models.InfectionStatus;
import models.PersonMemento;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class SimulationPanel extends JPanel implements ActionListener {
    private List<Person> population;
    private final Map<Person, Map<Person, Integer>> proximityTracker;
    private int stepCounter;
    private final Timer timer;
    private final List<SimulationMemento> history;

    public SimulationPanel() {
        this.population = new ArrayList<>();
        this.proximityTracker = new HashMap<>();
        this.stepCounter = 0;
        this.history = new ArrayList<>();

        this.setLayout(new BorderLayout());

        // panel rysujący
        JPanel drawingPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                SimulationPanel.this.paintComponent(g);
            }
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(Area.calculatePixels(AreaConstants.N_WIDTH_METERS),
                        Area.calculatePixels(AreaConstants.M_HEIGHT_METERS));
            }
        };
        drawingPanel.setBackground(Color.LIGHT_GRAY);
        this.add(drawingPanel, BorderLayout.CENTER);

        initializePopulation();
        setupControlPanel();

        // timer symulacji
        timer = new Timer(SimulationConstants.SIMULATION_DELAY_MS, this);
        timer.start();
    }

    private void setupControlPanel() {
        JPanel controlPanel = new JPanel();

        JButton saveButton = new JButton("Zapisz Stan");
        saveButton.addActionListener(e -> saveState());

        JButton loadButton = new JButton("Wczytaj Stan");
        loadButton.addActionListener(e -> loadState());

        controlPanel.add(saveButton);
        controlPanel.add(loadButton);

        this.add(controlPanel, BorderLayout.SOUTH);
    }

    // zapisywanie stanu symulacji za pomocą pamiatki
    public void saveState() {
        List<PersonMemento> personMementos = population.stream()
                .map(Person::saveState)
                .collect(Collectors.toList());

        SimulationMemento memento = new SimulationMemento(stepCounter, personMementos);
        history.add(memento);
        System.out.println("Zapisano stan symulacji w kroku: " + stepCounter);
    }

    // załadowanie zapisanego stanu
    public void loadState() {
        if (history.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Brak zapisanych stanów do wczytania.", "Błąd Wczytywania", JOptionPane.WARNING_MESSAGE);
            return;
        }

        SimulationMemento memento = history.remove(history.size() - 1); // wczytanie ostatniego stanu

        // dtworzenie stanu kroków i populacji
        this.stepCounter = memento.getStepCounter();
        this.population = memento.getPopulationMementos().stream()
                .map(Person::new) // Użycie konstruktora Person(PersonMemento)
                .collect(Collectors.toList());

        proximityTracker.clear(); // wyczyszczenie proximityTracker

        System.out.println("Wczytano stan symulacji z kroku: " + stepCounter);
        repaint();
    }


    // populacja początkowa
    private void initializePopulation() {
        for (int i = 0; i < SimulationConstants.INITIAL_POPULATION_SIZE; i++) {
            population.add(Person.createInitialPerson());
        }
    }

    // dodawanie nowych osobnika co sekundę
    private void addNewPersons() {
        if (stepCounter % (SimulationConstants.NEW_PERSON_ENTRY_INTERVAL_S * SimulationConstants.STEPS_PER_SECOND) == 0) {
            population.add(Person.createNewEntryPerson());
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        updateSimulation();
        repaint();
    }

    // logika jednego kroku symulacji
    private void updateSimulation() {
        stepCounter++;
        addNewPersons();

        // aktualizacja stanu i ruchu osobników
        for (Person person : population) {
            person.update();
        }

        int initialSize = population.size();
        population.removeIf(Person::shouldBeRemoved);
        int removedCount = initialSize - population.size();

        if (removedCount > 0) {
            proximityTracker.clear(); // czyszczenie trackera
        }
        checkInfections();
    }

    // implementacja logiki zakażeń
    private void checkInfections() {
        int minStepsToInfection = SimulationConstants.MIN_INFECTION_STEPS;
        double infectionDistSq = SimulationConstants.INFECTION_DISTANCE_M * SimulationConstants.INFECTION_DISTANCE_M;

        Map<Person, Map<Person, Integer>> newProximityTracker = new HashMap<>();

        for (int i = 0; i < population.size(); i++) {
            Person personA = population.get(i);

            for (int j = i + 1; j < population.size(); j++) {
                Person personB = population.get(j);

                Vector2D posA = personA.getPosition();
                Vector2D posB = personB.getPosition();

                double dx = posA.getComponents()[0] - posB.getComponents()[0];
                double dy = posA.getComponents()[1] - posB.getComponents()[1];
                double distanceSq = dx * dx + dy * dy;

                if (distanceSq < infectionDistSq) {
                    int stepsAtoB = proximityTracker.getOrDefault(personA, new HashMap<>()).getOrDefault(personB, 0);
                    stepsAtoB++;

                    newProximityTracker.computeIfAbsent(personA, _ -> new HashMap<>()).put(personB, stepsAtoB);
                    newProximityTracker.computeIfAbsent(personB, _ -> new HashMap<>()).put(personA, stepsAtoB);

                    if (stepsAtoB >= minStepsToInfection) {
                        attemptInfection(personA, personB);
                    }
                }
            }
        }

        proximityTracker.clear();
        proximityTracker.putAll(newProximityTracker);
    }

    // zarażanie zdorwych i wrażliwych osobników
    private void attemptInfection(Person p1, Person p2) {
        Person illPerson;
        Person susceptiblePerson;

        // określenie, który jest chory, a który podatny
        if (p1.isIll() && p2.isSusceptible()) {
            illPerson = p1;
            susceptiblePerson = p2;
        } else if (p2.isIll() && p1.isSusceptible()) {
            illPerson = p2;
            susceptiblePerson = p1;
        } else {
            return; // brak interakcji zakaźny/podatny lub osoba odporna
        }

        double chance;
        if (illPerson.hasSymptoms()) {
            chance = SimulationConstants.SYMPTOMATIC_TRANSMISSION_PROBABILITY;
        } else {
            chance = SimulationConstants.ASYMPTOMATIC_TRANSMISSION_PROBABILITY;
        }

        if (ThreadLocalRandom.current().nextDouble() < chance) {
            susceptiblePerson.infect();
        }
    }

    // wizualizacja symulacji
    @Override
    protected void paintComponent(Graphics g) {
        if (getParent().getLayout() instanceof BorderLayout) {
            super.paintComponent(g);
        } else {
            super.paintComponent(g);
        }

        Graphics2D g2d = (Graphics2D) g;

        // wymiary obszaru
        int panelWidth = getWidth();
        int panelHeight = getHeight();

        for (Person person : population) {
            Vector2D pos = person.getPosition();

            // konwersja metrów na piksele
            int xPixel = (int) (pos.getComponents()[0] * panelWidth / AreaConstants.N_WIDTH_METERS);
            int yPixel = (int) (pos.getComponents()[1] * panelHeight / AreaConstants.M_HEIGHT_METERS);

            int diameter = 8;
            int radius = diameter / 2;

            g2d.setColor(person.getStatus().getColor());

            g2d.fillOval(xPixel - radius, yPixel - radius, diameter, diameter);
        }

        drawStatus(g2d);
    }

    // wyświetlenie statystyk
    private void drawStatus(Graphics2D g2d) {
        Map<InfectionStatus, Long> statusCounts = population.stream()
                .collect(java.util.stream.Collectors.groupingBy(Person::getStatus, java.util.stream.Collectors.counting()));

        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.BOLD, 14));

        int yOffset = 20;
        g2d.drawString(String.format("Wersja: %d", SimulationConstants.SIMULATION_VERSION), 10, yOffset);
        yOffset += 20;
        g2d.drawString(String.format("Krok: %d (%.2f s)", stepCounter, (double)stepCounter / SimulationConstants.STEPS_PER_SECOND), 10, yOffset);
        yOffset += 20;
        g2d.drawString("Populacja: " + population.size(), 10, yOffset);
        yOffset += 20;

        for (InfectionStatus status : InfectionStatus.values()) {
            g2d.setColor(status.getColor().darker());
            long count = statusCounts.getOrDefault(status, 0L);
            String statusText = status.toString().replace("_", " ").toLowerCase();
            g2d.drawString(String.format("%s: %d", statusText, count), 10, yOffset);
            yOffset += 20;
        }
    }
}