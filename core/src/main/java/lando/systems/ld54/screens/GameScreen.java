package lando.systems.ld54.screens;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.utils.ScreenUtils;
import lando.systems.ld54.Config;

public class GameScreen extends BaseScreen {

    FrameBuffer foggedBuffer;
    FrameBuffer exploredBuffer;
    TextureRegion foggedTextureRegion;
    TextureRegion exploredTextureRegion;

    public GameScreen() {

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


    }

    @Override
    public void update(float dt) {

    }

    @Override
    public void renderFrameBuffers(SpriteBatch batch) {
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

        batch.begin();
        batch.draw(exploredTextureRegion.getTexture(), 0, 0, worldCamera.viewportWidth, worldCamera.viewportHeight);
        batch.end();
    }

    /**
     * Use this to render things that should be seen when fog is removed.
     * It is ok to render things that will be covered
     * @param batch
     */
    private void renderExploredArea(SpriteBatch batch) {
        ScreenUtils.clear(Color.BLACK);

        batch.begin();
        batch.draw(assets.pixelRegion, worldCamera.viewportWidth/2f, worldCamera.viewportHeight/2f, 5, 5);
        batch.end();
    }

    /**
     * This should render things that should be in the fagged ared
     * Indicators of things to find etc
     * @param batch
     */
    private void renderFogArea(SpriteBatch batch) {

    }
}
