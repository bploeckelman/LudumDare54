package lando.systems.ld54.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import lando.systems.ld54.Assets;
import lando.systems.ld54.Config;
import lando.systems.ld54.screens.GameScreen;

public class MiniMap {

    static float WIDTH = Config.Screen.window_width * .2f;
    static float HEIGHT = Config.Screen.window_height * .2f;

    GameScreen screen;
    Rectangle bounds;


    public MiniMap(GameScreen screen) {
        this.screen = screen;
        bounds = new Rectangle(0, screen.windowCamera.viewportHeight - HEIGHT, WIDTH, HEIGHT);
    }

    public void update(float dt) {
        if (screen.worldCamera.position.x < GameScreen.gameWidth/2) {
            bounds.x = screen.windowCamera.viewportWidth - WIDTH;
        } else {
            bounds.x = 0;
        }
    }

    public void render(SpriteBatch batch) {
        batch.setColor(1f, 1f, 1f, .4f);
        Assets.NinePatches.glass_blue.draw(batch, bounds.x, bounds.y, bounds.width, bounds.height);
        batch.setColor(Color.WHITE);
    }
}
