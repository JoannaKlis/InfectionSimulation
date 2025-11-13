import constants.AreaConstants;
import constants.SimulationConstants;
import models.Area;
import simulation.SimulationPanel;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {

            // MENU WYBORU SYMULACJI
            String[] options = {"Wersja 1 (Bez odporności)", "Wersja 2 (Z odpornością)"};
            int choice = JOptionPane.showOptionDialog(
                    null,
                    "Wybierz wersję symulacji:",
                    "Konfiguracja Symulacji Zakażeń",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[0]
            );

            // ustawienie globalnej stałej na podstawie wyboru
            if (choice == 0) {
                SimulationConstants.SIMULATION_VERSION = 1;
                System.out.println("Wybrano Wersję 1: Wrażliwa populacja początkowa, nowi osobnicy tylko wrażliwi/chorzy.");
            } else if (choice == 1) {
                SimulationConstants.SIMULATION_VERSION = 2;
                System.out.println("Wybrano Wersję 2: Możliwość odporności (zielony) w populacji początkowej i u nowych osobników.");
            } else {
                // anulowanie lub zamknięcie okna
                System.out.println("Anulowano uruchomienie symulacji.");
                return;
            }

            // URUCHOMIENIE SYMULACJI
            Area frame = new Area(AreaConstants.N_WIDTH_METERS, AreaConstants.M_HEIGHT_METERS,
                    "Symulacja Zakażeń SEIR (Wersja " + SimulationConstants.SIMULATION_VERSION + ")");

            SimulationPanel simulationPanel = new SimulationPanel();
            frame.add(simulationPanel);

            frame.pack();
            frame.setVisible(true);
        });
    }
}