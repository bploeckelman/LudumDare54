package lando.systems.ld54.objects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import lando.systems.ld54.Assets;
import lando.systems.ld54.physics.Collidable;
import lando.systems.ld54.physics.CollisionShape;
import lando.systems.ld54.physics.CollisionShapeCircle;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class PlayerShipPart implements Collidable {

    public enum Type { nose, cabin, tail }

    public static final float RADIUS = 32;

    private static final float MASS = 4;
    private static final float DRAG_FRICTION = 0.5f;

    private final Vector2 pos;
    private final Vector2 vel;
    private final Rectangle collisionBounds;
    private final CollisionShapeCircle collisionShape;

    private final Animation<TextureRegion> anim;
    private float stateTime;
    private float rotation;
    private TextureRegion keyframe;

    public PlayerShipPart(Type type, Assets assets, float x, float y) {
        this.pos = new Vector2(x, y);
        this.vel = Vector2.Zero.cpy();
        this.collisionBounds = new Rectangle(pos.x - RADIUS, pos.y - RADIUS, RADIUS * 2, RADIUS * 2);
        this.collisionShape = new CollisionShapeCircle(RADIUS, pos.x, pos.y);
        this.anim = assets.playerShipParts.get(type);
        this.stateTime = 0;
        this.rotation = MathUtils.random(0, 360);
        this.keyframe = anim.getKeyFrames()[0];
    }

    public void update(float dt) {
        stateTime += dt;
    }

    public void draw(SpriteBatch batch) {
        keyframe = anim.getKeyFrame(stateTime);
        batch.draw(keyframe,
            pos.x - RADIUS,
            pos.y - RADIUS,
            RADIUS, RADIUS,
            RADIUS * 2f,
            RADIUS * 2f,
            1f, 1f,
            rotation
        );
    }

    @Override
    public void renderDebug(ShapeDrawer shapes) {
        shapes.setColor(Color.CORAL);
        shapes.circle(pos.x, pos.y, RADIUS, 2f);
        shapes.setColor(Color.WHITE);
    }

    @Override
    public float getFriction() {
        return DRAG_FRICTION;
    }

    @Override
    public float getMass() {
        return MASS;
    }

    @Override
    public Vector2 getVelocity() {
        return vel;
    }

    @Override
    public void setVelocity(Vector2 newVel) {
        vel.set(newVel);
    }

    @Override
    public void setVelocity(float x, float y) {
        vel.set(x, y);
    }

    @Override
    public Vector2 getPosition() {
        return pos;
    }

    @Override
    public void setPosition(float x, float y) {
        pos.set(x, y);
        collisionShape.center.set(pos);
        collisionBounds.setPosition(pos.x - RADIUS, pos.y - RADIUS);
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
