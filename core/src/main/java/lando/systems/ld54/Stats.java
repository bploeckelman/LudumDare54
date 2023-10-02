package lando.systems.ld54;

public class Stats {
    public static int numLaunches = 0;
    public static int numShipsExploded = 0;
    public static int numShipsDerelict = 0;
    public static int numEncounters = 0;
    public static int numAstronautsEjected = 0;
    public static float runDuration = 0f;

    public static void reset() {
        numLaunches = 0;
        numShipsExploded=0;
        numShipsDerelict = 0;
        numEncounters = 0;
        numAstronautsEjected = 0;
        runDuration = 0;
    }
}
