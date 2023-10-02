package lando.systems.ld54.screens;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

public class EndScreen extends BaseScreen {

    public EndScreen() {


    }

    @Override
    public void alwaysUpdate(float delta) {

    }

    @Override
    public void renderFrameBuffers(SpriteBatch batch) {

    }

    @Override
    public void render(SpriteBatch batch) {
        ScreenUtils.clear(0.01f, 0.01f, 0.16f, 1f);

        batch.setProjectionMatrix(windowCamera.combined);
        batch.begin();
        {
            assets.font.draw(batch, "End Screen \n TODO", 400, 400);
        }
        batch.end();
    }
}
