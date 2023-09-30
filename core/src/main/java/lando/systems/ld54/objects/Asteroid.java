package lando.systems.ld54.objects;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.equations.Linear;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import lando.systems.ld54.Main;
import lando.systems.ld54.utils.accessors.Vector2Accessor;

public class Asteroid {

    public TextureRegion region;
    public Vector2 initialPos;
    public Vector2 initialSize;

    public Vector2 pos;
    public Vector2 size;
    public Vector2 range;

    private Tween floatTween;
    private boolean updateFloat;

    // TODO - support rotation, tweening, etc...
    private static final float default_range = 100;
    private static final float min_duration = 1;
    private static final float max_duration = 5;

    public Asteroid(TextureRegion region, float x, float y) {
        this.region = region;
        this.initialPos = new Vector2(x, y);
        this.initialSize = new Vector2(region.getRegionWidth(), region.getRegionHeight());
        this.pos = initialPos.cpy();
        this.size = initialSize.cpy();
        this.range = new Vector2(default_range, default_range);
        this.updateFloat = false;
        updateFloatTween();
    }

    public Asteroid(TextureRegion region, float x, float y, float w, float h) {
        this.region = region;
        this.initialPos = new Vector2(x, y);
        this.initialSize = new Vector2(w, h);
        this.pos = initialPos.cpy();
        this.size = initialSize.cpy();
        this.range = new Vector2(default_range, default_range);
        this.updateFloat = false;
        updateFloatTween();
    }

    public void draw(SpriteBatch batch) {
        batch.draw(region, pos.x, pos.y, size.x, size.y);
    }

    public void update() {
        if (updateFloat) {
            updateFloat = false;
            updateFloatTween();
        }
    }

    private void updateFloatTween() {
        // TODO - might be worth getting fancy and only picking new targets
        //  that are within some angle of the prev target so the new tween
        //  doesn't feel jerky
        float xTarget = initialPos.x + MathUtils.random(-range.x, range.x);
        float yTarget = initialPos.y + MathUtils.random(-range.y, range.y);
        float duration = MathUtils.random(min_duration, max_duration);
        floatTween = Tween.to(pos, Vector2Accessor.XY, duration)
            .target(xTarget, yTarget)
            .ease(Linear.INOUT)
            .setCallback((type, object) -> updateFloat = true)
            .start(Main.game.tween);
    }

}
