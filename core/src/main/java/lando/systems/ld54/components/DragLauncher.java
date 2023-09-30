package lando.systems.ld54.components;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import lando.systems.ld54.Config;
import lando.systems.ld54.screens.GameScreen;
import text.formic.Stringf;

public class DragLauncher {

    private boolean dragging = false;
    private final Vector2 dragPos = new Vector2();
    private float strength = 0;
    private float angle = 0;

    private float maxPull = 100;
    private Animation<TextureRegion> dragAnim;
    private TextureRegion currentImage;
    private float animTimer = 0;

    private final GameScreen screen;

    public DragLauncher(GameScreen gameScreen) {
        dragAnim = gameScreen.assets.launchArrow;
        currentImage = dragAnim.getKeyFrame(0);
        screen = gameScreen;
    }

    public void update(float delta) {
        animTimer += delta;
        Vector3 mousePos = screen.mousePos;

        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT) && screen.earth.contains(mousePos)) {
            dragging = true;
            animTimer = 0;
            updateLaunchAngle(mousePos);
        } else if (dragging && Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
            updateLaunchAngle(mousePos);
        } else if (dragging) {
            dragging = false;
            launchShip();
        }
    }

    private void updateLaunchAngle(Vector3 mousePos) {
        currentImage = dragAnim.getKeyFrame(animTimer);

        var earthCenter = screen.earth.centerPosition;

        dragPos.set(mousePos.x - earthCenter.x, mousePos.y - earthCenter.y).nor();
        angle = dragPos.angleDeg() - 90;
        strength = MathUtils.clamp(earthCenter.dst(mousePos.x, mousePos.y), 0, maxPull);
        dragPos.scl(strength).add(earthCenter);
    }

    public void render(SpriteBatch batch) {
        if (dragging) {
            var earthCenter = screen.earth.centerPosition;
//            batch.draw(currentImage, earthCenter.x,
//                earthCenter.y - currentImage.getRegionHeight() / 2f, 0,currentImage.getRegionHeight() / 2f,
//                currentImage.getRegionWidth(), currentImage.getRegionHeight(), -strength/maxPull, 1f,angle - 90);
            batch.draw(currentImage,
                earthCenter.x - currentImage.getRegionWidth() / 2f,
                earthCenter.y,
                currentImage.getRegionWidth() / 2f,
                0,
                currentImage.getRegionWidth(),
                currentImage.getRegionHeight(),
                1f, strength/maxPull,
                angle
            );
        }
    }

    private void launchShip() {
        // launch angle is backwards
//        screen.launchShip(angle - 180, strength);

        if (Config.Debug.general) {
            Gdx.app.log("LAUNCH", Stringf.format("angle: %.1f  mag: %.1f", angle, strength));
        }
        screen.launchShip(angle, strength);
    }

}
