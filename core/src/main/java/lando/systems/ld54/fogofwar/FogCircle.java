package lando.systems.ld54.fogofwar;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;
import lando.systems.ld54.Main;

public class FogCircle extends FogObject {
    public float centerX;
    public float centerY;
    public float radius;

    private Texture texture;


    public FogCircle(float x, float y, float radius) {
        this.centerX = x;
        this.centerY = y;
        this.radius = radius;
        this.alpha = 0f;
        texture = Main.game.assets.whitePixel;

    }

    @Override
    public void render(SpriteBatch batch) {
        batch.setColor(alpha, radius/1000f, 1, alpha);
        batch.draw(texture, centerX - radius, centerY - radius, radius*2f, radius*2f);
        batch.setColor(Color.WHITE);
    }
}
