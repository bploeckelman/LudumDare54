package lando.systems.ld54.assets;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import lando.systems.ld54.Assets;
import lando.systems.ld54.Config;
import lando.systems.ld54.objects.Asteroid;

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

    public static void createTestAsteroids(Array<Asteroid> array) {
        for (var level : Asteroids.Level.values()) {
            for (int i = 0; i < 10; i++) {
                var region = Asteroids.getRandomAsteroid(level);
//                var asteroid = new Asteroid(region, MathUtils.random(0, gameWidth), MathUtils.random(0, gameHeight));

                // for now create them in the center region since we don't have pan/zoom in yet
                var minX = Config.Screen.window_width;
                var maxX = 2 * minX;
                var minY = Config.Screen.window_height;
                var maxY = 2 * minY;

                var asteroid = new Asteroid(region, MathUtils.random(minX, maxX), MathUtils.random(minY, maxY));
                array.add(asteroid);
            }
        }
    }

    public static TextureRegion getRandomAsteroid(Level level) {
        return asteroids.get(level).random();
    }

}
