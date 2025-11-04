package implementation;

public enum HealthState {
    // odporny (zdrowy)
    IMMUNE,

    // wrażliwy i zdrowy
    SUSCEPTIBLE_HEALTHY,

    // wrażliwy i zakażony (bez objawów)
    INFECTED_ASYMPTOMATIC,

    // wrażliwy i zakażony (z objawami)
    INFECTED_SYMPTOMATIC
}
