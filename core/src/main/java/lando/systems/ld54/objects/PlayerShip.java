package lando.systems.ld54.objects;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import lando.systems.ld54.Assets;
import lando.systems.ld54.screens.GameScreen;

public class PlayerShip {

    private Animation<TextureRegion> anim;
    private TextureRegion keyframe;
    private float animState;
    private float shipAngle;

    // TODO - should pos be center and we offset by half-size in draw()?
    //  or should pos be bottom left and offset by half-size in getCenter()?
    //  or should we use a Circle object and use circle .pos / .radius to find draw corner?

    public Vector2 pos;
    public Vector2 vel;
    public Vector2 size;

    public PlayerShip(Assets assets) {
        this.anim = assets.playerShip;
        this.animState = 0;
        this.pos = new Vector2();
        this.vel = new Vector2();
        this.size = new Vector2();

        // TEMP - manually set initial position and size for now
        this.pos.set(GameScreen.gameWidth / 2f, GameScreen.gameHeight / 2f);
        this.size.set(128, 128);
    }

    public void update(float dt) {
        animState += dt;
        keyframe = anim.getKeyFrame(animState);

        pos.x += dt * vel.x;
        pos.y += dt * vel.y;
    }

    public void draw(SpriteBatch batch) {
        batch.draw(keyframe,
            pos.x - size.x / 2f,
            pos.y - size.y / 2f,
            size.x / 2f, size.y / 2f, size.x, size.y, 1f, 1f, shipAngle);
    }

    public void launch(float angle, float power) {
        shipAngle = angle;
        vel.set(power, 0).rotateDeg(shipAngle + 90);
    }

}
