package models;

import java.awt.*;

public enum InfectionStatus {
    // zdrowy i odporny (zielony)
    HEALTHY_IMMUNE(Color.GREEN),
    // wrażliwy na zakażenie: zdrowy (żółty)
    SUSCEPTIBLE_HEALTHY(Color.YELLOW),
    // wrażliwy na zakażenie: chory: bez objawów (różowy)
    SUSCEPTIBLE_ILL_ASYMPTOMATIC(Color.PINK),
    // wrażliwy na zakażenie: chory: z objawami (czerwony)
    SUSCEPTIBLE_ILL_SYMPTOMATIC(Color.RED);

    private final Color color;

    InfectionStatus(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }
}