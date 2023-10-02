package lando.systems.ld54.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import lando.systems.ld54.Config;
import lando.systems.ld54.Main;
import lando.systems.ld54.audio.AudioManager;
import lando.systems.ld54.utils.typinglabel.TypingLabel;

public class CreditScreen extends BaseScreen {

    private final TypingLabel titleLabel;
    private final TypingLabel themeLabel;
    private final TypingLabel leftCreditLabel;
    private final TypingLabel rightCreditLabel;
    private final TypingLabel thanksLabel;
    private final TypingLabel disclaimerLabel;

    private final Animation<TextureRegion> catAnimation;
    private final Animation<TextureRegion> dogAnimation;
    private final Animation<TextureRegion> kittenAnimation;
    private final TextureRegion background;

    private final String title = "{GRADIENT=purple;cyan}Space Limited{ENDGRADIENT}";
    private final String theme = "Made for Ludum Dare 54: Limited Space";

    private final String thanks = "{GRADIENT=purple;cyan}Thanks for playing our game!{ENDGRADIENT}";
    private final String developers = "{COLOR=gray}Developed by:{COLOR=white}\n {GRADIENT=white;gray}Brian Ploeckelman{ENDGRADIENT} \n {GRADIENT=white;gray}Doug Graham{ENDGRADIENT} \n {GRADIENT=white;gray}Brian Rossman{ENDGRADIENT} \n {GRADIENT=white;gray}Jeffrey Hwang{ENDGRADIENT}";
    private final String artists = "{COLOR=gray}Art by:{COLOR=white}\n {GRADIENT=white;gray}Matt Neumann{ENDGRADIENT}";
    private final String emotionalSupport = "{COLOR=cyan}Emotional Support:{COLOR=white}\n  Asuka, Osha, Cherry \n       obi, and yoda";
    private final String music = "{COLOR=gray}Music, Sounds, and Miscellaneous:{COLOR=white}\n {GRADIENT=white;gray}Pete Valeo{ENDGRADIENT}";
    private final String libgdx = "Made with {COLOR=red}<3{COLOR=white}\nand LibGDX";
    private final String disclaimer = "{GRADIENT=black;gray}Disclaimer:{ENDGRADIENT}  {GRADIENT=gold;yellow}{JUMP=1.2}No bunnies were harmed in the making of this game{ENDJUMP}{ENDGRADIENT}";

    private float accum = 0f;

    public CreditScreen() {
        super();

        titleLabel = new TypingLabel(assets.font, title.toLowerCase(), 0f, Config.Screen.window_height - 15f);
        titleLabel.setWidth(Config.Screen.window_width);
        titleLabel.setFontScale(1f);

        themeLabel = new TypingLabel(assets.smallFont, theme.toLowerCase(), 0f, Config.Screen.window_height - 70f);
        themeLabel.setWidth(Config.Screen.window_width);
        themeLabel.setFontScale(1f);

        leftCreditLabel = new TypingLabel(assets.smallFont, developers.toLowerCase() + "\n\n" + emotionalSupport.toLowerCase() + "\n\n", 75f, Config.Screen.window_height / 2f + 135f);
        leftCreditLabel.setWidth(Config.Screen.window_width / 2f - 150f);
        leftCreditLabel.setLineAlign(Align.left);
        leftCreditLabel.setFontScale(1f);

        background = assets.pixelRegion;

        rightCreditLabel = new TypingLabel(assets.smallFont, artists.toLowerCase() + "\n\n" + music.toLowerCase() + "\n\n" + libgdx.toLowerCase(), Config.Screen.window_width / 2 + 75f, Config.Screen.window_height / 2f + 135f);
        rightCreditLabel.setWidth(Config.Screen.window_width / 2f - 150f);
        rightCreditLabel.setLineAlign(Align.left);
        rightCreditLabel.setFontScale(1f);

        thanksLabel = new TypingLabel(assets.smallFont, thanks.toLowerCase(), 0f, 115f);
        thanksLabel.setWidth(Config.Screen.window_width);
        thanksLabel.setLineAlign(Align.center);
        thanksLabel.setFontScale(1f);

        disclaimerLabel = new TypingLabel(assets.abandonedFont20, disclaimer, 0f, 50f);
        disclaimerLabel.setWidth(Config.Screen.window_width);
        thanksLabel.setLineAlign(Align.center);
        disclaimerLabel.setFontScale(.6f);

        catAnimation = assets.cherry;
        dogAnimation = assets.asuka;
        kittenAnimation = assets.osha;

        game.audioManager.playMusic(AudioManager.Musics.outro);

    }

    @Override
    public void alwaysUpdate(float delta) {

    }

    @Override
    public void update(float dt) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) || Gdx.input.isTouched()) {
            game.setScreen(new TitleScreen());
            return;
        }
        accum += dt;
        titleLabel.update(dt);
        themeLabel.update(dt);
        leftCreditLabel.update(dt);
        rightCreditLabel.update(dt);
        thanksLabel.update(dt);
        disclaimerLabel.update(dt);
    }

    @Override
    public void renderFrameBuffers(SpriteBatch batch) {

    }

    @Override
    public void render(SpriteBatch batch) {
        ScreenUtils.clear(.1f, .1f, .6f, 1f);

        batch.enableBlending();
        batch.setProjectionMatrix(windowCamera.combined);
        batch.begin();
        {
            //batch.draw(background, 0, 0, Config.Screen.window_width, Config.Screen.window_height);

            batch.setColor(0f, 0f, 0f, 0.6f);
            batch.draw(assets.pixelRegion, 25f, 130f, Config.Screen.window_width / 2f - 50f, 400f);
            batch.draw(assets.pixelRegion, Config.Screen.window_width / 2f + 25f, 130f, Config.Screen.window_width / 2f - 50f, 400f);

            batch.setColor(Color.WHITE);
            titleLabel.render(batch);
            themeLabel.render(batch);
            leftCreditLabel.render(batch);
            rightCreditLabel.render(batch);
            thanksLabel.render(batch);
            disclaimerLabel.render(batch);
            if (accum > 7.5) {
                TextureRegion cherryTexture = assets.cherry.getKeyFrame(accum);
                TextureRegion asukaTexture = assets.asuka.getKeyFrame(accum);
                TextureRegion oshaTexture = assets.osha.getKeyFrame(accum);
                batch.draw(oshaTexture, 200f, 255f);
                batch.draw(asukaTexture, 65f, 255f);
                batch.draw(cherryTexture, 385f, 250f);
            }
            if (accum > 8.5) {
                TextureRegion obiTexture = assets.obi.getKeyFrame(accum);
                TextureRegion yodaTexture = assets.yoda.getKeyFrame(accum);
                batch.draw(obiTexture, 100f, 210f);
                batch.draw(yodaTexture, 340f, 220f);
            }
            batch.setColor(Color.WHITE);
        }
        batch.end();
    }

}
