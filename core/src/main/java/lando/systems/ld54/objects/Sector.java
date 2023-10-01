package lando.systems.ld54.objects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import de.damios.guacamole.gdx.math.IntVector2;
import lando.systems.ld54.Config;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class Sector {

    private static final float WIDTH = Config.Screen.window_width;
    private static final float HEIGHT = Config.Screen.window_height;
    private static final float LINE_WIDTH = 4f;
    private static final Color COLOR = Color.TEAL;

    private final IntVector2 coords;
    private final Rectangle bounds;

    public Sector(int x, int y) {
        this.coords = new IntVector2(x, y);
        this.bounds = new Rectangle(x * WIDTH, y * HEIGHT, WIDTH, HEIGHT);
    }

    public void draw(SpriteBatch batch) {
        // TODO - draw encounter icon or other sprite-based stuff here
    }

    public void draw(ShapeDrawer shapes) {
        shapes.rectangle(bounds, Sector.COLOR, Sector.LINE_WIDTH);
    }

}
