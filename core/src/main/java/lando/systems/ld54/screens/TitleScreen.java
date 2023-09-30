package lando.systems.ld54.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import lando.systems.ld54.Main;
import lando.systems.ld54.ui.TitleScreenUI;

public class TitleScreen extends BaseScreen {

    private final boolean drawUI = true;
    private Texture background;


    public TitleScreen() {
        background = Main.game.assets.gdx;
        Gdx.input.setInputProcessor(uiStage);
    }

    @Override
    public void update(float dt) {
        super.update(dt);
    }

    @Override
    public void renderFrameBuffers(SpriteBatch batch) {

    }

    @Override
    public void render(SpriteBatch batch) {
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);

        batch.setProjectionMatrix(worldCamera.combined);
        batch.begin();
        {
            float width = worldCamera.viewportWidth;
            float height = worldCamera.viewportHeight;
            //batch.draw(background, 50, 200, width - 100, height - 400);
            // draw text "LD45" with font assets.freeTypeFont
            batch.setColor(Color.RED);
            assets.abandonedFont50.draw(batch, "LD54", 50, 100);
            batch.setColor(Color.WHITE);
        }

        batch.end();
        if (drawUI) {
            uiStage.draw();
        }
    }

    @Override
    public void initializeUI() {
        super.initializeUI();
        TitleScreenUI titleScreenUI = new TitleScreenUI(this);
        uiStage.addActor(titleScreenUI);
    }
}
