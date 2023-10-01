package lando.systems.ld54.fogofwar;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import lando.systems.ld54.Main;

public class FogRectangle extends FogObject {
    public float x;
    public float y;
    public float width;
    public float height;


    private Texture texture;

    public FogRectangle(float x, float y, float width, float height, float transitionScale) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.alpha = 0f;
        this.transitionScale = transitionScale;
        texture = Main.game.assets.whitePixel;
    }


    @Override
    public void render(SpriteBatch batch) {
        batch.setColor(width/3000f, height/3000f, 0, alpha);
        batch.draw(texture, x, y, width, height);
        batch.setColor(Color.WHITE);
    }
}
