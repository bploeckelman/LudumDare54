package lando.systems.ld54.screens;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.utils.ScreenUtils;
import lando.systems.ld54.Config;
import lando.systems.ld54.fogofwar.FogOfWar;

public class GameScreen extends BaseScreen {

    public float gameWidth = Config.Screen.window_width * 3f;
    public float gameHeight = Config.Screen.framebuffer_height * 3f;

    FrameBuffer foggedBuffer;
    FrameBuffer exploredBuffer;
    TextureRegion foggedTextureRegion;
    TextureRegion exploredTextureRegion;
    FogOfWar fogOfWar;


    public GameScreen() {

        fogOfWar = new FogOfWar(gameWidth, gameHeight);

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

        worldCamera.position.set(gameWidth/2f, gameHeight/2f, 1f);
        worldCamera.update();
    }

    @Override
    public void update(float dt) {
        fogOfWar.update(dt);
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
        batch.end();
    }

    /**
     * Use this to render things that should be seen when fog is removed.
     * It is ok to render things that will be covered
     * @param batch
     */
    private void renderExploredArea(SpriteBatch batch) {
        ScreenUtils.clear(Color.BLACK);

        batch.setProjectionMatrix(worldCamera.combined);
        batch.begin();
        batch.draw(assets.pixelRegion, gameWidth/2f, gameHeight/2f, 5, 5);
        batch.end();
    }

    /**
     * This should render things that should be in the fagged ared
     * Indicators of things to find etc
     * @param batch
     */
    private void renderFogArea(SpriteBatch batch) {
        batch.setProjectionMatrix(worldCamera.combined);

    }
}
