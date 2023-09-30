package lando.systems.ld54.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import lando.systems.ld54.Config;
import lando.systems.ld54.assets.Asteroids;
import lando.systems.ld54.components.DragLauncher;
import lando.systems.ld54.fogofwar.FogOfWar;
import lando.systems.ld54.objects.Asteroid;
import lando.systems.ld54.utils.camera.PanZoomCameraController;

public class GameScreen extends BaseScreen {

    public static float gameWidth = Config.Screen.window_width * 3f;
    public static float gameHeight = Config.Screen.framebuffer_height * 3f;

    public final Vector3 mousePos = new Vector3();

    DragLauncher launcher;

    FrameBuffer foggedBuffer;
    FrameBuffer exploredBuffer;
    TextureRegion foggedTextureRegion;
    TextureRegion exploredTextureRegion;
    FogOfWar fogOfWar;
    Array<Asteroid> asteroids;
    PanZoomCameraController cameraController;

    public GameScreen() {
        launcher = new DragLauncher(this);
        fogOfWar = new FogOfWar(gameWidth, gameHeight);
        asteroids = new Array<>();
        Asteroids.createTestAsteroids(asteroids);

        Pixmap.Format format = Pixmap.Format.RGBA8888;
        int width = Config.Screen.framebuffer_width;
        int height = Config.Screen.framebuffer_height;

        foggedBuffer = new FrameBuffer(format, width, height, true);
        exploredBuffer = new FrameBuffer(format, width, height, true);

        Texture foggedFrameBufferTexture = foggedBuffer.getColorBufferTexture();
        foggedFrameBufferTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        foggedTextureRegion = new TextureRegion(foggedFrameBufferTexture);
        foggedTextureRegion.flip(false, true);

        Texture exploredFrameBufferTexture = exploredBuffer.getColorBufferTexture();
        exploredFrameBufferTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        exploredTextureRegion = new TextureRegion(exploredFrameBufferTexture);
        exploredTextureRegion.flip(false, true);

        resetWorldCamera();
    }

    @Override
    public void update(float dt) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            Gdx.app.exit();
        }

        windowCamera.unproject(mousePos.set(Gdx.input.getX(), Gdx.input.getY(), 0));
        launcher.update(dt);

        fogOfWar.update(dt);
        asteroids.forEach(Asteroid::update);

        cameraController.update(dt);
        super.update(dt);
    }

    @Override
    public void renderFrameBuffers(SpriteBatch batch) {
        fogOfWar.render(batch);

        foggedBuffer.begin();
        renderFogArea(batch);
        foggedBuffer.end();

        exploredBuffer.begin();
        renderExploredArea(batch);
        exploredBuffer.end();
    }

    @Override
    public void render(SpriteBatch batch) {
        ScreenUtils.clear(Color.DARK_GRAY);

        batch.setProjectionMatrix(windowCamera.combined);
        batch.begin();
        batch.draw(exploredTextureRegion.getTexture(), 0, 0, worldCamera.viewportWidth, worldCamera.viewportHeight);
        batch.draw(fogOfWar.fogMaskTexture, 0, 0, 40, 40);
        launcher.render(batch);
        batch.end();
    }

    /**
     * Use this to render things that should be seen when fog is removed.
     * It is ok to render things that will be covered
     *
     * @param batch
     */
    private void renderExploredArea(SpriteBatch batch) {
        ScreenUtils.clear(Color.BLACK);

        batch.setProjectionMatrix(worldCamera.combined);
        batch.begin();
        batch.draw(assets.pixelRegion, gameWidth / 2f, gameHeight / 2f, 5, 5);
        asteroids.forEach(a -> a.draw(batch));

        // TEMP - 'world' bounds
        assets.shapes.rectangle(0, 0, gameWidth, gameHeight, Color.MAGENTA, 4);
        batch.end();
    }

    /**
     * This should render things that should be in the fagged ared
     * Indicators of things to find etc
     *
     * @param batch
     */
    private void renderFogArea(SpriteBatch batch) {
        batch.setProjectionMatrix(worldCamera.combined);
    }

    private void resetWorldCamera() {
        var initialZoom = PanZoomCameraController.ZOOM_INITIAL;
        var centerShiftX = (gameWidth / 2f);
        var centerShiftY = (gameHeight / 2f);

        worldCamera = new OrthographicCamera();
        worldCamera.setToOrtho(false, Config.Screen.window_width, Config.Screen.window_height);
        worldCamera.position.set(0, 0, 0);
        worldCamera.translate(centerShiftX, centerShiftY);
        worldCamera.zoom = initialZoom;
        worldCamera.update();

        if (cameraController == null) {
            cameraController = new PanZoomCameraController(worldCamera);
            Gdx.input.setInputProcessor(cameraController);
        } else {
            cameraController.reset(worldCamera);
        }
    }

}
