package lando.systems.ld54.objects;

import com.badlogic.gdx.math.MathUtils;
import lando.systems.ld54.screens.GameScreen;

public class RandomShit extends Debris {

    public RandomShit(GameScreen screen, float x, float y) {
        super(screen, screen.assets.randomDebris.random(), x, y);
        angularMomentum = MathUtils.randomSign() * MathUtils.random(30f, 50f);
        mass = 0.01f;

        setRadius(8);
    }

//    @Override
//    public boolean shouldCollideWith(Collidable object) {
//        // see collideWith
//        return (object instanceof PlayerShip) && (object != lastHit || hitCount < 5);
//    }
//
//    private PlayerShip lastHit = null;
//    private int hitCount = 0;
//
//    @Override
//    public void collidedWith(Collidable object) {
//        if (object instanceof PlayerShip) {
//            // if the reflection velocity doesn't go away from the ship, it can get multiple hits
//            // prevent this from destroying the ship
//            var ship = (PlayerShip)object;
//            if (lastHit == ship) {
//                hitCount++;
//            } else {
//                lastHit = ship;
//                hitCount = 0;
//            }
//        }
//    }

    @Override
    public boolean canBeInfluenced() {
        return false;
    }
}
