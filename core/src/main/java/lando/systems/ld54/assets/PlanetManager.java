package lando.systems.ld54.assets;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import lando.systems.ld54.objects.*;
import lando.systems.ld54.screens.GameScreen;

public class PlanetManager {

    final int Rows = 3;
    final int Columns = 3;

    private final GameScreen screen;

    public PlanetManager(GameScreen screen) {
        this.screen = screen;
    }

    public Earth createPlanets(Array<Planet> planets) {
        addPlanet(planets, new Mercury(screen.assets), 0, 0);
        addPlanet(planets, new Venus(screen.assets), 0, 2);
        addPlanet(planets, new Mars(screen.assets), 2, 0);
        addPlanet(planets, new Jupiter(screen.assets), 2, 2);

        var earth = new Earth(screen.assets,GameScreen.gameWidth / 2f, GameScreen.gameHeight / 2f);
        planets.add(earth);
        return earth;
    }

    private void addPlanet(Array<Planet> planets, Planet planet, int row, int column) {
        float width = GameScreen.gameWidth / Columns;
        float height = GameScreen.gameHeight / Rows;

        float x = width * column;
        float y = height * row;

        float posX = MathUtils.random.nextFloat() * (width - planet.size * 2);
        float posY = MathUtils.random.nextFloat() * (height - planet.size * 2);

        planet.setPosition(x + planet.size + posX, y + planet.size + posY);

        planets.add(planet);
    }
}
