package lando.systems.ld54.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import lando.systems.ld54.Main;
import lando.systems.ld54.physics.Collidable;
import lando.systems.ld54.physics.CollisionShape;
import lando.systems.ld54.physics.CollisionShapeCircle;
import lando.systems.ld54.screens.GameScreen;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class PlayerShip implements Collidable {

    private static final float DRAG_FRICTION = 0.4f;
    private static final float FUEL_PER_BAR_LEVEL = 300f;

    private float mass = 100;

    private Animation<TextureRegion> anim;
    private TextureRegion keyframe;
    private float animState;

    public boolean trackMovement = false;
    public int currentSector = -1;

    private final Rectangle collisionBounds;
    private final CollisionShapeCircle collisionShape;

    // TODO - should pos be center and we offset by half-size in draw()?
    //  or should pos be bottom left and offset by half-size in getCenter()?
    //  or should we use a Circle object and use circle .pos / .radius to find draw corner?

    public Vector2 pos;
    public Vector2 vel;
    public Vector2 size;
    public float fuel;
    public float rotation; // relative to orientation in texture, if facing right, no adjustment needed for angle values

    private final GameScreen screen;

    public PlayerShip(GameScreen gameScreen) {
        this.screen = gameScreen;
        this.anim = screen.assets.playerShipActive;
        this.animState = 0;
        this.pos = new Vector2();
        this.vel = new Vector2();
        this.size = new Vector2();
        this.rotation = 0;


        // TODO - this position could be set via the center of the homeSector (passed in)
        // TEMP - manually set initial position and size for now
        this.pos.set(GameScreen.gameWidth / 2f, GameScreen.gameHeight / 2f);
        this.size.set(128, 128);

        this.collisionBounds = new Rectangle(pos.x - size.x/3f, pos.y - size.y /3f, size.x * 2f/3f, size.y * .66f);
        this.collisionShape = new CollisionShapeCircle(size.x /3f, pos.x, pos.y);
        this.fuel = Player.fuelLevel * FUEL_PER_BAR_LEVEL;
    }

    public void update(float dt) {
        // update animation
        animState += dt;
        keyframe = anim.getKeyFrame(animState);
        if (fuel <= 0) {
            Main.game.assets.engineRunning.stop();
            this.anim = screen.assets.playerShip;
        }

        fuel = Math.max(0, fuel - vel.len() * dt);
//        if (vel.isZero()) { return; }

//        // integrate velocity into position
//        pos.x += dt * vel.x;
//        pos.y += dt * vel.y;
//
//        // slow down over time
//        vel.scl(DRAG_FRICTION);

        float curVelocity = vel.len2();
        if (curVelocity < 1f) {
            trackMovement = false;
//            vel.setZero();
            screen.cameraController.targetPos.set(GameScreen.gameWidth/2f, GameScreen.gameHeight/2f, 0);

            explode();

//            Main.game.audioManager.stopSound(AudioManager.Sounds.engineRunning)
        } else if (curVelocity < 200f) {
            trackMovement = false;

            Gdx.app.log("Stopping", "stopping");
        } else {
            // get rotation based on velocity
            rotation = vel.angleDeg();
            float fogClearRadius = 200f;
            if (fuel > 0){
                fogClearRadius += MathUtils.clamp(10000f / vel.len(), 0, 200f);
            }
            screen.fogOfWar.addFogCircle(pos.x, pos.y, fogClearRadius, vel.len()/ 100f);
        }
    }

    public void draw(SpriteBatch batch) {
        batch.draw(keyframe,
            pos.x - size.x / 2f,
            pos.y - size.y / 2f,
            size.x / 2f,
            size.y / 2f,
            size.x, size.y,
            1, 1,
            rotation
        );
    }

    public void launch(float angle, float power) {
        trackMovement = true;
        vel.set(1, 0).setAngleDeg(angle).scl(power);
    }

    public void explode() {
        // TODO - particle effect
        // TODO - sound effect

        // instantiate the ship parts
        // TODO - maybe do something cutesy where we line them up all nice then blast them apart,
        //   but for now just get them onscreen
        var nose = new PlayerShipPart(PlayerShipPart.Type.nose, screen.assets, pos.x, pos.y);
        var cabin1 = new PlayerShipPart(PlayerShipPart.Type.cabin, screen.assets, pos.x, pos.y);
        var cabin2 = new PlayerShipPart(PlayerShipPart.Type.cabin, screen.assets, pos.x, pos.y);
        var tail = new PlayerShipPart(PlayerShipPart.Type.tail, screen.assets, pos.x, pos.y);
        // placeholder ranges just to have them move a bit on spawn
        var angle1 = MathUtils.random(0, 360);
        var angle2a = MathUtils.random(0, 360);
        var angle2b = MathUtils.random(0, 360);
        var angle3 = MathUtils.random(0, 360);
        var mag1 = MathUtils.random(30, 60);
        var mag2a = MathUtils.random(30, 60);
        var mag2b = MathUtils.random(30, 60);
        var mag3 = MathUtils.random(30, 60);
        nose.setVelocity(MathUtils.cosDeg(angle1) * mag1, MathUtils.sinDeg(angle1) * mag1);
        cabin1.setVelocity(MathUtils.cosDeg(angle2a) * mag2a, MathUtils.sinDeg(angle2a) * mag2a);
        cabin2.setVelocity(MathUtils.cosDeg(angle2b) * mag2b, MathUtils.sinDeg(angle2b) * mag2b);
        tail.setVelocity(MathUtils.cosDeg(angle3) * mag3, MathUtils.sinDeg(angle3) * mag3);
        screen.debris.addAll(nose, cabin1, cabin2, tail);
        screen.physicsObjects.addAll(nose, cabin1, cabin2, tail);

        // instantiate astronaut debris
        var numBodies = MathUtils.random(1, 4);
        for (int i = 0; i < numBodies; i++) {
            var body = new Debris(anim, pos.x, pos.y);
            var angle = MathUtils.random(0, 360);
            var magnitude = MathUtils.random(50, 80);
            var vx = MathUtils.cosDeg(angle * magnitude);
            var vy = MathUtils.sinDeg(angle * magnitude);
            body.setVelocity(vx, vy);
            screen.debris.add(body);
            screen.physicsObjects.add(body);
        }

        // remove this ship
        screen.playerShips.removeValue(this, true);
        screen.physicsObjects.removeValue(this, true);
    }

    @Override
    public void renderDebug(ShapeDrawer shapes) {
        shapes.filledCircle(collisionShape.center, collisionShape.radius, Color.YELLOW);
    }

    @Override
    public float getFriction() {
        if (fuel > 0) {
            return 1f;
        }
        return DRAG_FRICTION;
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
        setVelocity(newVel.x, newVel.y);
    }

    @Override
    public void setVelocity(float x, float y) {
        vel.set(x, y);
        // Todo: update rotation
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

        if (object instanceof GameBoundry){
            vel.set(0,0);
        }
    }

    @Override
    public boolean shouldCollideWith(Collidable object) {
        return true;
    }

}
