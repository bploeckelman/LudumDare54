package lando.systems.ld54.objects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import lando.systems.ld54.Main;
import lando.systems.ld54.Stats;
import lando.systems.ld54.audio.AudioManager;
import lando.systems.ld54.particles.Particle;
import lando.systems.ld54.physics.Collidable;
import lando.systems.ld54.physics.CollisionShape;
import lando.systems.ld54.physics.CollisionShapeCircle;
import lando.systems.ld54.screens.GameScreen;
import lando.systems.ld54.utils.Time;
import space.earlygrey.shapedrawer.ShapeDrawer;

import static lando.systems.ld54.objects.PlayerShipPart.Type.*;

public class PlayerShip implements Collidable {

    public static final float MAX_SPEED = 400f;
    public static final float MIN_SPEED_THRUSTING = 75f;

    private static final float DRAG_FRICTION = 0.4f;
    public static final float FUEL_PER_BAR_LEVEL = 450f;
    public static final float MAX_HEALTH = 100f;

    private float mass = 100;
    public float health = MAX_HEALTH;

    private Animation<TextureRegion> anim;
    private TextureRegion keyframe;
    private float animState;

    public boolean isShielded = false;
    public boolean trackMovement = false;
    public int currentSector = -1;
    public float ROTATION_LERP = 80f;

    private final Rectangle collisionBounds;
    private final CollisionShapeCircle collisionShape;

    // TODO - should pos be center and we offset by half-size in draw()?
    //  or should pos be bottom left and offset by half-size in getCenter()?
    //  or should we use a Circle object and use circle .pos / .radius to find draw corner?

    public Vector2 pos;
    public Vector2 vel;
    public Vector2 size;
    public float fuel;
    public float currentMaxFuel;
    public float rotation; // relative to orientation in texture, if facing right, no adjustment needed for angle values
    public float targetRotation;
    private boolean inactive = false;

    private final GameScreen screen;
    private long engineSoundID;
    public boolean isReset = false;

    public Array<Particle> trailParticles;

    public PlayerShip(GameScreen gameScreen) {
        this.screen = gameScreen;
        this.anim = screen.assets.playerShipActive;
        this.animState = 0;
        this.pos = new Vector2();
        this.vel = new Vector2();
        this.size = new Vector2();
        this.rotation = 0;
        this.trailParticles = new Array<>();

        var homeBounds = screen.homeSector.bounds;
        this.pos.set(
            homeBounds.x + homeBounds.width / 2f,
            homeBounds.y + homeBounds.height / 2f
        );

        // TEMP - manually set initial size for now
        this.size.set(128, 128);

        this.collisionBounds = new Rectangle(pos.x - size.x/3f, pos.y - size.y /3f, size.x * 2f/3f, size.y * .66f);
        this.collisionShape = new CollisionShapeCircle(size.x /3f, pos.x, pos.y);
        this.fuel = screen.player.fuelLevel * FUEL_PER_BAR_LEVEL;
        this.currentMaxFuel = fuel;
        targetRotation = vel.angleDeg();
        rotation = targetRotation;
    }

