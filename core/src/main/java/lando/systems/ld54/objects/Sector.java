package lando.systems.ld54.objects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import de.damios.guacamole.gdx.math.IntVector2;
import lando.systems.ld54.Config;
import lando.systems.ld54.Main;
import lando.systems.ld54.encounters.Encounter;
import lando.systems.ld54.utils.Time;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class Sector {

    private static final float WIDTH = Config.Screen.window_width;
    private static final float HEIGHT = Config.Screen.window_height;
    private static final float LINE_WIDTH = 4f;
    private static final Color COLOR = Color.TEAL.cpy().add(0, 0, 0, -0.75f);

    private final IntVector2 coords;
    public final Rectangle bounds;
    private boolean visited = false;
    public Encounter encounter = null;

    private float animState = 0;

    public Sector(int x, int y, Encounter encounter) {
        this.coords = new IntVector2(x, y);
        this.bounds = new Rectangle(x * WIDTH, y * HEIGHT, WIDTH, HEIGHT);
        this.encounter = encounter;
    }

    public void draw(SpriteBatch batch) {
        // TEMP
        animState += Time.delta;
        var keyframe = Main.game.assets.asuka.getKeyFrame(animState);
        batch.draw(keyframe, bounds.x, bounds.y, bounds.width, bounds.height);
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
