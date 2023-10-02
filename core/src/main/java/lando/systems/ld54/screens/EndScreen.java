package lando.systems.ld54.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import lando.systems.ld54.utils.typinglabel.TypingLabel;

public class EndScreen extends BaseScreen {

    float accum;
    BitmapFont font;
    String text = "It was touch and go there for a while, but {GRADIENT=gold;brown}we made it{ENDGRADIENT}!\n" +
        "\n" +
        "That floating pod-city-terrarium thing really saved our bacon.\n" +
        "\n" +
        "To be real, the whole experience, with all the ships and starting a new civilization... It felt kind of god-like . . . maybe we should call this new place \"Deus\".\n" +
        "\n" +
        "And as a cyborg-planet thing, it's kind mechanical too.\n" +
        "\n" +
        "\"Deus Ex Machina\" -  kind of a nice ring to it - maybe that's what we call it?" +
        "\n\n" +
        "Either way, it's been real, folks.\n\n" +
        "Thanks for helping us figure out how to navigate the limited space we had to work with." +
        "\n\n" +
        "This truly has been a trip through {GRADIENT=gold;brown}The Finite Frontier{ENDGRADIENT}."
       ;
    TypingLabel label;
    GlyphLayout layout;

    public EndScreen() {
        float scale = 1f;
        boolean gettingFix = true;
        int fuckingInfiniteLoops = 0;
        font = assets.abandonedFont20;
        layout = new GlyphLayout();
        layout.setText(font, text, Color.WHITE, windowCamera.viewportWidth - 200, Align.topLeft, true);

        scale = (windowCamera.viewportHeight - 200f) / layout.height;
        if (scale >1f) {
            scale = 1f;
        }
        font.getData().setScale(scale);
        layout.setText(font, text, Color.WHITE, windowCamera.viewportWidth - 200, Align.left, true);
        font.getData().setScale(1f);

        label = new TypingLabel(font, text, 100, windowCamera.viewportHeight/2f + layout.height/2f + 50);
        label.setWidth(windowCamera.viewportWidth - 200);
        label.setLineAlign(Align.left);
        font.getData().setLineHeight(30f);
        ;
        label.setFontScale(scale);


    }

    @Override
    public void alwaysUpdate(float delta) {
        accum += delta;
        float timeScale = 1f;
        if (Gdx.input.justTouched() && label.hasEnded() && !exitingScreen){
            exitingScreen = true;
            game.setScreen(new StatsScreen(), assets.dreamyShader, 1.5f);
        }
        if (Gdx.input.isTouched()) {
            timeScale = 8f;
        }
        label.update(delta * timeScale);

    }

    @Override
    public void renderFrameBuffers(SpriteBatch batch) {

    }

    @Override
    public void render(SpriteBatch batch) {
        ScreenUtils.clear(0.01f, 0.01f, 0.16f, 1f);

        batch.setProjectionMatrix(windowCamera.combined);
        ShaderProgram wormHoleShader = assets.wormholeShader;

        batch.setShader(wormHoleShader);
        batch.begin();
        wormHoleShader.setUniformf("u_time", accum);
        batch.draw(assets.wormholeTexture, 0, 0, windowCamera.viewportWidth, windowCamera.viewportHeight);
        batch.end();
        batch.setShader(null);

        batch.begin();
        {
            batch.setColor(0f, 0f, 0f, .6f);
            batch.draw(assets.pixel, 80, windowCamera.viewportHeight/2f - layout.height/2f - 70, windowCamera.viewportWidth - 160, layout.height + 140);
            batch.setColor(Color.WHITE);
            label.render(batch);
        }
        batch.end();
    }
}
