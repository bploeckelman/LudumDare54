package lando.systems.ld54.particles;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.*;
import lando.systems.ld54.Assets;
import lando.systems.ld54.objects.Body;
import lando.systems.ld54.objects.PlayerShip;

public class Particles implements Disposable {

    public enum Layer { background, middle, overFog }

    private static final int MAX_PARTICLES = 4000;

    private final Assets assets;
    private final ObjectMap<Layer, Array<Particle>> activeParticles;
    private final Pool<Particle> particlePool = Pools.get(Particle.class, MAX_PARTICLES);

    public Particles(Assets assets) {
        this.assets = assets;
        this.activeParticles = new ObjectMap<>();
        int particlesPerLayer = MAX_PARTICLES / Layer.values().length;
        this.activeParticles.put(Layer.background, new Array<>(false, particlesPerLayer));
        this.activeParticles.put(Layer.middle,     new Array<>(false, particlesPerLayer));
        this.activeParticles.put(Layer.overFog, new Array<>(false, particlesPerLayer));
    }

    public void clear() {
        for (Layer layer : Layer.values()) {
            particlePool.freeAll(activeParticles.get(layer));
            activeParticles.get(layer).clear();
        }
    }

    public void update(float dt) {
        for (Layer layer : Layer.values()) {
            for (int i = activeParticles.get(layer).size - 1; i >= 0; --i) {
                Particle particle = activeParticles.get(layer).get(i);
                particle.update(dt);
                if (particle.isDead()) {
                    activeParticles.get(layer).removeIndex(i);
                    particlePool.free(particle);
                }
            }
        }
    }


    public void draw(SpriteBatch batch, Layer layer) {
        activeParticles.get(layer).forEach(particle -> particle.render(batch));
    }

    @Override
    public void dispose() {
        clear();
    }

    // ------------------------------------------------------------------------
    // Helper fields for particle spawner methods
    // ------------------------------------------------------------------------
    private final Color tempColor = new Color();
    private final Vector2 tempVec2 = new Vector2();

    // ------------------------------------------------------------------------
    // Spawners for different particle effects
    // ------------------------------------------------------------------------


    public void addRocketPropulsionParticles(PlayerShip ship) {
        float positionFiddle = 1f;
        Vector2 vel = ship.vel;
        tempVec2.set(-20, 0).rotateDeg(ship.rotation);
        float centerX = ship.pos.x + tempVec2.x;
        float centerY = ship.pos.y + tempVec2.y;
        tempVec2.set(-20, 8).rotateDeg(ship.rotation);
        float leftX = ship.pos.x + tempVec2.x;
        float leftY = ship.pos.y + tempVec2.y;
        tempVec2.set(-20, -8).rotateDeg(ship.rotation);
        float rightX = ship.pos.x + tempVec2.x;
        float rightY = ship.pos.y + tempVec2.y;

        for (int i = 0; i < 10; i++){
            activeParticles.get(Layer.background).add(Particle.initializer(particlePool.obtain())
                .keyframe(assets.particles.circle)
                .startPos(centerX + MathUtils.random(-positionFiddle, positionFiddle), centerY + MathUtils.random(-positionFiddle, positionFiddle))
                .velocity(vel.x * MathUtils.random(-.25f, -.1f), vel.y * MathUtils.random(-.25f, -.1f))
                .startColor(1f, 0f, 0f,1f)
                .endColor(.5f,.5f,0,0)
                .startSize(2)
                .endSize(1f)
                .timeToLive(MathUtils.random(.1f, .3f))
                .init()
            );

            activeParticles.get(Layer.background).add(Particle.initializer(particlePool.obtain())
                .keyframe(assets.particles.circle)
                .startPos(leftX + MathUtils.random(-positionFiddle, positionFiddle), leftY + MathUtils.random(-positionFiddle, positionFiddle))
                .velocity(vel.x * MathUtils.random(-.25f, -.1f), vel.y * MathUtils.random(-.25f, -.1f))
                .startColor(1f, 0f, 0f,1f)
                .endColor(1f,1f,0,0)
                .startSize(2)
                .endSize(1f)
                .timeToLive(MathUtils.random(.1f, .3f))
                .init()
            );
            activeParticles.get(Layer.background).add(Particle.initializer(particlePool.obtain())
                .keyframe(assets.particles.circle)
                .startPos(rightX + MathUtils.random(-positionFiddle, positionFiddle), rightY + MathUtils.random(-positionFiddle, positionFiddle))
                .velocity(vel.x * MathUtils.random(-.25f, -.1f), vel.y * MathUtils.random(-.25f, -.1f))
                .startColor(1f, 0f, 0f,1f)
                .endColor(1f,1f,0,0)
                .startSize(2)
                .endSize(1f)
                .timeToLive(MathUtils.random(.1f, .3f))
                .init()
            );

            // Smokes
//            activeParticles.get(Layer.background).add(Particle.initializer(particlePool.obtain())
//                .keyframe(assets.particles.circle)
//                .startPos(centerX + MathUtils.random(-positionFiddle, positionFiddle), centerY + MathUtils.random(-positionFiddle, positionFiddle))
//                .velocity(vel.x * MathUtils.random(-.25f, -.1f), vel.y * MathUtils.random(-.25f, -.1f))
//                .startColor(.3f, .3f, .3f,.5f)
//                .endColor(.3f,.3f,.3f,.5f)
//                .startSize(1f)
//                .endSize(1f)
//                .timeToLive(MathUtils.random(.3f, .6f))
//                .init()
//            );
        }

    }

