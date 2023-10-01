package lando.systems.ld54.objects;

// Store global stuff for the player
public class Player {
    private final float STARTING_FUEL = 100f;
    public final float MAX_FUEL = 500f;
    private final float MAX_SCRAP = 500f;

    public static float fuelLevel;
    public static float scrap;

    public Player() {
        fuelLevel = STARTING_FUEL;
        scrap = 0f;
    }

    public void addFuel(float fuel) {
        fuelLevel += fuel;
        if (fuelLevel > MAX_FUEL) {
            fuelLevel = MAX_FUEL;
        }
    }

    public void addScrap(float scrap) {
        Player.scrap += scrap;
        if (Player.scrap > MAX_SCRAP) {
            Player.scrap = MAX_SCRAP;
        }
    }
}
