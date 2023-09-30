package lando.systems.ld54.components;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import lando.systems.ld54.screens.GameScreen;

public class DragLauncher {

    private boolean dragging = false;
    private final Vector2 touchPos = new Vector2();
    private final Vector2 dragPos = new Vector2();
    //private final Vector2 launchAngle = new Vector2();

    private float maxPull = 100;
    private Animation<TextureRegion> dragAnim;

    private GameScreen screen;

    public DragLauncher(GameScreen gameScreen) {
        dragAnim = gameScreen.assets.obi;
        screen = gameScreen;
    }

    public void update(float delta) {
        Vector3 mousePos = screen.mousePos;
        if (!dragging) {
            dragging = Gdx.input.isButtonJustPressed(Input.Buttons.LEFT);
            if (dragging) {
                touchPos.set(mousePos.x, mousePos.y);
            }
        } else if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
            dragPos.set(mousePos.x - touchPos.x, mousePos.y - touchPos.y).nor();
            float strength = MathUtils.clamp(touchPos.dst(mousePos.x, mousePos.y), 0, maxPull);
            dragPos.scl(strength).add(touchPos);
        } else {
            dragging = false;
        }
    }

    public void render(SpriteBatch batch) {
        if (dragging) {
            batch.draw(dragAnim.getKeyFrame(0), dragPos.x, dragPos.y);
        }
    }

    private void launch(Vector3 launchVector) {

    }
}
