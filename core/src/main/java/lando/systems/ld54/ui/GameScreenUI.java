package lando.systems.ld54.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import lando.systems.ld54.Assets;
import lando.systems.ld54.Config;
import lando.systems.ld54.Main;
import lando.systems.ld54.screens.GameScreen;

public class GameScreenUI {

    static float WIDTH = Config.Screen.window_width * .2f;
    static float HEIGHT = Config.Screen.window_height * .2f;

    GameScreen screen;
    Rectangle bounds;
    Rectangle targetBounds;
    Rectangle fuelBox1;
    Rectangle fuelBox2;
    Rectangle fuelBox3;
    Rectangle fuelBox4;
    Rectangle fuelBox5;
    Rectangle fuelBox6;
    Rectangle fuelBox7;
    Rectangle fuelBox8;

    float accum;


    public GameScreenUI(GameScreen screen) {
        this.screen = screen;
        bounds = new Rectangle(5, screen.windowCamera.viewportHeight - HEIGHT - screen.miniMap.bounds.height - 15f, WIDTH, HEIGHT);
        targetBounds = new Rectangle(bounds);
        fuelBox1 = new Rectangle(bounds.x + 5f, bounds.y + bounds.height - 40f, 30f, 30f);

    }

    public void update(float dt) {
        bounds.x = screen.miniMap.bounds.x;
        accum+=dt;
    }

    public void render(SpriteBatch batch) {
        float alpha = .4f;
        batch.setColor(1f, 1f, 1f, alpha);
        Assets.NinePatches.glass_blue.draw(batch, bounds.x - 5, bounds.y - 5, bounds.width + 10, bounds.height + 10);
        batch.setColor(Color.WHITE);
        batch.draw(Main.game.assets.pickupsFuel.getKeyFrame(accum), bounds.x + 5f, bounds.y + bounds.height - 40f, 30f, 30f);

        //draw fuelBox1 next to the pickupsFuel
        batch.draw(Main.game.assets.whitePixel, bounds.x + 30f + 10f, bounds.y + bounds.height - 40f, 30f, 30f);
        batch.draw(Main.game.assets.whitePixel, bounds.x + 60f + 10f + 5f, bounds.y + bounds.height - 40f, 30f, 30f);
        batch.draw(Main.game.assets.whitePixel, bounds.x + 90f + 10f + 5f + 5f, bounds.y + bounds.height - 40f, 30f, 30f);

    }
}