    public Array<Particle> addShipTrail(float inX, float inY) {
        Array<Particle> trailParticles = new Array<>();
        for (int i = 0; i < 4; i++) {
            float x = inX + MathUtils.random(-3, 3f);
            float y = inY + MathUtils.random(-3f, 3f);
            Particle p = Particle.initializer(particlePool.obtain())
                .keyframe(assets.particles.smoke)
                .startPos(x, y)
                .startColor(.5f, .5f, .7f, .5f)
                .endColor(0,0,0,0)
                .timeToLive(1000f)
                .startSize(MathUtils.random(10, 15f))
                .endSize(10)
                .init();
            trailParticles.add(p);
            activeParticles.get(Layer.background).add(p);
        }
        return trailParticles;
    }

    public void debrisExplode(float inX, float inY) {
        for (int i = 0; i < 20; i++) {
            float angle = MathUtils.random(0f, 360f);
            float speed = MathUtils.random(0f, 40f);
            float x = inX + MathUtils.random(-20f, 20f);
            float y = inY + MathUtils.random(-20f, 20f);
            float size = MathUtils.random(10f, 30f);
            activeParticles.get(Layer.middle).add(Particle.initializer(particlePool.obtain())
                .keyframe(assets.particles.smoke)
                .startPos(x, y)
                .velocity(MathUtils.cosDeg(angle) * speed, MathUtils.sinDeg(angle) * speed)
                .startColor(1f, 1f, 1f,1f)
                .endColor(0,0,0,0)
                .startSize(size)
                .endSize(5f)
                .timeToLive(MathUtils.random(2f, 4f))
                .init()
            );
        }
    }

    public void shipExplode(float inX, float inY) {
        for (int i = 0; i < 30; i++) {
            float angle = MathUtils.random(0f, 360f);
            float speed = MathUtils.random(0f, 100f);
            float x = inX + MathUtils.random(-100f, 100f);
            float y = inY + MathUtils.random(-100f, 100f);
            float size = MathUtils.random(60f, 200f);
            float color = MathUtils.random(.3f, 1f);
            activeParticles.get(Layer.middle).add(Particle.initializer(particlePool.obtain())
                .keyframe(assets.particles.smoke)
                .startPos(x, y)
                .velocity(MathUtils.cosDeg(angle) * speed, MathUtils.sinDeg(angle) * speed)
                    .startColor(color, color, color,1f)
                    .endColor(0,0,0,0)
                    .startSize(size)
                    .endSize(5f)
                    .timeToLive(MathUtils.random(2f, 4f))
                .init()
            );
        }
    }

    public void bodySplatter(Body body) {
        var color = Color.RED.cpy();
        color.a = 0.25f;
        for (int i = 0; i < 10; i++) {
            float x = body.getPosition().x + MathUtils.random(-15f, 15f);
            float y = body.getPosition().y + MathUtils.random(-15f, 15f);
            float size = MathUtils.random(10f, 30f);
            activeParticles.get(Layer.background).add(Particle.initializer(particlePool.obtain())
                .keyframe(assets.bloodSplatters.random())
                .startPos(x, y)
                .velocity(body.getVelocity().x, body.getVelocity().y)
                .startColor(color)
                .startSize(size)
                .persist()
                .init()
            );
        }
    }

}
