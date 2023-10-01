package lando.systems.ld54.objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Rectangle;
import lando.systems.ld54.screens.BaseScreen;
import lando.systems.ld54.screens.GameScreen;

public class Background {

    BaseScreen screen;
    ShaderProgram starfieldShader;
    Rectangle bounds;
    float accum;

    public Background(BaseScreen screen, Rectangle bounds) {
        this.screen =  screen;
        starfieldShader = screen.assets.starfieldShader;
        this.bounds = bounds;
    }

    public void update(float dt) {
        accum += dt;
    }

    public void render(SpriteBatch batch, boolean isExplored) {
        batch.end();
        batch.setShader(starfieldShader);
        batch.begin();
        screen.assets.noiseTexture.bind(1);
        starfieldShader.setUniformi("u_texture1", 1);
        screen.assets.starsTexture.bind(0);
        starfieldShader.setUniformf("u_time", accum);
        starfieldShader.setUniformf("u_pos", screen.worldCamera.position);
        starfieldShader.setUniformf("u_nebula", isExplored ? 1f : 0f);
        batch.draw(screen.assets.starsTexture, 0, 0, bounds.width, bounds.height);

        batch.end();
        batch.setShader(null);
        batch.begin();
    }
}
