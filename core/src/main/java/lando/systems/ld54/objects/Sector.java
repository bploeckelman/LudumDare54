package lando.systems.ld54.objects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import de.damios.guacamole.gdx.math.IntVector2;
import lando.systems.ld54.Config;
import lando.systems.ld54.Main;
import lando.systems.ld54.encounters.Encounter;
import lando.systems.ld54.utils.Time;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class Sector {

    public static final float WIDTH = Config.Screen.window_width;
    public static final float HEIGHT = Config.Screen.window_height;
    private static final float LINE_WIDTH = 4f;
    private static final Color COLOR = Color.TEAL.cpy().add(0, 0, 0, -0.75f);

    private final IntVector2 coords;
    public final Rectangle bounds;
    private boolean visited = false;
    public Encounter encounter;
    public Rectangle encounterBounds;
    public boolean isEncounterActive = true;

    private float animState = 0;
    private float encounterAnimState = 0;

    public Sector(int x, int y, Encounter encounter) {
        this.coords = new IntVector2(x, y);
        this.bounds = new Rectangle(x * WIDTH, y * HEIGHT, WIDTH, HEIGHT);
        this.encounter = encounter;
        // create 100x100 encounter bounds random in sector
        this.encounterBounds = new Rectangle(MathUtils.random(bounds.x + 200, bounds.x + bounds.width - 300f),
            MathUtils.random(bounds.y + 100, bounds.y + bounds.height - 200f), 100f, 100f);
    }

    public void draw(SpriteBatch batch) {
        // TEMP
        animState += Time.delta;
        var keyframe = Main.game.assets.asuka.getKeyFrame(animState);
        batch.draw(keyframe, bounds.x, bounds.y, bounds.width, bounds.height);
    }

    public void drawEncounter(SpriteBatch batch) {
        if (isEncounterActive) {
            batch.setColor(Color.WHITE);
        }
        else {
            batch.setColor(1f, 1f, 1f, 0.5f);
        }
        if (encounter != null) {
            encounterAnimState += Time.delta;
            batch.draw(Main.game.assets.encounterAnimationHashMap.get(encounter.imageKey).getKeyFrame(encounterAnimState), encounterBounds.x, encounterBounds.y, encounterBounds.width, encounterBounds.height);
        }
        batch.setColor(Color.WHITE);
    }

    public void draw(ShapeDrawer shapes) {
        shapes.rectangle(bounds, Sector.COLOR, Sector.LINE_WIDTH);
    }

    public boolean isVisited() {
        return visited;
    }

    public boolean setVisited(boolean visited) {
        return this.visited = visited;
    }

    public void resetVisited() {
        this.visited = false;
    }

}
