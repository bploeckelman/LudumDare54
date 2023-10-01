package lando.systems.ld54.objects;

import com.badlogic.gdx.math.MathUtils;

// Store global stuff for the player
public class Player {
    private final int STARTING_FUEL = 3;
    public final int MAX_FUEL = 8;
    private final float MAX_SCRAP = 500f;

    public static int fuelLevel;
    public static float scrap;

    public Player() {
        fuelLevel = STARTING_FUEL;
        scrap = 0f;
    }

    public void addFuel(int fuel) {
        fuelLevel += fuel;
        fuelLevel = MathUtils.clamp(fuelLevel, STARTING_FUEL, MAX_FUEL);
    }

    public void useFuel(float fuel) {
        fuelLevel -= fuel;
        if (fuelLevel < 0) {
            fuelLevel = 0;
        }
    }

    public void addScrap(float scrap) {
        Player.scrap += scrap;
        if (Player.scrap > MAX_SCRAP) {
            Player.scrap = MAX_SCRAP;
        }
    }
}
