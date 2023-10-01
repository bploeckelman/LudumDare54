package lando.systems.ld54.components;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import lando.systems.ld54.Config;
import lando.systems.ld54.audio.AudioManager;
import lando.systems.ld54.screens.GameScreen;
import text.formic.Stringf;

public class DragLauncher extends InputAdapter {

    private boolean dragging = false;
    private final Vector3 mousePos = new Vector3();

    private final Vector2 dragPos = new Vector2();
    private float strength = 0;
    private float angle = 0;

    private float maxPull = 100;
    private Animation<TextureRegion> dragAnim;
    private TextureRegion currentImage;
    private float animTimer = 0;
    private float dragTimer = 0;

    private boolean isRevving = false;

    private final GameScreen screen;

    public DragLauncher(GameScreen gameScreen) {
        dragAnim = gameScreen.assets.launchArrow;
        currentImage = dragAnim.getKeyFrame(0);
        screen = gameScreen;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (button != Input.Buttons.LEFT || pointer > 0) {
            dragging = false;
        } else {
            mousePos.set(screenX, screenY, 0);
            screen.worldCamera.unproject(mousePos);
            dragging = screen.earth.contains(mousePos);






            if (dragging) {
                updateLaunchAngle(mousePos);
                screen.audioManager.playSound(AudioManager.Sounds.engineStart);
            }
        }
        return dragging;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if (!dragging) { return false; }

        mousePos.set(screenX, screenY, 0);
        screen.worldCamera.unproject(mousePos);
        updateLaunchAngle(mousePos);
//        if(screen.assets.engineRevving);




        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (dragging) {
            dragging = false;
            screen.audioManager.stopSound(AudioManager.Sounds.engineStart);
            screen.audioManager.stopSound(AudioManager.Sounds.engineRevving);
            launchShip();
            return true;
        }
        return false;
    }

    public void update(float delta) {
        animTimer += delta;
        currentImage = dragAnim.getKeyFrame(animTimer);
        if(dragging) {
            if(dragTimer > .85f) {
                if(!isRevving) {
                    screen.audioManager.loopSound(AudioManager.Sounds.engineRevving, .46f);
                }

                isRevving = true;
            }
            dragTimer+= delta;
        }

    }

    private void updateLaunchAngle(Vector3 mousePos) {

        var earthCenter = screen.earth.centerPosition;

        dragPos.set(earthCenter).sub(mousePos.x, mousePos.y).nor();
        angle = dragPos.angleDeg();

        strength = MathUtils.clamp(earthCenter.dst(mousePos.x, mousePos.y), 0, maxPull);
        dragPos.scl(strength).add(earthCenter);
    }

    public void render(SpriteBatch batch) {
        if (dragging) {
            var earthCenter = screen.earth.centerPosition;
            var w = currentImage.getRegionWidth();
            var h = currentImage.getRegionHeight();
            var scale = strength / maxPull;
            batch.draw(currentImage,
                earthCenter.x - w,
                earthCenter.y - h / 2f,
                w,
                h / 2f,
                w, h,
                scale,
                1f,
                angle
            );
        }
    }

    private void launchShip() {
        if (Config.Debug.general) {
            Gdx.app.log("LAUNCH", Stringf.format("angle: %.1f  mag: %.1f", angle, strength));
        }
        dragTimer = 0;
        isRevving = false;
        screen.audioManager.playSound(AudioManager.Sounds.engineLaunch, 1.8f);
        screen.audioManager.loopSound(AudioManager.Sounds.engineRunning, 1.74f);

        // temp
        screen.launchShip(angle, strength * 5);
    }
}
