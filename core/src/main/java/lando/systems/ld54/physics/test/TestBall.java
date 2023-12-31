package lando.systems.ld54.physics.test;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import lando.systems.ld54.physics.Collidable;
import lando.systems.ld54.physics.CollisionShape;
import lando.systems.ld54.physics.CollisionShapeCircle;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class TestBall implements Collidable {
    float RADIUS = 10f;
    float COLLISION_MARGIN = 10f;
    Vector2 position;
    Vector2 velocity;
    float friction;
    float mass;
    CollisionShapeCircle collisionShape;
    Rectangle collisionBounds;


    public TestBall(Vector2 pos, Vector2 velocity) {
        this.position = new Vector2(pos);
        this.velocity = new Vector2(velocity);
        this.mass = MathUtils.random(1f, 10f);
        this.friction = MathUtils.random(.5f, .99f);
        this.collisionShape = new CollisionShapeCircle(RADIUS, position.x, position.y);
        this.collisionBounds = new Rectangle(position.x - RADIUS - COLLISION_MARGIN, position.y - RADIUS - COLLISION_MARGIN, (RADIUS+COLLISION_MARGIN)*2f , (RADIUS+COLLISION_MARGIN)*2f);
    }

    public void debugRender(SpriteBatch batch) {
        collisionShape.debugRender(batch);
    }

    private static final Color debugColor = new Color(1, 165f / 255f, 0, 0.5f); // Color.ORANGE half alpha
    @Override
    public void renderDebug(ShapeDrawer shapes) {
        shapes.setColor(debugColor);
        shapes.circle(
            collisionShape.center.x,
            collisionShape.center.y,
            collisionShape.radius,
            2f);
        shapes.setColor(Color.WHITE);
    }

    @Override
    public float getMass() {
        return mass;
    }

    @Override
    public float getFriction() {
        return friction;
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
        return position;
    }

    @Override
    public void setPosition(float x, float y) {
        position.set(x, y);
        collisionShape.center.set(position);
        this.collisionBounds = new Rectangle(position.x - RADIUS, position.y - RADIUS, RADIUS*2f, RADIUS*2f);
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
        // TODO pick up, make sound, that stuff lives here
        // Need to check what type it is
    }

    @Override
    public boolean shouldCollideWith(Collidable object) {
        if (object instanceof TestWallSegment) return true;
        return true;
    }
}
