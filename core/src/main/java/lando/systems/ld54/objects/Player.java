package lando.systems.ld54.objects;

import com.badlogic.gdx.math.MathUtils;

// Store global stuff for the player
public class Player {
    public static final int STARTING_FUEL = 2;
    public static final int MAX_FUEL = 8;
    private final float MAX_SCRAP = 500f;

    public int fuelLevel;
    public float scrap;

    public int minFuelLevel = STARTING_FUEL;

    public Player() {
        fuelLevel = STARTING_FUEL;
        scrap = 0f;
    }

    public void addFuel(int fuel) {
        fuelLevel += fuel;
        fuelLevel = MathUtils.clamp(fuelLevel, minFuelLevel, MAX_FUEL);
    }


    public void addScrap(float scrap) {
        scrap += scrap;
        if (scrap > MAX_SCRAP) {
            scrap = MAX_SCRAP;
        }
    }
}