    public void update(float dt) {
        // update animation
        animState += dt;
        Main.game.assets.engineRunning.setVolume(engineSoundID, fuel / currentMaxFuel * Main.game.audioManager.soundVolume.floatValue());
//        Main.game.assets.engineRunning.setVolume(engineSoundID, fuel / 1000 * Main.game.audioManager.soundVolume.floatValue());

        if (fuel <= 0 && !inactive) { // don't show idle when inactive
            Main.game.assets.engineRunning.stop();
            anim = screen.assets.playerShip; // revert to idle animation
            isShielded = false;
        }

        if (health <= 0) {
            explode();
            vel.setZero();
            trackMovement = false;
            screen.isShipMoving = false;
            screen.isLaunchPhase = inactive = true;
            Time.do_after_delay(2f, (params) -> resetCameraToHomeSector());
            return;
        }

        float rotationChange = targetRotation - rotation;
        if (Math.abs(rotationChange) < ROTATION_LERP * dt){
            rotation = targetRotation;
        } else {
            while (rotationChange < -180 || rotationChange > 180){
                if (rotationChange > 180) rotationChange -= 360;
                if (rotationChange < -180) rotationChange += 360;
            }
            rotation += Math.signum(rotationChange) * ROTATION_LERP * dt;
        }

        fuel = Math.max(0, fuel - vel.len() * dt);

        float curVelocity = vel.len2();
        if (curVelocity < 10f) {
            trackMovement = false;
            screen.isShipMoving = false;
            screen.isLaunchPhase = inactive = true;
            becomeDerelict();
            Time.do_after_delay(2f, (params) -> resetCameraToHomeSector());
        } else {
            // get rotation based on velocity
            targetRotation = vel.angleDeg();
            float fogClearRadius = 200f;
            if (fuel > 0){
                fogClearRadius += MathUtils.clamp(10000f / vel.len(), 0, 200f);
            }
            screen.fogOfWar.addFogCircle(pos.x, pos.y, fogClearRadius, vel.len()/ 100f);
        }
        if (fuel > 0){
            screen.particles.addRocketPropulsionParticles(this);
            screen.particles.addShipSpeedParticles(this);
        }

        trailParticles.addAll(screen.particles.addShipTrail(pos.x, pos.y, vel.len() / 200f));
    }

    public void draw(SpriteBatch batch) {
        keyframe = anim.getKeyFrame(animState);
        batch.draw(keyframe,
            pos.x - size.x / 2f,
            pos.y - size.y / 2f,
            size.x / 2f,
            size.y / 2f,
            size.x, size.y,
            1, 1,
            rotation
        );

        if (isShielded) {
            batch.setColor(1, 1, 1, 0.9f);
            var margin = 5;
            var scale = 4;
            var shield = screen.assets.shield.getKeyFrame(animState);
            batch.draw(shield,
                pos.x - size.x / 2f - margin * scale,
                pos.y - size.y / 2f - margin,
                size.x / 2f + margin * scale,
                size.y / 2f + margin,
                size.x + margin * 2 * scale,
                size.y + margin * 2,
                1, 1,
                rotation
            );
            batch.setColor(1, 1, 1, 1);
        }
    }

    public void launch(float angle, float power) {
        trackMovement = true;
        vel.set(1, 0).setAngleDeg(angle);
        pos.add(vel.x * 60, vel.y * 60);
        vel.scl(power);
//         = screen.audioManager.playSound(AudioManager.Sounds.engineLaunch, 1.4f);
        engineSoundID = screen.audioManager.loopSound(AudioManager.Sounds.engineRunning, .4f);
        screen.audioManager.playSound(AudioManager.Sounds.engineLaunch);
        Main.game.assets.engineRunning.setVolume(engineSoundID,  Main.game.audioManager.soundVolume.floatValue());
        targetRotation = vel.angleDeg();
        rotation = targetRotation;
    }

    private static final PlayerShipPart.Type[] shipPartTypes = new PlayerShipPart.Type[] { nose, cabin, cabin, tail };

