package lando.systems.ld54.components;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
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
import lando.systems.ld54.objects.Sector;

public class DragLauncher extends InputAdapter {
    private final GameScreen screen;
    private final Vector3 mousePos = new Vector3();

    private boolean dragging = false;

    private final float MAX_PULL_DISTANCE = Sector.HEIGHT / 4;
    private float pullDistance = 0;
    private float angle = 0;
    private float speed = 0;
    private final Vector2 dragPos = new Vector2();

    private final Animation<TextureRegion> launcherAnimation;
    private TextureRegion currentImage;
    private float yPulse = 0;

    private float animTimer = 0;
    private float dragTimer = 0;

    private boolean isRevving = false;

    public DragLauncher(GameScreen gameScreen) {
        screen = gameScreen;
        launcherAnimation = gameScreen.assets.launchArrow;
        currentImage = launcherAnimation.getKeyFrame(0);
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
        currentImage = launcherAnimation.getKeyFrame(animTimer);
        if(dragging) {
            // adjust speed of pulse by speed pull
            yPulse = MathUtils.sin(animTimer * 10) * 0.1f;

            if(dragTimer > .85f) {
                if(!isRevving) {
                    screen.audioManager.loopSound(AudioManager.Sounds.engineRevving, .3f);
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

        pullDistance = MathUtils.clamp(earthCenter.dst(mousePos.x, mousePos.y), 0, MAX_PULL_DISTANCE);
        speed = pullDistance / MAX_PULL_DISTANCE;
        dragPos.scl(pullDistance).add(earthCenter);
    }

    public void render(SpriteBatch batch) {
        if (dragging) {
            var earthCenter = screen.earth.centerPosition;
            var w = MAX_PULL_DISTANCE;
            float imageScale = currentImage.getRegionWidth() / w;
            var h = currentImage.getRegionHeight() * imageScale;
            var scale = pullDistance / MAX_PULL_DISTANCE;
            batch.draw(currentImage,
                earthCenter.x - w,
                earthCenter.y - h / 2f,
                w,
                h / 2f,
                w, h,
                scale,
                0.9f + yPulse,
                angle
            );
        }
    }

    private void launchShip() {
        if (Config.Debug.general) {
            Gdx.app.log("LAUNCH", Stringf.format("angle: %.1f  mag: %.1f", angle, pullDistance));
        }
        dragTimer = 0;
        isRevving = false;


        // speed is 0.2 - 1
        screen.launchShip(angle, speed);
    }
}
