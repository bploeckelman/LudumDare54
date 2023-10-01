package lando.systems.ld54;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.I18NBundle;
import lando.systems.ld54.assets.Asteroids;
import lando.systems.ld54.assets.InputPrompts;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class Assets implements Disposable {

    public enum Load { ASYNC, SYNC }

    public boolean initialized;

    public SpriteBatch batch;
    public ShapeDrawer shapes;
    public GlyphLayout layout;
    public AssetManager mgr;
    public TextureAtlas atlas;
    public I18NBundle strings;
    public InputPrompts inputPrompts;

    public BitmapFont font;
    public BitmapFont smallFont;
    public BitmapFont largeFont;
    public BitmapFont abandonedFont20;
    public BitmapFont abandonedFont50;
    public BitmapFont abandonedFont100;
    public BitmapFont starJediFont20;
    public BitmapFont starJediFont50;
    public BitmapFont starJediFont100;

    public Texture pixel;
    public Texture gdx;
    public Texture noiseTexture;
    public Texture whitePixel;
    public Texture starsTexture;

    public Animation<TextureRegion> asuka;
    public Animation<TextureRegion> cherry;
    public Animation<TextureRegion> osha;
    public Animation<TextureRegion> yoda; //german shep
    public Animation<TextureRegion> obi; //white lab
    public Animation<TextureRegion> mercurySpin;
    public Animation<TextureRegion> venusSpin;
    public Animation<TextureRegion> earthSpin;
    public Animation<TextureRegion> marsSpin;
    public Animation<TextureRegion> jupiterSpin;
    public Animation<TextureRegion> playerShip;
    public Animation<TextureRegion> launchArrow;

    public TextureRegion pixelRegion;
    public TextureRegion fuzzyCircle;

    public Array<ShaderProgram> randomTransitions;
    public ShaderProgram starWarsShader;
    public ShaderProgram blindsShader;
    public ShaderProgram fadeShader;
    public ShaderProgram radialShader;
    public ShaderProgram doomShader;
    public ShaderProgram pizelizeShader;
    public ShaderProgram doorwayShader;
    public ShaderProgram crosshatchShader;
    public ShaderProgram rippleShader;
    public ShaderProgram heartShader;
    public ShaderProgram stereoShader;
    public ShaderProgram circleCropShader;
    public ShaderProgram cubeShader;
    public ShaderProgram dreamyShader;
    public ShaderProgram flameShader;
    public ShaderProgram cooldownShader;
    public ShaderProgram influencerShader;
    public ShaderProgram goalShader;
    public ShaderProgram fogOfWarShader;
    public ShaderProgram plasmaShader;
    public ShaderProgram fogObjectShader;
    public ShaderProgram starfieldShader;

    public enum Patch {
        debug, panel, metal, glass,
        glass_green, glass_yellow, glass_red, glass_blue, glass_dim, glass_active;
        public NinePatch ninePatch;
        public NinePatchDrawable drawable;
    }

    public static class NinePatches {
        public static NinePatch plain;
        public static NinePatch plain_dim;
        public static NinePatch plain_gradient;
        public static NinePatch plain_gradient_highlight_yellow;
        public static NinePatch plain_gradient_highlight_green;
        public static NinePatch plain_gradient_highlight_red;
        public static NinePatch glass;
        public static NinePatch glass_active;
        public static NinePatch glass_blue;
        public static NinePatch glass_light_blue;
        public static NinePatch glass_corner_bl;
        public static NinePatch glass_corner_br;
        public static NinePatch glass_corner_tl;
        public static NinePatch glass_corner_tr;
        public static NinePatch glass_corners;
        public static NinePatch glass_red;
        public static NinePatch glass_yellow;
        public static NinePatch glass_green;
        public static NinePatch glass_tab;
        public static NinePatch glass_dim;
        public static NinePatch metal;
        public static NinePatch metal_blue;
        public static NinePatch metal_green;
        public static NinePatch metal_yellow;
        public static NinePatch shear;
    }

    public Music intro;
    public Music outro;
    public Music mainTheme;
    public Music mainThemeLowpass;

    public Sound coin;
    public Sound engineStart;
    public Sound engineRevving;
    public Sound engineLaunch;
    public Sound engineRunning;

    public Assets() {
        this(Load.SYNC);
    }

    public Assets(Load load) {
        initialized = false;

        // create a single pixel texture and associated region
        Pixmap pixmap = new Pixmap(2, 2, Pixmap.Format.RGBA8888);
        {
            pixmap.setColor(Color.WHITE);
            pixmap.drawPixel(0, 0);
            pixmap.drawPixel(1, 0);
            pixmap.drawPixel(0, 1);
            pixmap.drawPixel(1, 1);
            pixel = new Texture(pixmap);
        }
        pixmap.dispose();
        pixelRegion = new TextureRegion(pixel);

        batch = new SpriteBatch();
        shapes = new ShapeDrawer(batch, pixelRegion);
        layout = new GlyphLayout();

        mgr = new AssetManager();
        {
            mgr.load("sprites/sprites.atlas", TextureAtlas.class);
            mgr.load("ui/uiskin.json", Skin.class);
            mgr.load("i18n/strings", I18NBundle.class);

            mgr.load("images/libgdx.png", Texture.class);
            mgr.load("images/noise.png", Texture.class);
            mgr.load("images/pixel.png", Texture.class);
            mgr.load("images/stars.png", Texture.class);


            mgr.load("fonts/outfit-medium-20px.fnt", BitmapFont.class);
            mgr.load("fonts/outfit-medium-40px.fnt", BitmapFont.class);
            mgr.load("fonts/outfit-medium-80px.fnt", BitmapFont.class);

            //Load Music
            mgr.load("audio/music/mainTheme.ogg", Music.class);
            mgr.load("audio/music/mainThemeLowpass.ogg", Music.class);
            mgr.load("audio/music/intro.ogg", Music.class);
            mgr.load("audio/music/outro.ogg", Music.class);

            //Load Sounds
            mgr.load("audio/sounds/coin1.ogg", Sound.class);
            mgr.load("audio/sounds/engine-start.ogg", Sound.class);
            mgr.load("audio/sounds/engine-revving.ogg", Sound.class);
            mgr.load("audio/sounds/engine-launch.ogg", Sound.class);
            mgr.load("audio/sounds/engine-running.ogg", Sound.class);

        }

        if (load == Load.SYNC) {
            mgr.finishLoading();
            updateLoading();
        }
    }

    public float updateLoading() {
        if (!mgr.update()) return mgr.getProgress();
        if (initialized) return 1;

        atlas = mgr.get("sprites/sprites.atlas");

        // Initialize asset helpers
        Asteroids.init(this);

        // String replacement
        strings = mgr.get("i18n/strings", I18NBundle.class);

        // Misc references
        gdx = mgr.get("images/libgdx.png");
        whitePixel = mgr.get("images/pixel.png");
        noiseTexture = mgr.get("images/noise.png");
        noiseTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        noiseTexture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
        starsTexture = mgr.get("images/stars.png");
        starsTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        starsTexture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);

        // Animations
        cherry = new Animation<>(.1f, atlas.findRegions("pets/cat"), Animation.PlayMode.LOOP);
        asuka = new Animation<>(.1f, atlas.findRegions("pets/dog"), Animation.PlayMode.LOOP);
        osha = new Animation<>(.1f, atlas.findRegions("pets/kitten"), Animation.PlayMode.LOOP);
        yoda = new Animation<>(.1f, atlas.findRegions("pets/ross-dog"), Animation.PlayMode.LOOP);
        obi = new Animation<>(.1f, atlas.findRegions("pets/white-lab-dog"), Animation.PlayMode.LOOP);
        mercurySpin = new Animation<>(0.2f, atlas.findRegions("planets/mercury/mercury-spin"), Animation.PlayMode.LOOP);
        venusSpin = new Animation<>(0.2f, atlas.findRegions("planets/venus/venus-spin"), Animation.PlayMode.LOOP_REVERSED);
        earthSpin = new Animation<>(0.1f, atlas.findRegions("planets/earth/earth-spin"), Animation.PlayMode.LOOP);
        marsSpin = new Animation<>(0.08f, atlas.findRegions("planets/mars/mars-spin"), Animation.PlayMode.LOOP);
        jupiterSpin = new Animation<>(0.04f, atlas.findRegions("planets/gas/gas-spin"), Animation.PlayMode.LOOP);
        playerShip = new Animation<>(.1f, atlas.findRegions("ships/player/player-ship"), Animation.PlayMode.LOOP);
        launchArrow = new Animation<>(.1f, atlas.findRegions("launch-arrow/launch-arrow"), Animation.PlayMode.LOOP);

        // Fonts
        smallFont = mgr.get("fonts/outfit-medium-20px.fnt");
        smallFont.setUseIntegerPositions(false);
        font      = mgr.get("fonts/outfit-medium-40px.fnt");
        font.setUseIntegerPositions(false);
        largeFont = mgr.get("fonts/outfit-medium-80px.fnt");
        largeFont.setUseIntegerPositions(false);
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Abandoned-Bold.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 50;
        abandonedFont50 = generator.generateFont(parameter);
        parameter.size = 20;
        abandonedFont20 = generator.generateFont(parameter);
        parameter.size = 100;
        abandonedFont100 = generator.generateFont(parameter);
        generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Abandoned-Bold.ttf"));
        parameter.size = 50;
        starJediFont50 = generator.generateFont(parameter);
        parameter.size = 20;
        starJediFont20 = generator.generateFont(parameter);
        parameter.size = 100;
        starJediFont100 = generator.generateFont(parameter);
        generator.dispose();

        fuzzyCircle = atlas.findRegion("fuzzy-circle");

        inputPrompts = new InputPrompts(this);

        // Transition shaders
        randomTransitions = new Array<>();
        blindsShader = loadShader("shaders/transitions/default.vert", "shaders/transitions/blinds.frag");
        fadeShader = loadShader("shaders/transitions/default.vert", "shaders/transitions/dissolve.frag");
        radialShader = loadShader("shaders/transitions/default.vert", "shaders/transitions/radial.frag");
        doomShader = loadShader("shaders/transitions/default.vert", "shaders/transitions/doomdrip.frag");
        pizelizeShader = loadShader("shaders/transitions/default.vert", "shaders/transitions/pixelize.frag");
        doorwayShader = loadShader("shaders/transitions/default.vert", "shaders/transitions/doorway.frag");
        crosshatchShader = loadShader("shaders/transitions/default.vert", "shaders/transitions/crosshatch.frag");
        rippleShader = loadShader("shaders/transitions/default.vert", "shaders/transitions/ripple.frag");
        heartShader = loadShader("shaders/transitions/default.vert", "shaders/transitions/heart.frag");
        stereoShader = loadShader("shaders/transitions/default.vert", "shaders/transitions/stereo.frag");
        circleCropShader = loadShader("shaders/transitions/default.vert", "shaders/transitions/circlecrop.frag");
        cubeShader = loadShader("shaders/transitions/default.vert", "shaders/transitions/cube.frag");
        dreamyShader = loadShader("shaders/transitions/default.vert", "shaders/transitions/dreamy.frag");
        flameShader = loadShader("shaders/default.vert", "shaders/flame.frag");
        starWarsShader = loadShader("shaders/default.vert", "shaders/starwars.frag");
        cooldownShader = loadShader("shaders/default.vert", "shaders/cooldown.frag");
        influencerShader = loadShader("shaders/default.vert", "shaders/influencer.frag");
        goalShader = loadShader("shaders/default.vert", "shaders/goal.frag");

        randomTransitions.add(radialShader);
        randomTransitions.add(pizelizeShader);

        fogOfWarShader = loadShader("shaders/default.vert", "shaders/fog_of_war.frag");
        plasmaShader = loadShader("shaders/default.vert", "shaders/plasma.frag");
        fogObjectShader = loadShader("shaders/default.vert", "shaders/fog_of_war_object.frag");
        starfieldShader = loadShader("shaders/default.vert", "shaders/starfield.frag");

        // initialize patch values
        Patch.debug.ninePatch        = new NinePatch(atlas.findRegion("ninepatch/debug"), 2, 2, 2, 2);
        Patch.panel.ninePatch        = new NinePatch(atlas.findRegion("ninepatch/panel"), 15, 15, 15, 15);
        Patch.glass.ninePatch        = new NinePatch(atlas.findRegion("ninepatch/glass"), 8, 8, 8, 8);
        Patch.glass_green.ninePatch  = new NinePatch(atlas.findRegion("ninepatch/glass-green"), 8, 8, 8, 8);
        Patch.glass_yellow.ninePatch = new NinePatch(atlas.findRegion("ninepatch/glass-yellow"), 8, 8, 8, 8);
        Patch.glass_red.ninePatch  = new NinePatch(atlas.findRegion("ninepatch/glass-red"), 8, 8, 8, 8);
        Patch.glass_blue.ninePatch = new NinePatch(atlas.findRegion("ninepatch/glass-blue"), 8, 8, 8, 8);
        Patch.glass_dim.ninePatch    = new NinePatch(atlas.findRegion("ninepatch/glass-dim"), 8, 8, 8, 8);
        Patch.glass_active.ninePatch = new NinePatch(atlas.findRegion("ninepatch/glass-active"), 8, 8, 8, 8);
        Patch.metal.ninePatch        = new NinePatch(atlas.findRegion("ninepatch/metal"), 12, 12, 12, 12);

        Patch.debug.drawable        = new NinePatchDrawable(Patch.debug.ninePatch);
        Patch.panel.drawable        = new NinePatchDrawable(Patch.panel.ninePatch);
        Patch.glass.drawable        = new NinePatchDrawable(Patch.glass.ninePatch);
        Patch.glass_green.drawable  = new NinePatchDrawable(Patch.glass_green.ninePatch);
        Patch.glass_yellow.drawable = new NinePatchDrawable(Patch.glass_yellow.ninePatch);
        Patch.glass_dim.drawable    = new NinePatchDrawable(Patch.glass_dim.ninePatch);
        Patch.glass_active.drawable = new NinePatchDrawable(Patch.glass_active.ninePatch);
        Patch.metal.drawable        = new NinePatchDrawable(Patch.metal.ninePatch);
        Patch.glass_red.drawable    = new NinePatchDrawable(Patch.glass_red.ninePatch);
        Patch.glass_blue.drawable   = new NinePatchDrawable(Patch.glass_blue.ninePatch);

        NinePatches.plain_dim                       = new NinePatch(atlas.findRegion("ninepatch/plain-dim"),               12, 12, 12, 12);
        NinePatches.plain_gradient                  = new NinePatch(atlas.findRegion("ninepatch/plain-gradient"),           2,  2,  2,  2);
        NinePatches.plain_gradient_highlight_yellow = new NinePatch(atlas.findRegion("ninepatch/plain-gradient-highlight-yellow"), 2,  2,  2,  2);
        NinePatches.plain_gradient_highlight_green  = new NinePatch(atlas.findRegion("ninepatch/plain-gradient-highlight-green"), 2,  2,  2,  2);
        NinePatches.plain_gradient_highlight_red    = new NinePatch(atlas.findRegion("ninepatch/plain-gradient-highlight-red"), 2,  2,  2,  2);
        NinePatches.glass                           = new NinePatch(atlas.findRegion("ninepatch/glass"),                   12, 12, 12, 12);
        NinePatches.glass_blue                      = new NinePatch(atlas.findRegion("ninepatch/glass-blue"),              12, 12, 12, 12);
        NinePatches.glass_light_blue                = new NinePatch(atlas.findRegion("ninepatch/glass"),                   12, 12, 12, 12);
        NinePatches.glass_active                    = new NinePatch(atlas.findRegion("ninepatch/glass-active"),            12, 12, 12, 12);
        NinePatches.glass_corner_bl                 = new NinePatch(atlas.findRegion("ninepatch/glass-corner-bl"),         12, 12, 12, 12);
        NinePatches.glass_corner_br                 = new NinePatch(atlas.findRegion("ninepatch/glass-corner-br"),         12, 12, 12, 12);
        NinePatches.glass_corner_tl                 = new NinePatch(atlas.findRegion("ninepatch/glass-corner-tl"),         12, 12, 12, 12);
        NinePatches.glass_corner_tr                 = new NinePatch(atlas.findRegion("ninepatch/glass-corner-tr"),         12, 12, 12, 12);
        NinePatches.glass_corners                   = new NinePatch(atlas.findRegion("ninepatch/glass-corners"),           12, 12, 12, 12);
        NinePatches.glass_red                       = new NinePatch(atlas.findRegion("ninepatch/glass-red"),               12, 12, 12, 12);
        NinePatches.glass_yellow                    = new NinePatch(atlas.findRegion("ninepatch/glass-yellow"),            12, 12, 12, 12);
        NinePatches.glass_green                     = new NinePatch(atlas.findRegion("ninepatch/glass-green"),             12, 12, 12, 12);
        NinePatches.glass_tab                       = new NinePatch(atlas.findRegion("ninepatch/glass-tab"),               12, 12, 22, 12);
        NinePatches.glass_dim                       = new NinePatch(atlas.findRegion("ninepatch/glass-dim"),               12, 12, 22, 12);
        NinePatches.metal                           = new NinePatch(atlas.findRegion("ninepatch/metal"),                   12, 12, 12, 12);
        NinePatches.metal_blue                      = new NinePatch(atlas.findRegion("ninepatch/metal-blue"),              12, 12, 12, 12);
        NinePatches.metal_green                     = new NinePatch(atlas.findRegion("ninepatch/metal-green"),             12, 12, 12, 12);
        NinePatches.metal_yellow                    = new NinePatch(atlas.findRegion("ninepatch/metal-yellow"),            12, 12, 12, 12);
        NinePatches.shear                           = new NinePatch(atlas.findRegion("ninepatch/shear"),                   75, 75, 12, 12);

        // Music
        mainTheme = mgr.get("audio/music/mainTheme.ogg", Music.class);
        mainThemeLowpass = mgr.get("audio/music/mainThemeLowpass.ogg", Music.class);
        intro = mgr.get("audio/music/intro.ogg", Music.class);
        outro = mgr.get("audio/music/outro.ogg", Music.class);

        // Sounds
        coin = mgr.get("audio/sounds/coin1.ogg", Sound.class);
        engineStart = mgr.get("audio/sounds/engine-start.ogg", Sound.class);
        engineRevving = mgr.get("audio/sounds/engine-revving.ogg", Sound.class);
        engineLaunch = mgr.get("audio/sounds/engine-launch.ogg", Sound.class);
        engineRunning = mgr.get("audio/sounds/engine-running.ogg", Sound.class);

        initialized = true;
        return 1;
    }

    @Override
    public void dispose() {
        mgr.dispose();
        batch.dispose();
        pixel.dispose();
    }

    public static ShaderProgram loadShader(String vertSourcePath, String fragSourcePath) {
        ShaderProgram.pedantic = false;
        ShaderProgram shaderProgram = new ShaderProgram(
            Gdx.files.internal(vertSourcePath),
            Gdx.files.internal(fragSourcePath));
        String log = shaderProgram.getLog();

        if (!shaderProgram.isCompiled()) {
            Gdx.app.error("LoadShader", "compilation failed:\n" + log);
            throw new GdxRuntimeException("LoadShader: compilation failed:\n" + log);
        } else if (Config.Debug.shaders) {
            Gdx.app.debug("LoadShader", "ShaderProgram compilation log: " + log);
        }

        return shaderProgram;
    }

}
