package lando.systems.ld54.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
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
    Animation<TextureRegion> fuelAnimation1;
    Animation<TextureRegion> fuelAnimation2;
    Animation<TextureRegion> fuelAnimation3;
    Animation<TextureRegion> fuelAnimation4;
    Animation<TextureRegion> fuelAnimation5;
    Animation<TextureRegion> fuelAnimation6;
    Animation<TextureRegion> fuelAnimation7;
    Animation<TextureRegion> fuelAnimation8;
    float accum;
    float fuelLevel;


    public GameScreenUI(GameScreen screen) {
        this.screen = screen;
        bounds = new Rectangle(5, screen.windowCamera.viewportHeight - HEIGHT - screen.miniMap.bounds.height - 15f, WIDTH, HEIGHT);
        targetBounds = new Rectangle(bounds);
        fuelLevel = screen.player.fuelLevel;
    }

    public void update(float dt) {
        bounds.x = screen.miniMap.bounds.x;
        accum+=dt;
        if (screen.currentShip != null && !screen.currentShip.isReset) {
            fuelLevel= screen.currentShip.fuel / screen.currentShip.FUEL_PER_BAR_LEVEL;
        } else {
            fuelLevel = screen.player.fuelLevel;
        }
    }

    public void render(SpriteBatch batch) {
        float alpha = .4f;
        batch.setColor(1f, 1f, 1f, alpha);
        Assets.NinePatches.glass_blue.draw(batch, bounds.x - 5, bounds.y - 5, bounds.width + 10, bounds.height + 10);
        batch.setColor(Color.WHITE);
        batch.draw(Main.game.assets.pickupsFuel.getKeyFrame(accum), bounds.x + 5f, bounds.y + bounds.height - 80f, 60f, 60f);

        for (int i = 0; i < screen.player.fuelLevel; i++) {
            Animation<TextureRegion> batteryAnimation;
            int row = i / 4;
            if (i < MathUtils.floor(fuelLevel)) {
                batteryAnimation = Main.game.assets.batteryGreen;
            }
            else if (i >= MathUtils.ceil(fuelLevel)) {
                batteryAnimation = Main.game.assets.batteryEmpty;
            }
            else {
                //this is last battery, so grab the decimals to pick the battery sprite
                float decimals = fuelLevel - MathUtils.floor(fuelLevel);
                if (decimals > .75f || decimals == 0f) {
                    batteryAnimation = Main.game.assets.batteryGreen;
                }
                else if (decimals > .5f) {
                    batteryAnimation = Main.game.assets.batteryYellow;
                }
                else if (decimals > .25f) {
                    batteryAnimation = Main.game.assets.batteryOrange;
                }
                else if (decimals > 0f) {
                    batteryAnimation = Main.game.assets.batteryRed;
                }
                else {
                    batteryAnimation = Main.game.assets.batteryEmpty;
                }
            }
            batch.draw(batteryAnimation.getKeyFrame(accum), bounds.x + 80f + (i % 4 * 40f), bounds.y + bounds.height - 50f - row * 30f , 40f, 30f);
        }
        //batch.draw(Main.game.assets.batteryGreen.getKeyFrame(0), bounds.x + 80f, bounds.y + bounds.height - 50f, 40f, 30f);
//        batch.draw(Main.game.assets.whitePixel, bounds.x + 60f + 10f + 5f, bounds.y + bounds.height - 40f, 30f, 30f);
 //       batch.draw(Main.game.assets.whitePixel, bounds.x + 90f + 10f + 5f + 5f, bounds.y + bounds.height - 40f, 30f, 30f);

    }
}
