package lando.systems.ld54.objects;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.equations.Linear;
import com.badlogic.gdx.Gdx;
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
import lando.systems.ld54.physics.influencers.JunkInfluencible;
import lando.systems.ld54.screens.GameScreen;
import lando.systems.ld54.utils.accessors.Vector2Accessor;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class Asteroid implements Collidable, JunkInfluencible {

    GameScreen screen;
    public float friction = .7f;
    public float mass;
    public float health;

    public TextureRegion region;
    public Vector2 initialPos;
    public Vector2 initialSize;

    public Vector2 pos;
    public Vector2 velocity;
    public Vector2 size;
    public Vector2 range;
    private float rotationSpeed;

    private final Rectangle collisionBounds;
    private final CollisionShapeCircle collisionShape;

    public boolean alive;

    private Tween floatTween;
    private boolean updateFloat;

    // TODO - support rotation, tweening, etc...
    private static final float default_range = 100;
    private static final float min_duration = 1;
    private static final float max_duration = 5;

    public Asteroid(GameScreen screen, TextureRegion region, float x, float y) {
        this.screen = screen;
        this.region = region;
        this.initialPos = new Vector2(x, y);
        this.initialSize = new Vector2(region.getRegionWidth(), region.getRegionHeight());
        this.pos = initialPos.cpy();
        this.size = initialSize.cpy();
        this.mass = size.x * size.y / 600f;
        this.health = mass * 2f;
        this.velocity = new Vector2(0, 0);
        this.range = new Vector2(default_range, default_range);
        this.updateFloat = false;
        this.alive = true;
        this.collisionBounds = new Rectangle(pos.x - size.x/2, pos.y - size.y /2, size.x, size.y);
        this.collisionShape = new CollisionShapeCircle(size.x/2f, pos.x, pos.y);
        this.rotationSpeed = MathUtils.random(-30f, 30f);
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

    private float animTime = 0;

    public void draw(SpriteBatch batch) {
        float rotation = animTime * rotationSpeed;
        batch.draw(region, pos.x - size.x/2f, pos.y - size.y/2f, size.x / 2, size.y / 2, size.x, size.y, 1, 1, rotation);
    }

    public void update(float dt) {
        animTime += dt;
        if (health <= 0){
            // remove from list
            screen.particles.debrisExplode(pos.x, pos.y);
            alive = false;
        }
        // kill if off-screen
        if (pos.x - size.x / 2f < 0
         || pos.y - size.y / 2f < 0
         || pos.x + size.x / 2f > GameScreen.gameWidth
         || pos.y + size.y / 2f > GameScreen.gameHeight) {
            alive = false;
        }
    }

    @Override
    public void renderDebug(ShapeDrawer shapes) {
        shapes.filledCircle(collisionShape.center, collisionShape.radius, Color.MAGENTA);
    }

    @Override
    public float getFriction() {
        return friction;
    }

    @Override
    public float getMass() {
        return mass;
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

    Vector2 tempVec = new Vector2();
    @Override
    public void collidedWith(Collidable object) {
        if (object instanceof PlayerShip){
            float dotProduct = Math.abs(tempVec.set(object.getPosition()).sub(pos).nor().dot(object.getVelocity()));
            health -= dotProduct * .07f;
            Gdx.app.log("Asteroid", "Asteroid Health: " +health );
        } else if (object instanceof Asteroid) {

        }
    }

    @Override
    public boolean shouldCollideWith(Collidable object) {
        return true;
    }

    @Override
    public boolean canBeInfluenced() { return true; }
}
