package lando.systems.ld54.fogofwar;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.ScreenUtils;
import lando.systems.ld54.Main;

import java.util.ArrayList;

public class FogOfWar {
    private final FrameBuffer fogMaskBuffer;
    public final Texture fogMaskTexture;
    private final OrthographicCamera fogMaskCamera;

    private final float gameWidth;
    private final float gameHeight;

    private final float scale = .25f;

    private ArrayList<FogObject> fogObjects;
    public float accum;

    public FogOfWar(float gameWidth, float gameHeight) {
        fogObjects = new ArrayList<>();
        accum = 0;
        this.gameWidth = gameWidth;
        this.gameHeight = gameHeight;

        fogMaskBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, (int)(gameWidth * scale), (int)(gameHeight * scale), true);
        fogMaskTexture = fogMaskBuffer.getColorBufferTexture();
        fogMaskTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        fogMaskCamera = new OrthographicCamera(gameWidth * scale, gameHeight * scale);
        fogMaskCamera.position.set(fogMaskCamera.viewportWidth/2f, fogMaskCamera.viewportHeight/2f, 1f);
        fogMaskCamera.update();

        fogMaskBuffer.begin();
        ScreenUtils.clear(0,0,0,0);
        fogMaskBuffer.end();
    }

    public void update(float dt) {
        accum += dt;
        for (int i = fogObjects.size() -1 ; i >= 0; i--) {
            FogObject o = fogObjects.get(i);
            o.alpha += dt * 2f;

        }
    }

    public void render(SpriteBatch batch) {
        fogMaskBuffer.begin();
        ShaderProgram fogObjectShader = Main.game.assets.fogObjectShader;
        batch.setProjectionMatrix(fogMaskCamera.combined);
        batch.setShader(fogObjectShader);
        batch.begin();
        fogObjectShader.setUniformf("u_screenBounds", fogMaskCamera.viewportWidth, fogMaskCamera.viewportHeight);
        Gdx.gl.glEnable(GL30.GL_BLEND);
        Gdx.gl.glBlendEquation(GL30.GL_MAX);
//        batch.setBlendFunction(GL30.GL_SRC_ALPHA, GL30.GL_DST_ALPHA);
//        batch.setBlendFunctionSeparate(GL30.GL_MAX, GL30.GL_MAX, GL30.GL_MAX, GL30.GL_MAX);
        for (int i = fogObjects.size()-1; i >= 0; i--){
            FogObject o = fogObjects.get(i);
            o.render(batch);
            if (o.alpha >= 1f) fogObjects.remove(o);
        }
        batch.setBlendFunction(GL30.GL_SRC_ALPHA, GL30.GL_ONE_MINUS_SRC_ALPHA);
        batch.setShader(null);
        batch.end();
        fogMaskBuffer.end();
        Gdx.gl.glBlendEquation(GL30.GL_FUNC_ADD);

    }

    public void addFogCircle(float x, float y, float radius) {
        fogObjects.add(new FogCircle(x * scale, y * scale, radius * scale));
    }
}
