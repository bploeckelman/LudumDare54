package lando.systems.ld54.fogofwar;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;

public class FogOfWar {
    private FrameBuffer fogMaskBuffer;
    public Texture fogMaskTexture;
    private OrthographicCamera fogMaskCamera;

    private float gameWidth;
    private float gameHeight;

    private float scale = .25f;

    public FogOfWar(float gameWidth, float gameHeight) {
        this.gameWidth = gameWidth;
        this.gameHeight = gameHeight;

        fogMaskBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, (int)(gameWidth * scale), (int)(gameHeight * scale), true);
        fogMaskTexture = fogMaskBuffer.getColorBufferTexture();
        fogMaskTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
    }

    public void update(float dt) {

    }

    public void render(SpriteBatch batch) {

    }
}
