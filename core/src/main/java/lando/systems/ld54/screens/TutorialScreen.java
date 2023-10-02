package lando.systems.ld54.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Align;
import lando.systems.ld54.Assets;
import lando.systems.ld54.Config;

public class TutorialScreen extends BaseScreen {

    private final int numPages;
    private final Rectangle bounds;
    private final Color textColor;
    private final Color titleColor;

    private int page;
    private float alpha;
    private float targetAlpha;
    private float headingBottomY;

    private final BitmapFont fontSm;
    private final BitmapFont fontMd;
    private final BitmapFont fontLg;
    private final GlyphLayout layout;

    static class Art {
        TextureRegion minimapUnexplored;
        TextureRegion minimapExplored;
        TextureRegion waypointExample;
        TextureRegion waypointGoal;
        TextureRegion earth;
        TextureRegion launch;
        TextureRegion fuel;
    }
    private final Art art;

    public TutorialScreen() {
        this.bounds = new Rectangle(60, 60, Config.Screen.window_width - 120, Config.Screen.window_height - 120);
        this.textColor = new Color(Color.WHITE);
        this.titleColor = new Color(.8f, .8f, 1f, 1f);

        this.numPages = 2;
        this.page = 0;
        this.alpha = 0;
        this.targetAlpha = 1;

        this.fontSm = assets.abandonedFont20;
        this.fontMd = assets.abandonedFont50;
        this.fontLg = assets.abandonedFont100;
        this.layout = assets.layout;

        this.art = new Art();
        art.minimapExplored = assets.atlas.findRegion("tutorial/minimap-explored");
        art.minimapUnexplored = assets.atlas.findRegion("tutorial/minimap-unexplored");
        art.waypointExample = assets.atlas.findRegion("tutorial/waypoint-example");
        art.waypointGoal = assets.atlas.findRegion("tutorial/waypoint-goal");
        art.earth = assets.atlas.findRegion("tutorial/earth");
        art.launch = assets.atlas.findRegion("tutorial/launch");
        art.fuel = assets.atlas.findRegion("tutorial/fuel");
    }

    @Override
    public void alwaysUpdate(float delta) {}

