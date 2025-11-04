package model;

public class SimulationConstants {
    public static double PROB_TURN_BACK = 0.5; // prawdopodobieństwo zawrócenia przy granicy
    public static double PROB_INFECTION = 0.1; // prawdopodobieństwo zakażenia wkraczającego osobnika
    public static int TARGET_POPULATION = 15; // docelowa populacja do utrzymania
    public static double ENTRY_FREQUENCY = 1.0; // średnia liczba wkraczających osobników na krok (dla DELTA_TIME=1s)
}
