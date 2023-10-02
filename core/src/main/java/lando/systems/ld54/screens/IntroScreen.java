package lando.systems.ld54.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Align;
import lando.systems.ld54.Assets;
import lando.systems.ld54.Main;
import lando.systems.ld54.audio.AudioManager;

public class IntroScreen extends BaseScreen {

    private static float textScale = 1.0f;
    float accum = 0;
    PerspectiveCamera perspectiveCamera;
    GlyphLayout layout;
    BitmapFont font;

    String text = "\n\n\n\n\nThis is the story, all about how\n\n" +
        "my life got flip turned upside down\n\n" +
        "And I'd like to take a minute so just sit right there\n\n" +
        "I'll tell you all about how corporate\n\n"+
        "imperialism destroyed a country called POTASSIA\n\n"+
        "\n\n" +
        "--------\n\n";

    public IntroScreen() {
        layout = new GlyphLayout();
        Assets assets = Main.game.assets;
        font = assets.font;
        font.getData().setScale(textScale);
        layout.setText(font, text, Color.WHITE, worldCamera.viewportWidth, Align.center, true);
        font.getData().setScale(1f);

        // TODO: Start intro Music
//        audioManager.playMusic(AudioManager.Musics.intro);

        perspectiveCamera = new PerspectiveCamera(90, 1280, 800);
        perspectiveCamera.far=10000;
        perspectiveCamera.position.set(640, 0, 500);
        perspectiveCamera.lookAt(640, 400, 0);
        perspectiveCamera.update();
    }

    @Override
    public void update(float dt) {
        float speedMultiplier = 1.0f;

        if (Gdx.input.isTouched()){
            speedMultiplier = 10f;
        }
        accum += 75*dt * speedMultiplier;
//        accum = MathUtils.clamp(accum, 0, layout.height);
        if (accum > layout.height && Gdx.input.justTouched()) {
            launchGame();
        }
        if (accum >= layout.height + 500f) {
            launchGame();
        }
    }

    private void launchGame() {
        if (!exitingScreen){
            exitingScreen = true;
//            game.audioManager.stopMusic();
            game.setScreen(new GameScreen(), assets.cubeShader, 3f);
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
    }
}
