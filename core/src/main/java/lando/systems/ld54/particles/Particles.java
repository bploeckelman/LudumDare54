package lando.systems.ld54.particles;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.*;
import lando.systems.ld54.Assets;
import lando.systems.ld54.utils.Utils;

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




    public void shipExplode(float inX, float inY) {
        for (int i = 0; i < 20; i++) {
            float angle = MathUtils.random(0f, 360f);
            float speed = MathUtils.random(0f, 100f);
            float x = inX + MathUtils.random(-100f, 100f);
            float y = inY + MathUtils.random(-100f, 100f);
            float size = MathUtils.random(60f, 200f);
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

}
