package lando.systems.ld54.objects;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import lando.systems.ld54.Assets;
import lando.systems.ld54.fogofwar.FogOfWar;
import lando.systems.ld54.screens.GameScreen;

public class PlayerShip {

    private static final float DRAG_FRICTION = 0.995f;

    private Animation<TextureRegion> anim;
    private TextureRegion keyframe;
    private float animState;

    public boolean trackMovement = false;

    // TODO - should pos be center and we offset by half-size in draw()?
    //  or should pos be bottom left and offset by half-size in getCenter()?
    //  or should we use a Circle object and use circle .pos / .radius to find draw corner?

    public Vector2 pos;
    public Vector2 vel;
    public Vector2 size;
    public float rotation; // relative to orientation in texture, if facing right, no adjustment needed for angle values
    private FogOfWar fogOfWar;

    public PlayerShip(Assets assets, FogOfWar fogOfWar) {
        this.anim = assets.playerShip;
        this.fogOfWar = fogOfWar;
        this.animState = 0;
        this.pos = new Vector2();
        this.vel = new Vector2();
        this.size = new Vector2();
        this.rotation = 0;

        // TEMP - manually set initial position and size for now
        this.pos.set(GameScreen.gameWidth / 2f, GameScreen.gameHeight / 2f);
        this.size.set(128, 128);
    }

    public void update(float dt) {
        // update animation
        animState += dt;
        keyframe = anim.getKeyFrame(animState);

        if (vel.isZero()) { return; }

        // integrate velocity into position
        pos.x += dt * vel.x;
        pos.y += dt * vel.y;

        // slow down over time
        vel.scl(DRAG_FRICTION);
        float curVelocity = vel.len2();
        if (curVelocity < 0.1f) {
            vel.setZero();
        } else if (curVelocity < 200f) {
            trackMovement = false;
        } else {
            // get rotation based on velocity
            rotation = vel.angleDeg();
            fogOfWar.addFogCircle(pos.x, pos.y, 200);
        }
    }

    public void setVel(float angle, float power) {
        vel.set(1, 0).setAngleDeg(angle).scl(power);
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

}
