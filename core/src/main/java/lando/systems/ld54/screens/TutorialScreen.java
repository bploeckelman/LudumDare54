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
    private float accum;
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
    }
    private final Art art;

    public TutorialScreen() {
        this.numPages = 3;
        this.bounds = new Rectangle(60, 60, Config.Screen.window_width - 120, Config.Screen.window_height - 120);
        this.textColor = new Color(Color.WHITE);
        this.titleColor = new Color(.8f, .8f, 1f, 1f);

        this.page = 0;
        this.accum = 0;
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
    }

    @Override
    public void alwaysUpdate(float delta) {}

    @Override
    public void update(float delta) {
        super.update(delta);
        accum += delta;

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

//        fontMd.getData().setScale(.8f);
        layout.setText(fontMd, "How to play!", titleColor, bounds.width, Align.center, true);
        headingBottomY = bounds.y + bounds.height - 20 - layout.height;
        fontMd.draw(batch, layout, bounds.x, bounds.y + bounds.height - 20);
//        fontMd.getData().setScale(1f);

        batch.setColor(1, 1, 1, alpha);
        switch (page) {
            case 0: drawPage1(batch); break;
            case 1: drawPage2(batch); break;
            case 2: drawPage3(batch); break;
        }
        batch.setColor(Color.WHITE);

//        fontSm.getData().setScale(.5f);
        String continueString = "Click or press a key to continue";
        if (page == 2) continueString = "Let's start!";
        layout.setText(fontSm, continueString, textColor, bounds.width, Align.center, true);
        fontSm.draw(batch, layout, bounds.x, bounds.y + layout.height + 15);
//        fontSm.getData().setScale(1f);
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

        float delta = 60;
//        for (int i = 0; i < 4; i++){
//            TextureRegion tex = assets.gobbler.getKeyFrame(accum);
//            switch(i){
//                case 0:
//                    tex = assets.thief.getKeyFrame(accum);
//                    break;
//                case 1:
//                    tex = assets.reapo.getKeyFrame(accum);
//                    break;
//                case 2:
//                    tex = assets.turtle.getKeyFrame(accum);
//                    break;
//                case 3:
//                    tex = assets.gobbler.getKeyFrame(accum);
//                    break;
//                default:
//            }
//            batch.draw(tex, bounds.x + 445 + (i * (delta+10)), bounds.y + 400, delta, delta);
//        }
        layout.setText(fontMd, "Enemies", textColor, bounds.width, Align.center, true);
        fontMd.draw(batch, layout, bounds.x, bounds.y + 400 - 10);

//        batch.draw(assets.playerIdleRight.getKeyFrame(accum), Config.Screen.window_width/2f - 40, bounds.y + 290, 80, 80);
//        layout.setText(fontMd, "The Player (You)", textColor, bounds.width, Align.center, true);
//        fontMd.draw(batch, layout, bounds.x, bounds.y + 290 - 10);

        layout.setText(fontMd, "Move the player with WASD and press number keys [1-5] to use your powers\n\nThings bounce around like billiard balls.  Knock the packages into their goals by moving into them, but watch out enemies will show up to harass you.", textColor, bounds.width - 20, Align.center, true);
        fontMd.draw(batch, layout, bounds.x + 10, bounds.y + 200 - 10);
    }

    private void drawPage3(SpriteBatch batch) {
        // Controls ?

        // Powerups
        float delta = 60;
//        for (int i = 0; i < 5; i++){
//            PlayerAbility ability = PlayerAbility.bomb_throw;
//            switch(i){
//                case 0:
//                    ability = PlayerAbility.bomb_throw;
//                    break;
//                case 1:
//                    ability = PlayerAbility.speed_up;
//                    break;
//                case 2:
//                    ability = PlayerAbility.repulse;
//                    break;
//                case 3:
//                    ability = PlayerAbility.shield_360;
//                    break;
//                case 4:
//                    ability = PlayerAbility.fetch;
//                    break;
//                default:
//            }
//            batch.draw(ability.textureRegion, bounds.x + 100, bounds.y + 420 - (i * (delta+5)), delta, delta);
//            layout.setText(fontMd, ability.title + " - " + ability.description, textColor, bounds.width - 200, Align.left, true);
//            fontMd.draw(batch, layout, bounds.x + 190, bounds.y + 420 - (i * (delta+5)) + 30 + layout.height/2f);
//        }
        layout.setText(fontMd, "Powerups: You can activate a powerup with the corresponding number key (1-5), but watch how much stamina you have (The bar at the top), great powers come with great costs.", textColor, bounds.width, Align.center, true);
        fontMd.draw(batch, layout, bounds.x, bounds.y + 100 - 10);
    }

}
