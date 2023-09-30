package lando.systems.ld54.screens;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import lando.systems.ld54.Config;
import lando.systems.ld54.assets.Asteroids;
import lando.systems.ld54.objects.Earth;
import lando.systems.ld54.components.DragLauncher;
import lando.systems.ld54.fogofwar.FogOfWar;
import lando.systems.ld54.objects.Asteroid;
import lando.systems.ld54.objects.PlayerShip;
import lando.systems.ld54.utils.camera.PanZoomCameraController;

public class GameScreen extends BaseScreen {

    public static float gameWidth = Config.Screen.window_width * 3f;
    public static float gameHeight = Config.Screen.framebuffer_height * 3f;

    public final Vector3 mousePos = new Vector3();

    public Earth earth;

    DragLauncher launcher;

    FrameBuffer foggedBuffer;
    FrameBuffer exploredBuffer;
    FrameBuffer fogMaskBuffer;
    TextureRegion foggedTextureRegion;
    TextureRegion exploredTextureRegion;
    TextureRegion fogMaskTextureRegion;
    FogOfWar fogOfWar;
    Array<PlayerShip> playerShips = new Array<>();
    Array<Asteroid> asteroids;
    PanZoomCameraController cameraController;
    float accum;

    public GameScreen() {
        launcher = new DragLauncher(this);
        fogOfWar = new FogOfWar(gameWidth, gameHeight);
        earth = new Earth(assets,gameWidth / 2f, gameHeight / 2f);
        asteroids = new Array<>();
        Asteroids.createTestAsteroids(asteroids);

        Pixmap.Format format = Pixmap.Format.RGBA8888;
        int width = Config.Screen.framebuffer_width;
        int height = Config.Screen.framebuffer_height;

        foggedBuffer = new FrameBuffer(format, width, height, true);
        exploredBuffer = new FrameBuffer(format, width, height, true);
        fogMaskBuffer = new FrameBuffer(format, width, height, true);

        Texture foggedFrameBufferTexture = foggedBuffer.getColorBufferTexture();
        foggedFrameBufferTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        foggedTextureRegion = new TextureRegion(foggedFrameBufferTexture);
        foggedTextureRegion.flip(false, true);

        Texture exploredFrameBufferTexture = exploredBuffer.getColorBufferTexture();
        exploredFrameBufferTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        exploredTextureRegion = new TextureRegion(exploredFrameBufferTexture);
        exploredTextureRegion.flip(false, true);

        resetWorldCamera();

        Texture fogMaskFrameBufferTexture = fogMaskBuffer.getColorBufferTexture();
        fogMaskFrameBufferTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        fogMaskTextureRegion = new TextureRegion(fogMaskFrameBufferTexture);
        fogMaskTextureRegion.flip(false, true);

        worldCamera.position.set(gameWidth/2f, gameHeight/2f, 1f);
        worldCamera.update();

        //DEBUG
        fogOfWar.addFogCircle(gameWidth/2f, gameHeight/2f, 300);
    }

    @Override
    public void update(float dt) {
        if (Gdx.app.getType() == Application.ApplicationType.Desktop && Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            Gdx.app.exit();
        }

        accum += dt;
        worldCamera.unproject(mousePos.set(Gdx.input.getX(), Gdx.input.getY(), 0));

        launcher.update(dt);

        if (Gdx.input.isTouched()){
            Vector3 touchPoint = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            worldCamera.unproject(touchPoint);
            fogOfWar.addFogCircle(touchPoint.x, touchPoint.y, 100);
        }

        fogOfWar.update(dt);
        earth.update(dt);
        playerShips.forEach(x -> x.update(dt));
        asteroids.forEach(Asteroid::update);

        cameraController.update(dt);
        super.update(dt);
    }

    @Override
    public void renderFrameBuffers(SpriteBatch batch) {
        fogOfWar.render(batch);

        fogMaskBuffer.begin();
        ScreenUtils.clear(Color.BLACK);
        batch.setProjectionMatrix(worldCamera.combined);
        batch.begin();
        batch.draw(fogOfWar.fogMaskTexture, 0, gameHeight, gameWidth, -gameHeight);
        batch.end();
        fogMaskBuffer.end();

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

        ShaderProgram fogShader = assets.fogOfWarShader;
        batch.setProjectionMatrix(windowCamera.combined);
        batch.setShader(fogShader);
        batch.begin();

        fogShader.setUniformf("u_time", fogOfWar.accum);
        fogShader.setUniformf("u_screenSize", worldCamera.viewportWidth, worldCamera.viewportHeight);
        fogShader.setUniformf("u_showFog", Config.Debug.general ? 0f : 1f);

        fogMaskTextureRegion.getTexture().bind(2);
        fogShader.setUniformi("u_texture2", 2);
        foggedTextureRegion.getTexture().bind(1);
        fogShader.setUniformi("u_texture1", 1);
        exploredTextureRegion.getTexture().bind(0);

        batch.draw(exploredTextureRegion.getTexture(), 0, worldCamera.viewportHeight, worldCamera.viewportWidth, -worldCamera.viewportHeight);

        batch.setShader(null);
        launcher.render(batch);
        if (Config.Debug.general) {
            batch.draw(fogOfWar.fogMaskTexture, 0, 0, windowCamera.viewportWidth / 6, windowCamera.viewportHeight / 6);
        }
        batch.end();
    }

    /**
     * Use this to render things that should be seen when fog is removed.
     * It is ok to render things that will be covered
     *
     * @param batch the Spritebatch to draw with
     */
    private void renderExploredArea(SpriteBatch batch) {
        ScreenUtils.clear(Color.BLACK);

        batch.setProjectionMatrix(worldCamera.combined);
        batch.begin();
        earth.render(batch);
        playerShips.forEach(ps -> ps.draw(batch));
        launcher.render(batch);
        asteroids.forEach(a -> a.draw(batch));

        // TEMP - 'world' bounds
        assets.shapes.rectangle(0, 0, gameWidth, gameHeight, Color.MAGENTA, 4);
        batch.end();
    }

    /**
     * This should render things that should be in the fagged area
     * Indicators of things to find etc
     * @param batch the SpriteBatch to draw with
     */
    private void renderFogArea(SpriteBatch batch) {
        ScreenUtils.clear(Color.BLACK);
        batch.setProjectionMatrix(worldCamera.combined);
        batch.begin();
        batch.draw(assets.pixel, 100, 100, 5, 5);

        // TEMP - 'world' bounds
        assets.shapes.rectangle(0, 0, gameWidth, gameHeight, Color.MAGENTA, 4);
        batch.end();

        // Background
        batch.setShader(assets.plasmaShader);
        batch.begin();
        assets.plasmaShader.setUniformf("u_time", accum);
        batch.draw(assets.noiseTexture, -200, -200, gameWidth + 400, gameHeight + 400);
        batch.end();
        batch.setShader(null);
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

    public void launchShip(float angle, float power) {
        var ship = new PlayerShip(assets);
        ship.launch(angle, power);
        playerShips.add(ship);
    }

}
