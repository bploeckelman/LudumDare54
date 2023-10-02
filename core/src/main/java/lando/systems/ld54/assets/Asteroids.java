package lando.systems.ld54.assets;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import lando.systems.ld54.Assets;
import lando.systems.ld54.objects.Asteroid;
import lando.systems.ld54.screens.GameScreen;

public class Asteroids {

    private static Assets assets;
    private static ObjectMap<Level, Array<TextureRegion>> asteroids;

    public enum Level { one, two, three, four }

    public static void init(Assets assets) {
        Asteroids.assets = assets;
        Asteroids.asteroids = new ObjectMap<>();

        for (var level : Level.values()) {
            asteroids.put(level, new Array<>());
        }

        for (var region : assets.atlas.getRegions()) {
            if      (region.name.startsWith("asteroids/level-1/")) asteroids.get(Level.one).add(region);
            else if (region.name.startsWith("asteroids/level-2/")) asteroids.get(Level.two).add(region);
            else if (region.name.startsWith("asteroids/level-3/")) asteroids.get(Level.three).add(region);
            else if (region.name.startsWith("asteroids/level-4/")) asteroids.get(Level.four).add(region);
        }
    }

    public static void createTestAsteroids(GameScreen screen, Array<Asteroid> array) {
        for (var level : Asteroids.Level.values()) {
            for (int i = 0; i < 60; i++) {
                var region = Asteroids.getRandomAsteroid(level);
                var x = MathUtils.random(0, GameScreen.gameWidth);
                var y = MathUtils.random(0, GameScreen.gameHeight);
                var asteroid = new Asteroid(screen, region, x, y);
                array.add(asteroid);
            }
        }
    }

    public static TextureRegion getRandomAsteroid(Level level) {
        return asteroids.get(level).random();
    }

}
