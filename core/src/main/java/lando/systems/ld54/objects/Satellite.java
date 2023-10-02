package lando.systems.ld54.objects;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import lando.systems.ld54.physics.Collidable;
import lando.systems.ld54.screens.GameScreen;
import org.ietf.jgss.GSSCredential;

public class Satellite extends Debris {
    private final Planet planet;
    private final Vector2 angleVector = new Vector2();
    private boolean free = false;

    public Satellite(GameScreen screen, Planet planet, Animation<TextureRegion> anim, float x, float y) {
        super(screen, anim, x, y);

        this.planet = planet;
        angleVector.set(x, y).sub(planet.centerPosition);

        setRadius(20);
        angularMomentum = MathUtils.randomSign() * MathUtils.random(30f, 50f);
    }

    @Override
    public void update(float dt) {
        super.update(dt);

        if (free) return;
        angleVector.rotateDeg(dt * 30);
        var center = planet.centerPosition;
        getPosition().set(center.x + angleVector.x, center.y + angleVector.y);
    }

    @Override
    public boolean shouldCollideWith(Collidable object) {
        // see collideWith
        return (object instanceof PlayerShip) && (object != lastHit || hitCount < 5);
    }

    private PlayerShip lastHit = null;
    private int hitCount = 0;

    @Override
    public void collidedWith(Collidable object) {
        if (object instanceof PlayerShip) {
            // if the reflection velocity doesn't go away from the ship, it can get multiple hits
            // prevent this from destroying the ship
            var ship = (PlayerShip)object;
            if (lastHit == ship) {
                hitCount++;
            } else {
                lastHit = ship;
                hitCount = 0;
            }
            free = true;
        }
    }

    @Override
    public boolean canBeInfluenced() {
        return false;
    }
}
