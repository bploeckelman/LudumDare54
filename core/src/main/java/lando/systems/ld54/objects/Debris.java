package lando.systems.ld54.objects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import lando.systems.ld54.physics.Collidable;
import lando.systems.ld54.physics.CollisionShape;
import lando.systems.ld54.physics.CollisionShapeCircle;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class Debris implements Collidable {

    private final Vector2 pos;
    private final Vector2 vel;
    private final Rectangle collisionBounds;
    private final CollisionShapeCircle collisionShape;
    private final Animation<TextureRegion> anim;

    private TextureRegion keyframe;
    private float stateTime;
    private float rotation;

    // Override default values in subclasses for different behavior
    public float mass = 4;
    public float dragFriction = 0.5f;
    public float angularMomentum = 0;

    // This can also be overridden, but it has to be changed through a setter
    // because it ties to collision bounds and shape
    private float radius = 32;

    public Debris(Animation<TextureRegion> anim, float x, float y) {
        this.pos = new Vector2(x, y);
        this.vel = Vector2.Zero.cpy();
        this.collisionBounds = new Rectangle(pos.x - radius, pos.y - radius, radius * 2, radius * 2);
        this.collisionShape = new CollisionShapeCircle(radius, pos.x, pos.y);
        this.anim = anim;
        this.keyframe = anim.getKeyFrames()[0];
        this.stateTime = 0;
        this.rotation = MathUtils.random(0, 360);
    }

    // TODO - might need to separate sprite radius (size) from collision radius,
    //  but this if fine for now just to have a way to modify it per instance
    public void setRadius(float newRadius) {
        radius = newRadius;
        this.collisionBounds.set(pos.x - radius, pos.y - radius, radius * 2, radius * 2);
        this.collisionShape.set(radius, pos.x, pos.y);
    }

    public void update(float dt) {
        stateTime += dt;
        rotation += dt * angularMomentum;
    }

    public void draw(SpriteBatch batch) {
        keyframe = anim.getKeyFrame(stateTime);
        batch.draw(keyframe,
            pos.x - radius,
            pos.y - radius,
            radius, radius,
            radius * 2f,
            radius * 2f,
            1f, 1f,
            rotation
        );
    }

    @Override
    public void renderDebug(ShapeDrawer shapes) {
        shapes.setColor(Color.CORAL);
        shapes.circle(pos.x, pos.y, radius, 2f);
        shapes.setColor(Color.WHITE);
    }

    @Override
    public float getFriction() {
        return dragFriction;
    }

    @Override
    public float getMass() {
        return mass;
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
        collisionBounds.setPosition(pos.x - radius, pos.y - radius);
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
        // NOTE - callback on collision so something can be done with whatever was collided with
    }

    @Override
    public boolean shouldCollideWith(Collidable object) {
        return true;
    }

}