    public void explode() {
        // TODO - particle effect
        //screen.particles.shipExplode(pos.x, pos.y);
        screen.particles.shipExplode(this);

        screen.audioManager.playSound(AudioManager.Sounds.explosion);
        screen.assets.engineRunning.stop();

        Stats.numShipsExploded++;
        // TODO - maybe do something cutesy where we line them up all nice then blast them apart,
        //   but for now just get them onscreen
        // instantiate ship parts making sure to separate them a bit on init to reduce jitter when they get pushed out of overlap
        for (int i = 0; i < shipPartTypes.length; i++) {
            var type = shipPartTypes[i];
            var angle = MathUtils.random(0, 360);
            var magnitude = MathUtils.random(10, 50);
            var velX = magnitude * MathUtils.cosDeg(angle);
            var velY = magnitude * MathUtils.sinDeg(angle);
            var dist = MathUtils.random(10, 40);
            var posX = pos.x + dist * MathUtils.cosDeg(angle);
            var posY = pos.y + dist * MathUtils.sinDeg(angle);
            var spinDir = MathUtils.randomSign();
            var spin = spinDir * MathUtils.random(2, 15);
            var part = new PlayerShipPart(screen, type, screen.assets, posX, posY);
            part.setVelocity(velX, velY);
            part.angularMomentum = spin;
            screen.debris.add(part);
            screen.physicsObjects.add(part);
        }

        // instantiate astronaut debris
        var numBodies = MathUtils.random(1, 4);
        Stats.numAstronautsEjected += numBodies;
        for (int i = 0; i < numBodies; i++) {
            var body = new Body(screen, pos.x, pos.y);
            screen.debris.add(body);
            screen.physicsObjects.add(body);
        }

        // remove this ship from drawing and physics since it is now in pieces
        screen.playerShips.removeValue(this, true);
        screen.physicsObjects.removeValue(this, true);
        clearTrail();
        screen.isLaunchPhase = true;
    }

    private void becomeDerelict() {
        Stats.numShipsDerelict++;

        screen.audioManager.playSound(AudioManager.Sounds.explosion);
        var type = derelict;
        var angle = MathUtils.random(0, 360);
        var magnitude = MathUtils.random(5, 20);
        var velX = magnitude * MathUtils.cosDeg(angle);
        var velY = magnitude * MathUtils.sinDeg(angle);
        var spinDir = MathUtils.randomSign();
        var spin = spinDir * MathUtils.random(10, 25);
        var part = new PlayerShipPart(screen, type, screen.assets, pos.x, pos.y);
        part.setVelocity(velX, velY);
        part.angularMomentum = spin;
        part.setRadius(64);
        screen.debris.add(part);
        screen.physicsObjects.add(part);

        // instantiate astronaut debris
        var numBodies = MathUtils.random(1, 3);
        Stats.numAstronautsEjected += numBodies;
        for (int i = 0; i < numBodies; i++) {
            var body = new Body(screen, pos.x, pos.y);
            screen.debris.add(body);
            screen.physicsObjects.add(body);
        }

        screen.playerShips.removeValue(this, true);
        screen.physicsObjects.removeValue(this, true);

        screen.particles.shipExplode(pos.x, pos.y);
        clearTrail();
    }

    private void resetCameraToHomeSector() {
        isReset = true;
        var homeBounds = screen.homeSector.bounds;
        screen.cameraController.targetPos.set(
            homeBounds.x + homeBounds.width / 2f,
            homeBounds.y + homeBounds.height / 2f,
            0);
    }

    private void clearTrail() {
        for (Particle p : trailParticles) {
            p.ttl = 5f;
            p.ttlMax = 5f;
        }
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
        if (vel.len2() > MAX_SPEED * MAX_SPEED) {
            vel.nor().scl(MAX_SPEED);
        }
        if (fuel > 0 && vel.len2() < MIN_SPEED_THRUSTING * MIN_SPEED_THRUSTING) {
            vel.nor().scl(MIN_SPEED_THRUSTING);
        }
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

        screen.audioManager.playSound(AudioManager.Sounds.thud, .5f);


        // assumes max velocity is 300
        if (!isShielded) {
            float damageMultiplier = .1f;
            if (object instanceof Asteroid){
                damageMultiplier = .9f;
            }
            float speedModifier = vel.len() / 125 * damageMultiplier; // 4x damage for full speed
            health -= object.getMass() * speedModifier;
            System.out.println(health);
        }

        if (object instanceof GameBoundry) {
            // TODO - maybe reflect instead so parts
            vel.set(0,0);
        }

        if (object instanceof Body) {
            screen.particles.bodySplatter((Body) object);
                    screen.audioManager.playSound(AudioManager.Sounds.squish, .35f);
        }
    }

    @Override
    public boolean shouldCollideWith(Collidable object) {
        return true;
    }

}
