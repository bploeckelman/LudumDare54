package lando.systems.ld54.physics.influencers;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import lando.systems.ld54.objects.Asteroid;
import lando.systems.ld54.objects.Debris;
import lando.systems.ld54.physics.Collidable;
import lando.systems.ld54.physics.Influencer;
import lando.systems.ld54.screens.GameScreen;

public class PushJunkInfluencer implements Influencer {

    private static final Color COLOR = Color.GOLDENROD.cpy();

    private final GameScreen screen;
    private final InterpolatingFloat strength;
    private final InterpolatingFloat range;
    private final InfluenceRenderer influenceRenderer;

    public final Vector2 position;
    private boolean active;

    public PushJunkInfluencer(GameScreen screen, float x, float y) {
        COLOR.a = 0.2f;

        this.screen = screen;
        this.position = new Vector2(x, y);
        this.strength = new InterpolatingFloat(-200);
        this.range = new InterpolatingFloat(200f);
        this.range.setInterpolation(Interpolation.elasticOut);
        this.influenceRenderer = new InfluenceRenderer(this, COLOR);
        this.active = true;

        screen.influencers.add(this);
    }

    public void activate() {
        active = true;
        if (!screen.influencers.contains(this, true)) {
            screen.influencers.add(this);
        }
    }

    public void deactivate() {
        active = false;
        if (screen.influencers.contains(this, true)) {
            screen.influencers.removeValue(this, true);
        }
    }

    @Override
    public float getStrength() {
        return strength.getValue();
    }

    @Override
    public Vector2 getPosition() {
        return position;
    }

    @Override
    public float getRange() {
        return range.getValue();
    }

    @Override
    public void debugRender(SpriteBatch batch) {
        // TODO - update interface so this takes a ShapeDrawer and draw a circle or whatever
        var shapes = screen.assets.shapes;
        shapes.setColor(COLOR);
        shapes.circle(position.x, position.y, getRange(), 1f);
        shapes.setColor(Color.WHITE);
    }

    @Override
    public boolean shouldEffect(Collidable c) {
        if (!active) return false;
        return c instanceof JunkInfluencible && ((JunkInfluencible) c).canBeInfluenced();
    }

    @Override
    public void updateInfluence(float dt) {
        if (active) {
            range.update(dt);
            strength.update(dt);
            influenceRenderer.update(dt);
        }
    }

    @Override
    public void renderInfluence(SpriteBatch batch) {
        if (active) {
            influenceRenderer.render(batch);
        }
    }
}
