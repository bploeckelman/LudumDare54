package lando.systems.ld54.objects;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import lando.systems.ld54.physics.Collidable;

public class Satellite extends Debris {
    public Satellite(Animation<TextureRegion> anim, float x, float y) {
        super(anim, x, y);

        setRadius(10);
        angularMomentum = MathUtils.randomSign() * MathUtils.random(30f, 50f);
    }

    @Override
    public boolean shouldCollideWith(Collidable object) {
        return (object instanceof PlayerShip);
    }

    @Override
    public boolean canBeInfluenced() {
        return false;
    }
}
