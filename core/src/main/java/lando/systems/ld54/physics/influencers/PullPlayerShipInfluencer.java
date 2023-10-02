package lando.systems.ld54.physics.influencers;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import lando.systems.ld54.objects.PlayerShip;
import lando.systems.ld54.physics.Collidable;
import lando.systems.ld54.physics.Influencer;
import lando.systems.ld54.screens.GameScreen;

public class PullPlayerShipInfluencer implements Influencer {

    private static final Color COLOR = Color.TEAL.cpy();

    private final GameScreen screen;
    private final Vector2 position;
    private final InterpolatingFloat strength;
    private final InterpolatingFloat range;
    private final InfluenceRenderer influenceRenderer;

    private boolean active;

    public PullPlayerShipInfluencer(GameScreen screen, float x, float y) {
        COLOR.a = 0.8f;

        this.screen = screen;
        this.position = new Vector2(x, y);
        this.strength = new InterpolatingFloat(700);
        this.range = new InterpolatingFloat(150f);
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
        return (active && (c instanceof PlayerShip));
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
