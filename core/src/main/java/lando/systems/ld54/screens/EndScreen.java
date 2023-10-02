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
    String text = "{GRADIENT=purple;cyan}Lorem ipsum{ENDGRADIENT} dolor sit amet, consectetur adipiscing elit. Mauris gravida sem lectus, " +
        "vel convallis dolor tristique et. In a orci eget augue ornare blandit in " +
        "ac nulla. Ut id ante id augue imperdiet dapibus a id metus. Vivamus " +
        "in purus id enim imperdiet interdum in sed lorem. Donec quis sagittis purus. Integer iaculis auctor viverra. " +
        "Proin tempus consectetur diam, a iaculis erat dapibus sit amet. Integer eu sagittis orci, a scelerisque dolor. Morbi sollicitudin enim orci, at facilisis erat iaculis sit amet. Etiam sed nibh venenatis, fringilla enim nec, sodales lorem. Aliquam aliquam nunc ligula, ac sagittis purus vehicula vel.\n" +
        "\n" +
        "Quisque posuere, lectus et lobortis lobortis, odio arcu molestie erat, quis interdum ex lacus non sapien. Vestibulum lectus leo, consectetur eu diam quis, auctor ultrices nunc. Morbi nisi enim, cursus ut accumsan ut, tristique eget purus. Quisque nisi ex, interdum eu luctus sit amet, ullamcorper in nisi. Cras malesuada est nec tortor imperdiet, eu cursus nulla ullamcorper. Suspendisse porta id nibh nec sollicitudin. Ut risus nulla, pretium in ultricies sed, auctor sed eros. Vestibulum lacinia consequat commodo. Nam consequat mauris non porttitor mattis. Vestibulum sed fermentum nisl. Phasellus erat urna, elementum nec fringilla id, molestie ac eros. Sed ut sem sollicitudin, commodo libero et, luctus nisi. Vestibulum elementum justo id varius sollicitudin. Nunc scelerisque libero nibh, dapibus lacinia mauris iaculis a. Cras vitae hendrerit nibh. Quisque eget egestas elit.\n" +
        "\n" +
        "Vivamus diam velit, viverra quis faucibus at, vehicula cursus est. Mauris consequat ipsum eu tellus tristique varius. Donec sit amet lorem placerat dolor porttitor convallis ut sed tellus. ";
    TypingLabel label;
    GlyphLayout layout;

    public EndScreen() {
        float scale = 1f;
        boolean gettingFix = true;
        int fuckingInfiniteLoops = 0;
        font = assets.smallFont;
        layout = new GlyphLayout();
        layout.setText(font, text, Color.WHITE, windowCamera.viewportWidth - 200, Align.center, true);

        scale = (windowCamera.viewportHeight - 200f) / layout.height;
        if (scale >1f) {
            scale = 1f;
        }
        font.getData().setScale(scale);
        layout.setText(font, text, Color.WHITE, windowCamera.viewportWidth - 200, Align.center, true);
        font.getData().setScale(1f);

        label = new TypingLabel(font, text, 100, windowCamera.viewportHeight/2f + layout.height/2f);
        label.setWidth(windowCamera.viewportWidth - 200);
        label.setFontScale(scale);


    }

    @Override
    public void alwaysUpdate(float delta) {
        accum += delta;
        float timeScale = 1f;
        if (Gdx.input.justTouched() && label.hasEnded()){
            game.setScreen(new CreditScreen());
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
        batch.draw(assets.wormholdTexture, 0, 0, windowCamera.viewportWidth, windowCamera.viewportHeight);
        batch.end();
        batch.setShader(null);

        batch.begin();
        {
            batch.setColor(0f, 0f, 0f, .4f);
            batch.draw(assets.pixel, 80, windowCamera.viewportHeight/2f - layout.height/2f - 20, windowCamera.viewportWidth - 160, layout.height + 40);
            batch.setColor(Color.WHITE);
            label.render(batch);
        }
        batch.end();
    }
}
