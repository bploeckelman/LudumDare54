package lando.systems.ld54.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import lando.systems.ld54.Main;

public class TitleScreen extends BaseScreen {

    private boolean drawUI = false;
    private Texture background;


    public TitleScreen() {
        background = Main.game.assets.gdx;
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
            batch.draw(background, 50, 200, width - 100, height - 400);

        }

        batch.end();
        if (drawUI) {
            uiStage.draw();
        }
    }
}
