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

        fontLg.getData().setScale(.8f);
        layout.setText(fontLg, "How to play!", titleColor, bounds.width, Align.center, true);
        headingBottomY = bounds.y + bounds.height - 20 - layout.height;
        fontLg.draw(batch, layout, bounds.x, bounds.y + bounds.height - 20);
        fontLg.getData().setScale(1f);

//        float height = layout.height;
//        fontMd.getData().setScale(.5f);
//        layout.setText(fontMd, "(We somehow had time for this..)", titleColor, bounds.width, Align.center, true);
//        fontMd.draw(batch, layout, bounds.x, bounds.y + bounds.height - 40 - height);

        batch.setColor(1, 1, 1, alpha);
        switch (page) {
            case 0: drawPage1(batch); break;
            case 1: drawPage2(batch); break;
            case 2: drawPage3(batch); break;
        }
        batch.setColor(Color.WHITE);

        fontMd.getData().setScale(.5f);
        String continueString = "Click or press a key to continue";
        if (page == 2) continueString = "Let's start!";
        layout.setText(fontMd, continueString, textColor, bounds.width, Align.center, true);
        fontMd.draw(batch, layout, bounds.x, bounds.y + layout.height + 15);
        fontMd.getData().setScale(1f);
        batch.end();
    }

    @Override
    public void renderFrameBuffers(SpriteBatch batch) {}

    private void drawPage1(SpriteBatch batch) {
        // map, encounters, goal(?)
        float margin = 10f;
        float x, y;
        float lineThickness, lineRadius;
        TextureRegion tex;

        // sector map heading -----------------
        var mapLineY = headingBottomY - 4 * margin;
        layout.setText(fontMd, "Sector Map", Color.WHITE, bounds.width, Align.center, true);
        fontMd.draw(batch, layout, bounds.x, mapLineY);
        mapLineY -= layout.height;

        // header border-bottom
        lineThickness = 4f;
        lineRadius = 140f;
        assets.shapes.line(
            bounds.x + bounds.width / 2f - lineRadius, mapLineY - lineThickness - 2,
            bounds.x + bounds.width / 2f + lineRadius, mapLineY - lineThickness - 2,
            Color.LIGHT_GRAY, lineThickness);
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


        // waypoints heading ------------------
        // end waypoints heading --------------



//        float delta = 60;
//        for (int i = 0; i < 4; i++){
//            TextureRegion tex = assets.cargoCyan.getKeyFrame(accum);
//            switch(i){
//                case 0:
//                    tex = assets.cargoCyan.getKeyFrame(accum);
//                    break;
//                case 1:
//                    tex = assets.cargoRed.getKeyFrame(accum);
//                    break;
//                case 2:
//                    tex = assets.cargoGreen.getKeyFrame(accum);
//                    break;
//                case 3:
//                    tex = assets.cargoYellow.getKeyFrame(accum);
//                    break;
//                default:
//            }
//            batch.draw(tex, bounds.x + 445 + (i * (delta+10)), bounds.y + 400, delta, delta);
//        }
//        layout.setText(fontMd, "Packages", textColor, bounds.width, Align.center, true);
//        fontMd.draw(batch, layout, bounds.x, bounds.y + 400 - 10);

//        TextureRegion tex = Goal.Type.cyan.baseAnim.getKeyFrame(accum);
//        batch.draw(tex, Config.Screen.window_width/2f - 30, bounds.y + 290, delta, delta);

//        layout.setText(fontMd, "Goals", textColor, bounds.width, Align.center, true);
//        fontMd.draw(batch, layout, bounds.x, bounds.y + 290 - 10);
//
//        layout.setText(fontMd, "You need to \"deliver\" the packages to their corresponding goals.\nEach level has a quota of packages that need to be delivered to move to the next level.", textColor, bounds.width, Align.center, true);
//        fontMd.draw(batch, layout, bounds.x, bounds.y + 200 - 10);
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
