import javax.swing.SwingUtilities;

import constants.AreaConstants;
import models.Area;

public class Main {


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Area frame = new Area(AreaConstants.N_WIDTH_METERS, AreaConstants.M_HEIGHT_METERS);
            frame.setVisible(true);
        });
    }
}