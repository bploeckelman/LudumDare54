package lando.systems.ld54.screens;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import lando.systems.ld54.Assets;
import lando.systems.ld54.Config;
import lando.systems.ld54.assets.Asteroids;
import lando.systems.ld54.components.DragLauncher;
import lando.systems.ld54.fogofwar.FogOfWar;
import lando.systems.ld54.objects.Asteroid;
import lando.systems.ld54.objects.PlayerShip;
import lando.systems.ld54.utils.camera.PanZoomCameraController;

public class GameScreen extends BaseScreen {

    public static float gameWidth = Config.Screen.window_width * 3f;
    public static float gameHeight = Config.Screen.framebuffer_height * 3f;

    public final Vector3 mousePos = new Vector3();

    DragLauncher launcher;

    FrameBuffer foggedBuffer;
    FrameBuffer exploredBuffer;
    FrameBuffer fogMaskBuffer;
    TextureRegion foggedTextureRegion;
    TextureRegion exploredTextureRegion;
    TextureRegion fogMaskTextureRegion;
    FogOfWar fogOfWar;
    Earth earth;
    PlayerShip playerShip;
    Array<Asteroid> asteroids;
    PanZoomCameraController cameraController;

    public GameScreen() {
        launcher = new DragLauncher(this);
        fogOfWar = new FogOfWar(gameWidth, gameHeight);
        earth = new Earth(assets);
        playerShip = new PlayerShip(assets);
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

        windowCamera.unproject(mousePos.set(Gdx.input.getX(), Gdx.input.getY(), 0));
        launcher.update(dt);
        if (Gdx.input.isTouched()){
            Vector3 touchPoint = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            worldCamera.unproject(touchPoint);
            fogOfWar.addFogCircle(touchPoint.x, touchPoint.y, 100);
        }

        fogOfWar.update(dt);
        earth.update(dt);
        playerShip.update(dt);
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

        fogMaskTextureRegion.getTexture().bind(2);
        fogShader.setUniformi("u_texture2", 2);
        foggedTextureRegion.getTexture().bind(1);
        fogShader.setUniformi("u_texture1", 1);
        exploredTextureRegion.getTexture().bind(0);

        batch.draw(exploredTextureRegion.getTexture(), 0, worldCamera.viewportHeight, worldCamera.viewportWidth, -worldCamera.viewportHeight);
        batch.setShader(null);

        launcher.render(batch);

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
        earth.draw(batch, gameWidth / 2f, gameHeight / 2f);
        playerShip.draw(batch);
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
        ScreenUtils.clear(Color.DARK_GRAY);
        batch.setProjectionMatrix(worldCamera.combined);
        batch.begin();
        // TEMP - 'world' bounds
        assets.shapes.rectangle(0, 0, gameWidth, gameHeight, Color.MAGENTA, 4);
        batch.end();
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

    // ------------------------------------------------------------------------
    // Convenience data structures - extract to their own class if they get big
    // ------------------------------------------------------------------------

    static class Earth {
        Animation<TextureRegion> anim;
        TextureRegion keyframe;
        float animState;
        float size = 96;

        Earth(Assets assets) {
            this.anim = assets.earthSpin;
            this.keyframe = anim.getKeyFrames()[0];
            this.animState = 0;
        }

        void update(float dt) {
            animState += dt;
            keyframe = anim.getKeyFrame(animState);
        }

        void draw(SpriteBatch batch, float x, float y) {
            var centerX = x - size / 2f;
            var centerY = y - size / 2f;
            batch.draw(keyframe, centerX, centerY);
        }
    }

}
