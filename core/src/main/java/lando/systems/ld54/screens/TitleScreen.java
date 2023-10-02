package lando.systems.ld54.screens;

import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.equations.Expo;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import lando.systems.ld54.Config;
import lando.systems.ld54.Main;
import lando.systems.ld54.audio.AudioManager;
import lando.systems.ld54.ui.TitleScreenUI;
import lando.systems.ld54.utils.accessors.FloatAccessor;
import lando.systems.ld54.utils.accessors.Vector2Accessor;

public class TitleScreen extends BaseScreen {

    private boolean drawUI = false;
    private final Texture titleScreenWordsBlueTrail;
    private Vector2 titleScreenWordsBlueTrailPos;
    private Vector2 titleScreenWordsBlueTrailSize;
    private Vector2 ePos;
    private Float eRot = 0f;
    private Vector2 cPos;
    private Vector2 kPos;
    private Texture background;
    private Texture titleScreenWordsNoCek;
    private Texture titleScreenWordsWhiteTrail;
    private Animation<TextureRegion> mercury;
    private float accum = 0f;
    private boolean swapBackgroundText = false;


    public TitleScreen() {
        background = Main.game.assets.titleBackground;
        titleScreenWordsNoCek = Main.game.assets.titleScreenWordsNoCek;
        titleScreenWordsWhiteTrail = Main.game.assets.titleScreenWordsWhiteTrail;
        mercury = Main.game.assets.mercurySpin;
        titleScreenWordsBlueTrail = assets.titleScreenWordsBlueTrail;
        titleScreenWordsBlueTrailPos = new Vector2(Config.Screen.window_width / 2f, Config.Screen.window_height / 2f);
        titleScreenWordsBlueTrailSize = new Vector2(0, 0);

        ePos = new Vector2(0f, 0f);
        cPos = new Vector2(0f, 0f);
        kPos = new Vector2(0f, 0f);

        Gdx.input.setInputProcessor(uiStage);
        assets.intro.setLooping(true);
        audioManager.playMusic(AudioManager.Musics.intro);

        Timeline.createSequence()
            .pushPause(.2f)
            .beginParallel()
                .push(Tween.to(titleScreenWordsBlueTrailSize, Vector2Accessor.XY, 1f)
                    .ease(Expo.IN)
                    .target(Config.Screen.window_width, Config.Screen.window_height))
                .push(Tween.to(titleScreenWordsBlueTrailPos, Vector2Accessor.X, 1f)
                    .ease(Expo.IN)
                    .target(0f))
                .push(Tween.to(titleScreenWordsBlueTrailPos, Vector2Accessor.Y, 1f)
                    .ease(Expo.IN)
                    .target(0f))
            .end()
            .push(Tween.call((type, source) -> {
                swapBackgroundText = true;
            }))
            .beginParallel()
            .push(Tween.to(ePos, Vector2Accessor.XY, 1f)
                .ease(Expo.OUT)
                .target(0f, -50f))
            .push(Tween.to(cPos, Vector2Accessor.XY, 1f)
                .ease(Expo.OUT)
                .target(0f, -70f))
            .push(Tween.to(kPos, Vector2Accessor.XY, 1f)
                .ease(Expo.OUT)
                .target(0f, -80f))
            .push(Tween.to(eRot, FloatAccessor.VALUE, 1f)
                .ease(Expo.OUT)
                .target(Float.valueOf(360f)))
            .end()
            .start(tween);
    }

    @Override
    public void alwaysUpdate(float delta) {

    }

    @Override
    public void update(float dt) {
        super.update(dt);
        accum+=dt;
    }

    @Override
    public void renderFrameBuffers(SpriteBatch batch) {

    }

    @Override
    public void render(SpriteBatch batch) {
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);

        batch.setProjectionMatrix(worldCamera.combined);
        batch.begin();
        {
            float width = worldCamera.viewportWidth;
            float height = worldCamera.viewportHeight;
            TextureRegion mercuryKeyframe = mercury.getKeyFrame(accum);
            batch.draw(background, 0, 0, width, height);
            if (swapBackgroundText) {
                batch.draw(titleScreenWordsWhiteTrail, 0, 0, width, height);
                //void	draw(Texture texture, float x, float y, float originX, float originY, float width, float height, float scaleX, float scaleY, float rotation, int srcX, int srcY, int srcWidth, int srcHeight, boolean flipX, boolean flipY)
                batch.draw(assets.titleScreenWordsBlueE,ePos.x, ePos.y, 0f, 0f, width, height, 1f, 1f, eRot, 0, 0, assets.titleScreenWordsBlueE.getWidth(), assets.titleScreenWordsBlueE.getHeight(), false, false);
                batch.draw(assets.titleScreenWordsBlueC,cPos.x, cPos.y, width, height);
                batch.draw(assets.titleScreenWordsBlueK,kPos.x, kPos.y, width, height);
            }
            else {
                batch.draw(titleScreenWordsBlueTrail, titleScreenWordsBlueTrailPos.x, titleScreenWordsBlueTrailPos.y, titleScreenWordsBlueTrailSize.x, titleScreenWordsBlueTrailSize.y);
            }
            batch.draw(mercuryKeyframe, 940, 370, mercuryKeyframe.getRegionWidth() * 4, mercuryKeyframe.getRegionHeight() * 4);
            // draw text "LD45" with font assets.freeTypeFont
            assets.abandonedFont50.draw(batch, "LD54", 50, 100);
        }

        batch.end();
        if (drawUI) {
            uiStage.draw();
        }
    }

    @Override
    public void initializeUI() {
        super.initializeUI();
        TitleScreenUI titleScreenUI = new TitleScreenUI(this);
        uiStage.addActor(titleScreenUI);
    }
}
