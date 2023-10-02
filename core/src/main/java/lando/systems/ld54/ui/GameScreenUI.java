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
import lando.systems.ld54.Stats;
import lando.systems.ld54.screens.GameScreen;
import lando.systems.ld54.utils.Utils;

public class GameScreenUI {

    static float WIDTH = Config.Screen.window_width * .2f;
    static float HEIGHT = Config.Screen.window_height * .25f;

    GameScreen screen;
    Rectangle bounds;
    Rectangle targetBounds;
    float accum;
    float fuelLevel;
    float health = 1f;
    float pulseTimer = 0f;


    public GameScreenUI(GameScreen screen) {
        this.screen = screen;
        bounds = new Rectangle(5, 5f, WIDTH, HEIGHT);
        targetBounds = new Rectangle(bounds);
        fuelLevel = screen.player.fuelLevel;
    }

    public void update(float dt) {
        bounds.x = screen.miniMap.bounds.x;
        accum+=dt;
        pulseTimer+=dt;
        if (screen.currentShip != null && !screen.currentShip.isReset) {
            fuelLevel= screen.currentShip.fuel / screen.currentShip.FUEL_PER_BAR_LEVEL;
            health = MathUtils.clamp(screen.currentShip.health / screen.currentShip.MAX_HEALTH, 0f, 1f);
        } else {
            fuelLevel = screen.player.fuelLevel;
            health = 1f;
        }
    }

    public void render(SpriteBatch batch) {
        float alpha = .4f;
        batch.setColor(1f, 1f, 1f, alpha);
        Assets.NinePatches.glass_blue.draw(batch, bounds.x - 5, bounds.y - 5, bounds.width + 10, bounds.height + 10);
        batch.setColor(Color.WHITE);

        // Fuel Tank
        var anim = Main.game.assets.pickupsFuel;
        TextureRegion textureRegion;
        if (screen.currentShip == null || screen.currentShip.isReset || screen.currentShip.fuel <= 0) {
            textureRegion = anim.getKeyFrame(0f);
        } else {
            textureRegion = anim.getKeyFrame(accum);
        }
        batch.draw(textureRegion, bounds.x + 5f, bounds.y + bounds.height - 80f, 60f, 60f);

        // Heart
        TextureRegion icon = Main.game.assets.heartIcon;
        float iconSize = 40f;
        if (pulseTimer % 1.1f > (health)) {
            float pulsePercentage = (pulseTimer % 0.25f) +1f;
            iconSize = iconSize * pulsePercentage;
        }
        batch.draw(icon, bounds.x + 20f, bounds.y + bounds.height - 130f, iconSize, iconSize);

        // Batteries
        float endOfBatteriesY = 0f;
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
            endOfBatteriesY = bounds.y + bounds.height - 50f - row * 30f;
            batch.draw(batteryAnimation.getKeyFrame(accum), bounds.x + 80f + (i % 4 * 40f), endOfBatteriesY, 40f, 30f);
        }

        float healthBarWidth = 160f;
        float healthBarHeight = 20f;
        float healthBarX = bounds.x + 80f;
        float healthBarY = bounds.y + bounds.height - 120f;
        batch.setColor(Color.RED);
        batch.draw(Main.game.assets.whitePixel, healthBarX, healthBarY, healthBarWidth, healthBarHeight);
        batch.setColor(Color.FOREST);
        batch.draw(Main.game.assets.whitePixel, healthBarX, healthBarY, healthBarWidth * health, healthBarHeight);
        batch.setColor(Color.WHITE);

        // Show number of encounters
        String encounters = "Encounters: " + Stats.numEncounters + " / 23";
        Main.game.assets.starJediFont20.draw(batch, encounters, bounds.x + 20f, healthBarY - 30f);

    }
}
