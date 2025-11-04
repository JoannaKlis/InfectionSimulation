package model;

public class SimulationConstants {
    public static final double PROB_TURN_BACK = 0.5; // prawdopodobieństwo zawrócenia przy granicy
    public static final double PROB_ENTRY_INFECTION = 0.1; // prawdopodobieństwo zakażenia wkraczającego osobnika
    public static final int TARGET_POPULATION = 15; // docelowa populacja do utrzymania
    public static final double ENTRY_FREQUENCY = 1.0; // średnia liczba wkraczających osobników na krok (dla DELTA_TIME=1s)

    // logika zakażeń
    public static final double INFECTION_RADIUS = 2.0; // [m]
    public static final double REQUIRED_CONTACT_TIME = 3.0; // [s]
    public static final double PROB_ASYMPTOMATIC_INFECTION = 0.5; // 50%
    public static final double PROB_SYMPTOMATIC_INFECTION = 1.0; // 100%

    private SimulationConstants() {
        // blokada instancjonowania
    }
}
