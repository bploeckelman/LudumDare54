package lando.systems.ld54.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import lando.systems.ld54.Stats;

public class StatsScreen extends BaseScreen{

    float CLICK_DELAY = 3f;

    float accum;
    BitmapFont font;
    GlyphLayout layout;
    Color continueColor = new Color();

    public StatsScreen() {
        font = assets.abandonedFont50;
        layout = assets.layout;

    }


    @Override
    public void alwaysUpdate(float delta) {
        accum += delta;


        if (Gdx.input.justTouched() && accum > CLICK_DELAY) {
            exitingScreen = true;
            game.setScreen(new CreditScreen(), assets.heartShader, 2f);
        }
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
            batch.draw(assets.pixel, 80, 70, windowCamera.viewportWidth - 160, windowCamera.viewportHeight - 140);
            batch.setColor(Color.WHITE);

            font.getData().setScale(1.8f);
            layout.setText(font, "Statistics", Color.WHITE, windowCamera.viewportWidth, Align.center, true);
            float y = windowCamera.viewportHeight - 100f;
            font.draw(batch, layout, 0, y);
            font.getData().setScale(.6f);
            y -= 200;
            drawText("Launches: " + Stats.numLaunches, 110, y, windowCamera.viewportWidth, Align.left);
            drawText("Ships Exploded: " + Stats.numShipsExploded, windowCamera.viewportWidth/2, y, windowCamera.viewportWidth, Align.left);
            y -= 70;
            drawText("Duration: " + (int)(Stats.runDuration/60) + " Minutes", 110, y, windowCamera.viewportWidth, Align.left);
            drawText("Derelict Ships: " + Stats.numShipsDerelict, windowCamera.viewportWidth/2, y, windowCamera.viewportWidth, Align.left);

            y -= 70;
            drawText("Astronauts Abandoned: " + Stats.numAstronautsEjected, 110, y, windowCamera.viewportWidth, Align.left);
            drawText("Encounters: " + Stats.numEncounters, windowCamera.viewportWidth/2, y, windowCamera.viewportWidth, Align.left);


            if (accum > CLICK_DELAY){
                continueColor.set(1f, 1f, 1f, MathUtils.sin(accum * 5f) * .5f + .5f);
                layout.setText(font, "click to continue", continueColor, windowCamera.viewportWidth, Align.center, true );
                font.draw(batch, layout, 0, 150);
            }
            font.getData().setScale(1f);
        }
        batch.end();
    }

    private void drawText (String string, float x, float y, float width, int align) {
        layout.setText(font, string, Color.WHITE, width, align, true);
        font.draw(batch, layout, x, y);

    }
}
