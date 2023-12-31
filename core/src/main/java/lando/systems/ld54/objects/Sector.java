package lando.systems.ld54.objects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import de.damios.guacamole.gdx.math.IntVector2;
import lando.systems.ld54.Config;
import lando.systems.ld54.Main;
import lando.systems.ld54.encounters.Encounter;
import lando.systems.ld54.physics.influencers.PullPlayerShipInfluencer;
import lando.systems.ld54.physics.influencers.PushJunkInfluencer;
import lando.systems.ld54.screens.GameScreen;
import lando.systems.ld54.utils.Time;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class Sector {

    public static final float WIDTH = Config.Screen.window_width;
    public static final float HEIGHT = Config.Screen.window_height;
    private static final float LINE_WIDTH = 4f;
    private static final Color COLOR = Color.TEAL.cpy().add(0, 0, 0, -0.75f);

    private final GameScreen screen;
    public final IntVector2 coords;
    public final Rectangle bounds;
    public Encounter encounter;
    public Rectangle encounterBounds;

    public boolean scanned = false;
    public boolean visited = false;
    public boolean isEncounterActive = true;

    public int distanceFromEarth;
    public boolean isGoal;

    public PullPlayerShipInfluencer pullPlayerShip;
    public PushJunkInfluencer pushJunk;

    private float animState = 0;
    private float encounterAnimState = 0;

    public Sector(GameScreen gameScreen, Encounter encounter, int x, int y) {
        this.screen = gameScreen;
        this.coords = new IntVector2(x, y);
        this.bounds = new Rectangle(x * WIDTH, y * HEIGHT, WIDTH, HEIGHT);
        this.encounter = encounter;
        this.encounter.sector = this;
        this.isGoal = false;

        int earthX = GameScreen.SECTORS_WIDE /2 ;
        int earthY = GameScreen.SECTORS_HIGH /2 ;

        distanceFromEarth = (int)Math.abs((earthX - x) * 1.6f) + Math.abs(earthY - y);

        // create 100x100 encounter bounds random in sector
        // TODO - these rands should be based on some fraction of a sector size
        this.encounterBounds = new Rectangle(
            MathUtils.random(bounds.x + 200, bounds.x + bounds.width - 300f),
            MathUtils.random(bounds.y + 100, bounds.y + bounds.height - 200f),
            100f, 100f
        );

        this.pullPlayerShip = new PullPlayerShipInfluencer(gameScreen,
            encounterBounds.x + encounterBounds.width / 2f,
            encounterBounds.y + encounterBounds.height / 2f
        );

        this.pushJunk = new PushJunkInfluencer(gameScreen,
            encounterBounds.x + encounterBounds.width / 2f,
            encounterBounds.y + encounterBounds.height / 2f
        );
    }

    public void update(float dt) {
        pullPlayerShip.updateInfluence(dt);
        pushJunk.updateInfluence(dt);
    }

    public void drawShape(ShapeDrawer shapes) {
        shapes.rectangle(bounds, Sector.COLOR, Sector.LINE_WIDTH);
    }

    public void drawAboveFogStuff(SpriteBatch batch) {
        if (scanned && encounter != null) {
            batch.draw(screen.assets.lockIcon,
                encounterBounds.x, encounterBounds.y,
                encounterBounds.width, encounterBounds.height);
        }
    }

    public void drawBelowFogStuff(SpriteBatch batch) {
        drawShape(screen.assets.shapes);

        batch.setColor(1, 1, 1, isEncounterActive ? 1 : 0.33f);

        // TODO - too many fiddly things to have setup correctly here
        //  for multiple influencers where the home and goal sectors might be handled specially
        //  need to simplify how influencers are setup in a sector (tied to an encounter or not)
        if (Config.Debug.general) {
            pushJunk.debugRender(batch);
        }
        pushJunk.renderInfluence(batch);

        if (encounter != null) {
            if (Config.Debug.general) {
                pullPlayerShip.debugRender(batch);
            }
            pullPlayerShip.renderInfluence(batch);

            encounterAnimState += Time.delta;
            var assets = Main.game.assets;
            var anim = assets.encounterAnimationHashMap.get(encounter.imageKey);
            var keyframe = anim.getKeyFrame(encounterAnimState);
            batch.draw(keyframe, encounterBounds.x, encounterBounds.y, encounterBounds.width, encounterBounds.height);
        }
        batch.setColor(Color.WHITE);
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

    public void scan() {
        scanned = true;
    }
}
