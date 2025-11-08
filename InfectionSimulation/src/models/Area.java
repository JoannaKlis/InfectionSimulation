package models;

import javax.swing.*;
import java.awt.*;

public class Area extends JFrame {
    private int calculatePixels(double meters) {
        // 1 cal = 0.0254 metra
        double inches = meters / 0.0254;

        Toolkit toolkit = Toolkit.getDefaultToolkit();
        double screenDPI = toolkit.getScreenResolution();

        return (int) Math.round(inches * screenDPI);
    }

    public Area(double widthMeters, double heightMeters) {
        int widthPixels = calculatePixels(widthMeters);
        int heightPixels = calculatePixels(heightMeters);

        this.setSize(widthPixels, heightPixels);

        // konfiguracja okna
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setResizable(true);
    }
}