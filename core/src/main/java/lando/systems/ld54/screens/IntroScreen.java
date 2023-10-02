package lando.systems.ld54.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;

import com.badlogic.gdx.utils.Align;
import lando.systems.ld54.Assets;
import lando.systems.ld54.Config;
import lando.systems.ld54.Main;
import lando.systems.ld54.audio.AudioManager;

public class IntroScreen extends BaseScreen {

    private static float textScale = 1.0f;
    float accum = 0;
    PerspectiveCamera perspectiveCamera;
    GlyphLayout layout;
    BitmapFont font;
    Rectangle skipRect;
    Rectangle speedRect;
    boolean done;

    // Last day Magic numbers
    Rectangle skipButtonRect = new Rectangle(Config.Screen.window_width - 150, 620, 100, 40);

    String text = "\n\n" +
        "Space. \n\n" +
        "" +
        "The Final Frontier. \n\n" +
        "\n\n" +
        "" +
       "These are the voyages of th- \n\n" +
        "" +
        "...\n\n" +
        "" +
        "Wait, is this the right franchise?\n\n" +
        "We've got the crawl going,\n" +
        "but the name of the game is...\n\n" +
        "" +
        "\n\n" +
        "And the sub-title is a play on...\n\n" +
        "\n\n" +
        "Anyway. Bottom line, Earth is FUCKED up. \n" +
        "Most of the rest of the solar system too." +
        "\n\n" +
        "And it's honestly kind of our fault." +
        "\n\n " +
        "\n\n " +
        "We've been living like tech-bro " +
        "rock stars for generations now, " +
        "which includes using near-Earth orbit as our space dump." +
        "\n\n" +
        "\n\n" +
        "We're trying to find a new planet to start fresh. " +
        "\n\n" +
        "\n\n" +
        "We send out ship after ship, but it's been rough." +
        "\n\n" +
        "When they aren't getting wrecked by the space debris, they're running out of fuel. " +
        "\n\n" +
        "\n\n" +
        "There is shit floating around EVERYWHERE up there, and this huge cloud of microplastics makes navigation basically impossible." +
        "\n\n" +


        "We know that each sector in the local system has a waypoint somewhere in it, though.\n\n" +
        "If we reach those, it'll help us chart our path and " +
        "figure out where to send the next ship." +
        "\n\n" +
        "Some of them even have fuel and other resources on them that might make subsequent trips easier..." +
        "\n\n" +
        "\n\n" +
        "We're also pretty sure one of the waypoints is actually a wormhole to a newer, better planet." +
        "\n\n" +
        "Which would be nice." +
        "\n\n" +
        "If we reach it, we'll be set!" +
        "\n\n " +
        "... but it does feel like our window of " +
        "opportunity is closing.\n\n" +
        "And the amount of space we have " +
        "to fly in is... rather limited." +

        "\n\n"
        ;


    public IntroScreen() {
        layout = new GlyphLayout();
        Assets assets = Main.game.assets;
        font = assets.font;
        font.getData().setScale(textScale);
        layout.setText(font, text, Color.WHITE, worldCamera.viewportWidth, Align.center, true);
        font.getData().setScale(1f);
        done = false;


        perspectiveCamera = new PerspectiveCamera(90, 1280, 800);
        perspectiveCamera.far=10000;
        perspectiveCamera.position.set(640, 0, 500);
        perspectiveCamera.lookAt(640, 400, 0);
        perspectiveCamera.update();
        skipRect = new Rectangle(windowCamera.viewportWidth - 170, 70, 150, 50);
        speedRect = new Rectangle(windowCamera.viewportWidth - 370, windowCamera.viewportHeight-70, 350, 50);
    }

    Vector3 mousePos = new Vector3();
    @Override
    public void update(float dt) {
        float speedMultiplier = 1.0f;

        if (Gdx.input.isTouched()){
            mousePos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            windowCamera.unproject(mousePos);
            if (!skipButtonRect.contains(mousePos.x, mousePos.y)) {
                speedMultiplier = 10f;
            }
        }

//        accum = MathUtils.clamp(accum, 0, layout.height);
        if (accum > layout.height) {
//            launchGame();
            done = true;
        }
        if (accum <= layout.height + 100f) {
//            launchGame();
            accum += 75*dt * speedMultiplier;
        }
        if (Gdx.input.justTouched()) {
            mousePos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            windowCamera.unproject(mousePos);
            if (skipButtonRect.contains(mousePos.x, mousePos.y)) {
                launchGame();
            }
        }
    }

    private void launchGame() {
        if (!exitingScreen){
            exitingScreen = true;
//            game.audioManager.stopMusic();
            game.setScreen(new GameScreen(true), assets.cubeShader, 3f);
        }
    }

    @Override
    public void alwaysUpdate(float delta) {

    }

    @Override
    public void renderFrameBuffers(SpriteBatch batch) {

    }

    @Override
    public void render(SpriteBatch batch) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT | (Gdx.graphics.getBufferFormat().coverageSampling?GL20.GL_COVERAGE_BUFFER_BIT_NV:0));

        batch.setProjectionMatrix(perspectiveCamera.combined);
        batch.begin();
        BitmapFont font = Main.game.assets.font;
        font.getData().setScale(textScale);
        font.setColor(.3f, .3f, .3f, 1.0f);
        font.draw(batch, text, 5, accum-5, worldCamera.viewportWidth, Align.center, true);
        font.setColor(Color.YELLOW);
        font.draw(batch, text, 0, accum, worldCamera.viewportWidth, Align.center, true);
        font.getData().setScale(1.0f);
//        batch.draw(textTexture, 0, 0, 1024, layout.height);

        batch.end();

        batch.setProjectionMatrix(windowCamera.combined);
        batch.begin();
        BitmapFont skipFont = assets.abandonedFont50;
        skipFont.getData().setScale(.4f);

        Assets.Patch.glass.ninePatch.draw(batch, skipButtonRect.x, skipButtonRect.y, skipButtonRect.width, skipButtonRect.height);

        if(!done) {

            assets.layout.setText(skipFont, "Skip", Color.WHITE, 200, Align.center, false);
            skipFont.draw(batch, assets.layout, skipButtonRect.x - 50, skipButtonRect.y + (assets.layout.height + skipButtonRect.height)/2f);
        }
        else {
            skipButtonRect.y = 20;


            assets.layout.setText(skipFont, "Play!", Color.WHITE, 200, Align.center, false);
            skipFont.draw(batch, assets.layout, skipButtonRect.x - 50, skipButtonRect.y + (assets.layout.height + skipButtonRect.height)/2f);
            Assets.Patch.glass.ninePatch.draw(batch, skipButtonRect.x, skipButtonRect.y, skipButtonRect.width, skipButtonRect.height);

        }


        skipFont.getData().setScale(1f);
        batch.end();

    }
}
