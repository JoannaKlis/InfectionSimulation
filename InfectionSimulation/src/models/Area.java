package models;

import javax.swing.*;

public class Area extends JFrame {
    public static int calculatePixels(double meters) {
        // skalowanie: 1 metr = 8 pikseli (dla widoczności obszaru 100x100m)
        final double PIXELS_PER_METER = 8.0;

        return (int) Math.round(meters * PIXELS_PER_METER);
    }

    public Area(double widthMeters, double heightMeters, String title) {
        int widthPixels = calculatePixels(widthMeters);
        int heightPixels = calculatePixels(heightMeters);

        // dodanie miejsca na ramkę
        this.setSize(widthPixels + 10, heightPixels + 50);
        this.setTitle(title);

        // konfiguracja okna
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setResizable(true);
    }
}