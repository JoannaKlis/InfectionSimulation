import constants.AreaConstants;
import models.Area;
import simulation.SimulationPanel;

import javax.swing.*;

void main() {
    SwingUtilities.invokeLater(() -> {
        // tworzenie okna
        Area frame = new Area(AreaConstants.N_WIDTH_METERS, AreaConstants.M_HEIGHT_METERS, "Symulacja Zakażeń SEIR");

        // tworzenie i dodawanie panelu symulacji do okna
        SimulationPanel simulationPanel = new SimulationPanel();
        frame.add(simulationPanel);

        frame.pack(); // dopasowanie rozmiaru okna do preferowanego rozmiaru panelu
        frame.setVisible(true);
    });
}