package lando.systems.ld54.screens;

import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.equations.Expo;
import aurelienribon.tweenengine.primitives.MutableFloat;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
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
import lando.systems.ld54.utils.accessors.Vector2Accessor;

public class TitleScreen extends BaseScreen {

    private boolean drawUI = false;
    private final Texture titleScreenWordsBlueTrail;
    private Vector2 titleScreenWordsBlueTrailPos;
    private Vector2 titleScreenWordsBlueTrailSize;
    private Vector2 ePos;
    private MutableFloat eRot = new MutableFloat(0f);
    private Vector2 cPos;
    private Vector2 kPos;
    private Texture background;
    private Texture backgroundEmpty;
    private Texture backgroundTrash;
    private Texture titleScreenWordsNoCek;
    private Texture titleScreenWordsWhiteTrail;
    private Animation<TextureRegion> mercury;
    private float accum = 0f;
    private boolean swapBackgroundText = false;
    private Animation<TextureRegion> ship;
    private Vector2 shipPos;
    private boolean showShip = false;
    private boolean exploded = false;
    private float explosionAccum = 0f;
    private MutableFloat mercuryAlpha = new MutableFloat(1f);


    public TitleScreen() {
        background = Main.game.assets.titleBackgroundTrash;
        titleScreenWordsNoCek = Main.game.assets.titleScreenWordsNoCek;
        titleScreenWordsWhiteTrail = Main.game.assets.titleScreenWordsWhiteTrail;
        mercury = Main.game.assets.mercurySpin;
        titleScreenWordsBlueTrail = assets.titleScreenWordsBlueTrail;
        titleScreenWordsBlueTrailPos = new Vector2(Config.Screen.window_width / 2f, Config.Screen.window_height / 2f);
        titleScreenWordsBlueTrailSize = new Vector2(0, 0);
        shipPos = new Vector2(300f, 800f);
        ship = Main.game.assets.playerShipActive;

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
                exploded = true;
                swapBackgroundText = true;
            }))

//            // letters falling out
            .beginParallel()
                .push(Tween.to(mercuryAlpha, -1, 1f)
                    .target(0f))
                .push(Tween.to(ePos, Vector2Accessor.XY, 1f)
                    .target(0f, -100f))
                .push(Tween.to(cPos, Vector2Accessor.XY, 1.5f)
                    .ease(Expo.OUT)
                    .target(0f, -150f))
                .push(Tween.to(kPos, Vector2Accessor.XY, 2f)
                    .ease(Expo.OUT)
                    .target(0f, -200f))
                .push(Tween.to(eRot, -1, 1f)
                    .ease(Expo.OUT)
                    .target(20f))
            .end()
            // rocket starts here
            .push(Tween.call((type, source) -> {
                drawUI = true;
                showShip = true;
            }))
            .push(Tween.to(shipPos, Vector2Accessor.XY, .5f)
                .target(940, 370))
            .push(Tween.call((type, source) -> {
                showShip = false;
            }))
            .start(tween);
    }

    @Override
    public void alwaysUpdate(float delta) {

    }

    @Override
    public void update(float dt) {
        super.update(dt);
        accum+=dt;
        if (exploded) {
            explosionAccum += dt;
        }
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
            TextureRegion shipKeyframe = ship.getKeyFrame(accum);
            TextureRegion explosionKeyframe = Main.game.assets.explosion.getKeyFrame(explosionAccum);
            batch.draw(background, 0, 0, width, height);
            if (swapBackgroundText) {
                batch.draw(titleScreenWordsWhiteTrail, 0, 0, width, height);
                //void	draw(Texture texture, float x, float y, float originX, float originY, float width, float height, float scaleX, float scaleY, float rotation, int srcX, int srcY, int srcWidth, int srcHeight, boolean flipX, boolean flipY)
                batch.draw(assets.titleScreenWordsBlueE,ePos.x, ePos.y, 900f, 500f, width, height, 1f, 1f, eRot.floatValue(), 0, 0, assets.titleScreenWordsBlueE.getWidth(), assets.titleScreenWordsBlueE.getHeight(), false, false);
                batch.draw(assets.titleScreenWordsBlueC,cPos.x, cPos.y, width, height);
                batch.draw(assets.titleScreenWordsBlueK,kPos.x, kPos.y, width, height);
            }
            else {
                batch.draw(titleScreenWordsBlueTrail, titleScreenWordsBlueTrailPos.x, titleScreenWordsBlueTrailPos.y, titleScreenWordsBlueTrailSize.x, titleScreenWordsBlueTrailSize.y);
            }
            batch.setColor(1f, 1f, 1f, mercuryAlpha.floatValue());
            batch.draw(mercuryKeyframe, 940, 370, mercuryKeyframe.getRegionWidth() * 4, mercuryKeyframe.getRegionHeight() * 4);
            batch.setColor(Color.WHITE);
            // rotate the ship so it's heading toward mercury
            float rotation = (float) Math.atan2(370 - shipPos.y, 940 - shipPos.x);
            if (exploded) {
                batch.draw(explosionKeyframe, 840, 240, 500, 500);
            }
            if (showShip) {
                batch.draw(shipKeyframe, shipPos.x, shipPos.y, shipKeyframe.getRegionWidth() * 2, shipKeyframe.getRegionHeight() * 2, shipKeyframe.getRegionWidth() * 2, shipKeyframe.getRegionHeight() * 2, 1f, 1f, (float) Math.toDegrees(rotation));
            }
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