    @Override
    public void update(float delta) {
        super.update(delta);

        if (alpha < targetAlpha) alpha = Math.min(targetAlpha, alpha + delta * 4f);
        if (alpha > targetAlpha) alpha = Math.max(targetAlpha, alpha - delta * 8f);
        textColor.set(.8f, .8f, 1f, alpha);

        if (alpha == targetAlpha) {
            if (targetAlpha == 0) {
                page++;
                targetAlpha = 1f;
                if (page >= numPages && !exitingScreen) {
                    exitingScreen = true;
                    game.setScreen(new GameScreen(true), assets.cubeShader, 2f);
                }
            } else {
                if (Gdx.input.justTouched() || Gdx.input.isKeyJustPressed(Input.Keys.ANY_KEY)) {
                    targetAlpha = 0;
                }
            }
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        int GL_COVERAGE_BUFFER_BIT_NV = (Gdx.graphics.getBufferFormat().coverageSampling) ? GL20.GL_COVERAGE_BUFFER_BIT_NV : 0;
        Gdx.gl.glClearColor(0.15f, 0.15f, 0.2f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT | GL_COVERAGE_BUFFER_BIT_NV);

        batch.setProjectionMatrix(worldCamera.combined);
        batch.begin();

        var patch = Assets.NinePatches.glass;
        patch.draw(batch, bounds.x, bounds.y, bounds.width, bounds.height);

        layout.setText(fontMd, "How to play!", titleColor, bounds.width, Align.center, true);
        headingBottomY = bounds.y + bounds.height - 20 - layout.height;
        fontMd.draw(batch, layout, bounds.x, bounds.y + bounds.height - 20);

        batch.setColor(1, 1, 1, alpha);
        switch (page) {
            case 0: drawPage1(batch); break;
            case 1: drawPage2(batch); break;
        }
        batch.setColor(Color.WHITE);

        String continueString = "Click or press a key to continue";
        if (page == 2) continueString = "Let's start!";
        layout.setText(fontSm, continueString, textColor, bounds.width, Align.center, true);
        fontSm.draw(batch, layout, bounds.x, bounds.y + layout.height + 15);
        batch.end();
    }

    @Override
    public void renderFrameBuffers(SpriteBatch batch) {}

    private void drawPage1(SpriteBatch batch) {
        // map, encounters, goal(?)
        float x, y;
        float margin = 10f;
        float borderOffset = 5f;
        float mapLineY;
        float lineThickness, lineRadius;
        TextureRegion tex;

        fontMd.getData().setScale(0.7f);

        // sector map heading -----------------
        mapLineY = headingBottomY - 2 * margin;
        layout.setText(fontMd, "Sector Map", Color.WHITE, bounds.width, Align.center, true);
        fontMd.draw(batch, layout, bounds.x, mapLineY);
        mapLineY -= layout.height;

        // header border-bottom
        lineThickness = 3f;
        lineRadius = 110f;
        assets.shapes.line(
            bounds.x + bounds.width / 2f - lineRadius, mapLineY - lineThickness - borderOffset,
            bounds.x + bounds.width / 2f + lineRadius, mapLineY - lineThickness - borderOffset,
            Color.GRAY, lineThickness);
        // end sector map heading -------------

        // minimap section --------------------
        tex = art.minimapUnexplored;
        mapLineY -= tex.getRegionHeight() + 2 * margin;

        y = mapLineY;
        x = bounds.x + (bounds.width - tex.getRegionWidth()) / 2f - (tex.getRegionWidth() / 2f + 5 * margin);
        batch.draw(tex, x, y);

        // draw text between minimap images
        layout.setText(fontSm, "  >>>  \nexplore\n  >>> ", Color.LIME, bounds.width, Align.center, true);
        fontSm.draw(batch, layout, bounds.x, mapLineY + (tex.getRegionHeight() + layout.height) / 2f);

        tex = art.minimapExplored;
        x = bounds.x + (bounds.width - tex.getRegionWidth()) / 2f + (tex.getRegionWidth() / 2f + 5 * margin);
        batch.draw(tex, x, y);
        // end minimap section ----------------


        // move to next section...
        mapLineY -= margin;


        // waypoints heading ------------------
        layout.setText(fontMd, "Waypoints", Color.WHITE, bounds.width, Align.center, true);
        fontMd.draw(batch, layout, bounds.x, mapLineY);
        mapLineY -= layout.height;

        // header border-bottom
        lineThickness = 3f;
        lineRadius = 100f;
        assets.shapes.line(
            bounds.x + bounds.width / 2f - lineRadius, mapLineY - lineThickness - borderOffset,
            bounds.x + bounds.width / 2f + lineRadius, mapLineY - lineThickness - borderOffset,
            Color.GRAY, lineThickness);
        // end waypoints heading --------------

        // waypoints section ------------------
        tex = art.waypointExample;
        mapLineY -= tex.getRegionHeight() + 2 * margin;

        y = mapLineY;
        x = bounds.x + (bounds.width - tex.getRegionWidth()) / 2f - (tex.getRegionWidth() / 2f + 10 * margin);
        batch.draw(tex, x, y);

        // draw waypoint explanation below waypoint image
        layout.setText(fontSm, "encounter waypoints\nmake choices", Color.CYAN, bounds.width / 3f, Align.center, true);
        fontSm.draw(batch, layout, bounds.x + 215, mapLineY - margin);

        // draw general waypoint explanation between waypoint images
        layout.setText(fontSm, "one waypoint\nin each sector\n\ninteract to\nreveal sector", Color.TAN, bounds.width, Align.center, true);
        fontSm.draw(batch, layout, bounds.x, mapLineY + (tex.getRegionHeight() + layout.height) / 2f);

        tex = art.waypointGoal;
        x = bounds.x + (bounds.width - tex.getRegionWidth()) / 2f + (tex.getRegionWidth() / 2f + 10 * margin);
        batch.draw(tex, x, y);

        // draw goal explanation below goal waypoint image
        layout.setText(fontSm, "find space station\nescape to free space!", Color.YELLOW, bounds.width / 3f, Align.center, true);
        fontSm.draw(batch, layout, bounds.x + bounds.width / 2f - 20, mapLineY - margin);
        // end waypoints section --------------

        fontMd.getData().setScale(1f);
    }

    private void drawPage2(SpriteBatch batch) {
        // fuel, speed, health(?)
        float x, y;
        float margin = 10f;
        float borderOffset = 5f;
        float mapLineY;
        float lineThickness, lineRadius;
        TextureRegion tex;

        fontMd.getData().setScale(0.7f);

        // controls heading -------------------
        mapLineY = headingBottomY - 2 * margin;
        layout.setText(fontMd, "Earth", Color.WHITE, bounds.width, Align.center, true);
        fontMd.draw(batch, layout, bounds.x, mapLineY);
        mapLineY -= layout.height;

        // header border-bottom
        lineThickness = 3f;
        lineRadius = 110f;
        assets.shapes.line(
            bounds.x + bounds.width / 2f - lineRadius, mapLineY - lineThickness - borderOffset,
            bounds.x + bounds.width / 2f + lineRadius, mapLineY - lineThickness - borderOffset,
            Color.GRAY, lineThickness);
        // end sector map heading -------------

        // earth section ----------------------
        tex = art.earth;
        mapLineY -= tex.getRegionHeight() + 2 * margin;

        y = mapLineY;
        x = bounds.x + (bounds.width - tex.getRegionWidth()) / 2f - (tex.getRegionWidth() / 2f + 8 * margin);
        batch.draw(tex, x, y);

        // draw text between minimap images
        layout.setText(fontSm, "Earth is\nhome base\n\nLaunch ship\nfrom here", Color.LIME, bounds.width, Align.center, true);
        fontSm.draw(batch, layout, bounds.x, mapLineY + (tex.getRegionHeight() + layout.height) / 2f);

        tex = art.launch;
        x = bounds.x + (bounds.width - tex.getRegionWidth()) / 2f + (tex.getRegionWidth() / 2f + 8 * margin);
        batch.draw(tex, x, y);
        // end controls section ---------------


        // move to next section...
        mapLineY -= margin;


        // fuel speed damage ---------------------------
        layout.setText(fontMd, "Fuel, Speed, Damage", Color.WHITE, bounds.width, Align.center, true);
        fontMd.draw(batch, layout, bounds.x, mapLineY);
        mapLineY -= layout.height;

        // header border-bottom
        lineThickness = 3f;
        lineRadius = 180f;
        assets.shapes.line(
            bounds.x + bounds.width / 2f - lineRadius, mapLineY - lineThickness - borderOffset,
            bounds.x + bounds.width / 2f + lineRadius, mapLineY - lineThickness - borderOffset,
            Color.GRAY, lineThickness);

        tex = art.fuel;
        mapLineY -= tex.getRegionHeight() + 2 * margin;

        y = mapLineY;
        x = bounds.x + (bounds.width - tex.getRegionWidth()) / 2f;// - (tex.getRegionWidth() / 2f + 10 * margin);
        batch.draw(tex, x, y);

        // draw waypoint explanation below waypoint image
        layout.setText(fontSm, "Fuel capacity = distance travelled", Color.CYAN, bounds.width, Align.center, true);
        fontSm.draw(batch, layout, bounds.x, mapLineY - margin);
        float lineHeight = layout.height;

        layout.setText(fontSm, "Launch Speed %", Color.WHITE, bounds.width, Align.center, true);
        fontSm.draw(batch, layout, bounds.x, mapLineY - lineHeight - 2 * margin);
        lineHeight = layout.height;

        layout.setText(fontSm, "Faster = Take More Damage", Color.CORAL, bounds.width, Align.center, true);
        fontSm.draw(batch, layout, bounds.x, mapLineY - 2 * lineHeight - 3 * margin);
        lineHeight = layout.height;

        layout.setText(fontSm, "Slower = Take Less Damage", Color.LIME, bounds.width, Align.center, true);
        fontSm.draw(batch, layout, bounds.x, mapLineY - 3 * lineHeight - 4 * margin);
        // end waypoints section --------------

        fontMd.getData().setScale(1f);
    }

}
