package lando.systems.ld54.objects;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.equations.Linear;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import lando.systems.ld54.Main;
import lando.systems.ld54.physics.Collidable;
import lando.systems.ld54.physics.CollisionShape;
import lando.systems.ld54.physics.CollisionShapeCircle;
import lando.systems.ld54.utils.accessors.Vector2Accessor;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class Asteroid implements Collidable {


    public TextureRegion region;
    public Vector2 initialPos;
    public Vector2 initialSize;

    public Vector2 pos;
    public Vector2 velocity;
    public Vector2 size;
    public Vector2 range;

    private final Rectangle collisionBounds;
    private final CollisionShapeCircle collisionShape;

    private Tween floatTween;
    private boolean updateFloat;

    // TODO - support rotation, tweening, etc...
    private static final float default_range = 100;
    private static final float min_duration = 1;
    private static final float max_duration = 5;

    public Asteroid(TextureRegion region, float x, float y) {
        this.region = region;
        this.initialPos = new Vector2(x, y);
        this.initialSize = new Vector2(region.getRegionWidth(), region.getRegionHeight());
        this.pos = initialPos.cpy();
        this.size = initialSize.cpy();
        this.velocity = new Vector2(0, 0);
        this.range = new Vector2(default_range, default_range);
        this.updateFloat = false;
        this.collisionBounds = new Rectangle(pos.x - size.x/2, pos.y - size.y /2, size.x, size.y);
        this.collisionShape = new CollisionShapeCircle(size.x/2f, pos.x, pos.y);
//        updateFloatTween();
    }

//    public Asteroid(TextureRegion region, float x, float y, float w, float h) {
//        this.region = region;
//        this.initialPos = new Vector2(x, y);
//        this.initialSize = new Vector2(w, h);
//        this.pos = initialPos.cpy();
//        this.size = initialSize.cpy();
//        this.range = new Vector2(default_range, default_range);
//        this.updateFloat = false;
//        updateFloatTween();
//    }

    public void draw(SpriteBatch batch) {
        batch.draw(region, pos.x - size.x/2f, pos.y - size.y/2f, size.x, size.y);
    }

    public void update() {

    }


    @Override
    public void renderDebug(ShapeDrawer shapes) {
        shapes.filledCircle(collisionShape.center, collisionShape.radius, Color.MAGENTA);
    }

    @Override
    public float getFriction() {
        return 0.9f;
    }

    @Override
    public float getMass() {
        return 1;
    }

    @Override
    public Vector2 getVelocity() {
        return velocity;
    }

    @Override
    public void setVelocity(Vector2 newVel) {
        velocity.set(newVel);
    }

    @Override
    public void setVelocity(float x, float y) {
        velocity.set(x, y);
    }

    @Override
    public Vector2 getPosition() {
        return pos;
    }

    @Override
    public void setPosition(float x, float y) {
        collisionShape.center.set(x, y);
        collisionBounds.setPosition(x - size.x/2f, y - size.y/2f);
        pos.set(x, y);
    }

    @Override
    public void setPosition(Vector2 newPos) {
        setPosition(newPos.x, newPos.y);
    }

    @Override
    public Rectangle getCollisionBounds() {
        return collisionBounds;
    }

    @Override
    public CollisionShape getCollisionShape() {
        return collisionShape;
    }

    @Override
    public void collidedWith(Collidable object) {

    }

    @Override
    public boolean shouldCollideWith(Collidable object) {
        return true;
    }
}
