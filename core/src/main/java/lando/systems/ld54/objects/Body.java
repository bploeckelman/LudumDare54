package lando.systems.ld54.objects;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import lando.systems.ld54.screens.GameScreen;

public class Body extends Debris {

    public Body(GameScreen screen, float x, float y) {
        this(screen, screen.assets.astronautBodies.random(), x, y);
    }

    public Body(GameScreen screen, Animation<TextureRegion> anim, float x, float y) {
        super(screen, anim, x, y);

        var radius = 10f;
        var angle = MathUtils.random(0, 360);
        var magnitude = MathUtils.random(5, 40);
        var velX = magnitude * MathUtils.cosDeg(angle);
        var velY = magnitude * MathUtils.sinDeg(angle);
        var dist = MathUtils.random(15, 25);
        var posX = x + dist * MathUtils.cosDeg(angle);
        var posY = y + dist * MathUtils.sinDeg(angle);
        var spinDir = MathUtils.randomSign();
        var spin = spinDir * MathUtils.random(4, 15);

        setPosition(posX, posY);
        setVelocity(velX, velY);
        setRadius(radius);
        angularMomentum = spin;
    }

}
