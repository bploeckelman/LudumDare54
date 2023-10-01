package lando.systems.ld54.objects;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class Planet {
    private final Animation<TextureRegion> planetAnimation;
    private TextureRegion keyframe;
    public final float size;
    public final Vector2 centerPosition = new Vector2();

    private float animState = 0;
    private final Circle bounds;

    // offset is space around image
    public Planet(Animation<TextureRegion> animation, float size, float offset) {
        this.planetAnimation = animation;
        this.keyframe = planetAnimation.getKeyFrames()[0];
        this.size = size;
        this.bounds = new Circle(0, 0, (size - (offset*2))/2);
    }

    public void setPosition(float x, float y) {
        centerPosition.set(x, y);
        bounds.setPosition(x, y);
    }

    public boolean contains(Vector3 worldPosition) {

        return bounds.contains(worldPosition.x, worldPosition.y);
    }

    public void update(float dt) {
        animState += dt;
        keyframe = planetAnimation.getKeyFrame(animState);
    }

    public void render(SpriteBatch batch) {
        var centerX = centerPosition.x - size / 2f;
        var centerY = centerPosition.y - size / 2f;
        batch.draw(keyframe, centerX, centerY, size, size);
    }
}
