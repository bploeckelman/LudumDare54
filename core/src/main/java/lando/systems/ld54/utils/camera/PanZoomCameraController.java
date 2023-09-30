package lando.systems.ld54.utils.camera;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.IntMap;
import lando.systems.ld54.Config;
import lando.systems.ld54.utils.Calc;
import lando.systems.ld54.utils.Time;
import text.formic.Stringf;

public class PanZoomCameraController extends GestureDetector.GestureAdapter implements InputProcessor {

    private final int PAN_LEFT = Input.Keys.D;
    private final int PAN_RIGHT = Input.Keys.A;
    private final int PAN_UP = Input.Keys.S;
    private final int PAN_DOWN = Input.Keys.W;

    private IntMap<Boolean> keys;

    private final float PAN_SPEED_0 = 10f;
    private final float PAN_SPEED_1 = 100f;
    private final float PAN_SPEED_2 = 500f;

    // TODO - scale based on zoom level
    private float units_dragged_per_pixel = PAN_SPEED_1;
    private float units_panned_per_pixel = PAN_SPEED_2;

    public static final float ZOOM_INITIAL = 1f;
    private final float ZOOM_MIN = 0.5f;
    private final float ZOOM_MAX = 4f;
    private final float ZOOM_SCALE_MIN = 2f;
    private final float ZOOM_SCALE_MAX = 5f;

    // TODO - scale based on current zoom level (zoom faster when farther away, slower when closer)
    private float zoom_scale = ZOOM_SCALE_MIN + (ZOOM_SCALE_MAX - ZOOM_SCALE_MIN) / 2f;

    private OrthographicCamera camera;
    private Vector3 tmp = new Vector3();

    public PanZoomCameraController(OrthographicCamera camera) {
        reset(camera);
    }

    public void reset(OrthographicCamera camera) {
        keys = new IntMap<>();
        keys.put(PAN_LEFT, false);
        keys.put(PAN_RIGHT, false);
        keys.put(PAN_UP, false);
        keys.put(PAN_DOWN, false);

        camera.zoom = ZOOM_INITIAL;
        this.camera = camera;
        this.camera.update();
    }

    public void moveLeft(float amount) {
        amount = Calc.abs(amount);
        tmp.set(Vector3.X).nor().scl(amount);
        camera.position.add(tmp);
    }

    public void moveRight(float amount) {
        amount = Calc.abs(amount);
        tmp.set(Vector3.X).nor().scl(-amount);
        camera.position.add(tmp);
    }

    public void moveUp(float amount) {
        amount = Calc.abs(amount);
        tmp.set(Vector3.Y).nor().scl(-amount);
        camera.position.add(tmp);
    }

    public void moveDown(float amount) {
        amount = Calc.abs(amount);
        tmp.set(Vector3.Y).nor().scl(amount);
        camera.position.add(tmp);
    }

    public void update(float delta) {
        // TODO set units_dragged_per_pixel and zoom_speed based on variable params

        var moveAmount = delta * units_panned_per_pixel;
        if (keys.get(PAN_LEFT, false)) moveLeft(moveAmount);
        if (keys.get(PAN_RIGHT, false)) moveRight(moveAmount);
        if (keys.get(PAN_UP, false)) moveUp(moveAmount);
        if (keys.get(PAN_DOWN, false)) moveDown(moveAmount);
    }

    // --------------------------------------------------------------
    // GestureAdapter implementation
    // --------------------------------------------------------------

    @Override
    public boolean touchDown(float x, float y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean tap(float x, float y, int count, int button) {
        return false;
    }

    @Override
    public boolean longPress(float x, float y) {
        return false;
    }

    @Override
    public boolean fling(float velocityX, float velocityY, int button) {
        return false;
    }

    @Override
    public boolean pan(float x, float y, float deltaX, float deltaY) {
        return false;
    }

    @Override
    public boolean panStop(float x, float y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean zoom(float initialDistance, float distance) {
        return false;
    }

    @Override
    public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
        return false;
    }

    @Override
    public void pinchStop() {
    }

    // --------------------------------------------------------------
    // InputProcessor implementation
    // --------------------------------------------------------------

    @Override
    public boolean keyDown(int keycode) {
        keys.put(keycode, true);
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        keys.put(keycode, false);
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if (Gdx.input.isButtonPressed(Input.Buttons.MIDDLE)) {
            var dx = Gdx.input.getDeltaX() * Time.delta * units_dragged_per_pixel;
            var dy = Gdx.input.getDeltaY() * Time.delta * units_dragged_per_pixel;
            if (dx > 0) moveLeft(dx);
            if (dx < 0) moveRight(dx);
            if (dy > 0) moveUp(dy);
            if (dy < 0) moveDown(dy);
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        if (amountY != 0) {
            var sign = Calc.sign(amountY);
            var zoom = camera.zoom + sign * Time.delta * zoom_scale;
            camera.zoom = MathUtils.clamp(zoom, ZOOM_MIN, ZOOM_MAX);
            if (Config.Debug.general) {
                Gdx.app.log("zoom", Stringf.format("%.2f", camera.zoom));
            }
            return true;
        }
        return false;
    }

}
