package constants;

import java.util.concurrent.ThreadLocalRandom;

public class SimulationConstants {
    // STAŁE DLA WERSJI SYMULACJI (USTAWIANE W MENU)
    public static int SIMULATION_VERSION = 1; // 1: Wersja 1 (domyślna), 2: Wersja 2
    public static final double INITIAL_IMMUNE_PROBABILITY = 0.10; // 10% szans na odporność w populacji początkowej (Wersja 2)
    public static final double NEW_PERSON_IMMUNE_PROBABILITY = 0.05; // 5% szans na odporność u wkraczającego (Wersja 2)

    // STAŁE CZASU SYMULACJI
    public static final int STEPS_PER_SECOND = 25; // 25 kroków to jedna sekunda symulacji
    public static final int SIMULATION_DELAY_MS = 1000 / STEPS_PER_SECOND; // opóźnienie dla timera (40ms)

    // STAŁE POPULACJI
    public static final double MAX_SPEED_M_PER_S = 2.5;  // max. prędkość poruszania się osobników (m/s)
    public static final int INITIAL_POPULATION_SIZE = 1000; // liczba początkowej populacji
    public static final double NEW_PERSON_ENTRY_INTERVAL_S = 0.01; // nowy osobnik co 0.01 sekundy

    // LOGIKA ZAKAŻEŃ
    public static final double NEW_PERSON_INFECTION_PROBABILITY = 0.10; // 10% na bycie zakażonym
    public static final double INFECTION_DISTANCE_M = 2.0;       // max. odległość zarażenia się (metry)
    public static final double MIN_INFECTION_TIME_S = 3.0;       // min. czas utrzymania odległości (sekundy)

    // WYMAGANY CZAS W KROKACH
    public static final int MIN_INFECTION_STEPS = (int) (MIN_INFECTION_TIME_S * STEPS_PER_SECOND);
    public static final double ASYMPTOMATIC_TRANSMISSION_PROBABILITY = 0.50; // 50%
    public static final double SYMPTOMATIC_TRANSMISSION_PROBABILITY = 1.00;  // 100%

    // CZAS TRWANIA CHOROBY W KROKACH MIN/MAX
    public static final int MIN_ILLNESS_DURATION_STEPS = 20 * STEPS_PER_SECOND;
    public static final int MAX_ILLNESS_DURATION_STEPS = 30 * STEPS_PER_SECOND;

    // losowy czas trwania choroby w krokach symulacji
    public static int getRandomIllnessDurationSteps() {
        return ThreadLocalRandom.current().nextInt(MIN_ILLNESS_DURATION_STEPS, MAX_ILLNESS_DURATION_STEPS + 1);
    }
}