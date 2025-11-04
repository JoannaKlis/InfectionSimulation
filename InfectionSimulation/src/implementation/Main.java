package implementation;

import model.SimulationArea;

public class Main {
    static void main() {
        // przykładowe parametry symulacji
        final double AREA_WIDTH = 100.0;    // n w metrach
        final double AREA_HEIGHT = 50.0;    // m w metrach
        final int INDIVIDUAL_COUNT = 15;    // "i" osobników

        final double DELTA_TIME = 0.5;      // krok czasowy w sekundach
        final int TOTAL_STEPS = 50;        // liczba kroków symulacji
        // ---------------------------

        // iicjalizacja obszaru symulacji
        SimulationArea area = new SimulationArea(AREA_WIDTH, AREA_HEIGHT, INDIVIDUAL_COUNT);

        System.out.println("\n--- START SYMULACJI (Krok: " + DELTA_TIME + "s) ---\n");
        area.displayState();

        // przebieg symulacji
        for (int step = 0; step < TOTAL_STEPS; step++) {
            double currentTime = (step + 1) * DELTA_TIME;
            System.out.println("--- KROK " + (step + 1) + " (Czas: " + String.format("%.1f", currentTime) + "s) ---");

            // wykonanie kroku symulacji
            area.step(DELTA_TIME);

            // wyświetl stan co 25 kroków
            if ((step + 1) % 25 == 0 || step == TOTAL_STEPS - 1) {
                area.displayState();
            } else {
                System.out.println("... ruch osobników ...");
            }
        }

        System.out.println("\n--- KONIEC SYMULACJI ---");
    }
}
