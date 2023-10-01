package lando.systems.ld54.screens;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.*;
import lando.systems.ld54.Config;
import lando.systems.ld54.assets.Asteroids;
import lando.systems.ld54.assets.PlanetManager;
import lando.systems.ld54.audio.AudioManager;
import lando.systems.ld54.components.DragLauncher;
import lando.systems.ld54.encounters.Encounter;
import lando.systems.ld54.fogofwar.FogOfWar;
import lando.systems.ld54.objects.*;
import lando.systems.ld54.physics.Collidable;
import lando.systems.ld54.physics.Influencer;
import lando.systems.ld54.physics.PhysicsSystem;
import lando.systems.ld54.ui.EncounterUI;
import lando.systems.ld54.ui.GameScreenUI;
import lando.systems.ld54.ui.MiniMap;
import lando.systems.ld54.utils.Time;
import lando.systems.ld54.utils.camera.PanZoomCameraController;

public class GameScreen extends BaseScreen {

    public static int SECTORS_WIDE = 5;
    public static int SECTORS_HIGH = 5;
    public static float gameWidth = Config.Screen.window_width * SECTORS_WIDE;
    public static float gameHeight = Config.Screen.window_height * SECTORS_HIGH;

    public final Vector3 mousePos = new Vector3();

    public final Earth earth;
    public final Array<Planet> planets = new Array<>();
    public final Array<Sector> sectors = new Array<>();
    public final Array<Asteroid> asteroids = new Array<>();
    public final Array<PlayerShip> playerShips = new Array<>();
    public final Array<PlayerShipPart> playerShipParts = new Array<>();
    public final Sector homeSector;
    public final Sector goalSector;

    private final Json json = new Json(JsonWriter.OutputType.json);

    public Music levelMusic;
    public Music levelMusicLowpass;

    public DragLauncher launcher;

    public Background background;
    public FrameBuffer foggedBuffer;
    public FrameBuffer exploredBuffer;
    public FrameBuffer fogMaskBuffer;
    public TextureRegion foggedTextureRegion;
    public TextureRegion exploredTextureRegion;
    public TextureRegion fogMaskTextureRegion;
    public FogOfWar fogOfWar;
    public PanZoomCameraController cameraController;
    public float accum;
    public boolean encounterShown = false;
    public EncounterUI encounterUI;
    public GameScreenUI gameScreenUI;
    public MiniMap miniMap;
    public PlayerShip currentShip;
    public Player player;
    public PhysicsSystem physics;
    public Array<Collidable> physicsObjects;
    public Array<Influencer> influencers;

    public GameScreen() {
        physics = new PhysicsSystem(new Rectangle(0, 0, gameWidth, gameHeight));
        physicsObjects = new Array<>();
        influencers = new Array<>();
        background = new Background(this, new Rectangle(0, 0, gameWidth, gameHeight));
        launcher = new DragLauncher(this);
        fogOfWar = new FogOfWar(gameWidth, gameHeight);
        player = new Player();

        var planetManager = new PlanetManager(this);
        earth = planetManager.createPlanets(planets);

        levelMusic = audioManager.musics.get(AudioManager.Musics.mainTheme);
        levelMusicLowpass = audioManager.musics.get(AudioManager.Musics.mainThemeLowpass);

        Asteroids.createTestAsteroids(asteroids);
        physicsObjects.addAll(asteroids)
        ;
        var possibleGoals = new IntArray();
        var numSectors = SECTORS_WIDE * SECTORS_HIGH;
        for (int i = 0; i < numSectors; i++) {
            var x = i / SECTORS_WIDE;
            var y = i % SECTORS_WIDE;
            var sector = new Sector(x, y, getRandomEncounter());
            sectors.add(sector);

            // save index as possible goal
            // if this sector is on an edge
            if (x == 0 || x == SECTORS_WIDE - 1
             || y == 0 || y == SECTORS_HIGH - 1) {
                possibleGoals.add(i);
            }
        }
        homeSector = sectors.get(numSectors / 2);
        goalSector = sectors.get(possibleGoals.random());

        Pixmap.Format format = Pixmap.Format.RGBA8888;
        int width = Config.Screen.framebuffer_width;
        int height = Config.Screen.framebuffer_height;

        foggedBuffer = new FrameBuffer(format, width, height, true);
        exploredBuffer = new FrameBuffer(format, width, height, true);
        fogMaskBuffer = new FrameBuffer(format, width, height, true);

        Texture foggedFrameBufferTexture = foggedBuffer.getColorBufferTexture();
        foggedFrameBufferTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        foggedTextureRegion = new TextureRegion(foggedFrameBufferTexture);
        foggedTextureRegion.flip(false, true);

        Texture exploredFrameBufferTexture = exploredBuffer.getColorBufferTexture();
        exploredFrameBufferTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        exploredTextureRegion = new TextureRegion(exploredFrameBufferTexture);
        exploredTextureRegion.flip(false, true);

        resetWorldCamera();

        Texture fogMaskFrameBufferTexture = fogMaskBuffer.getColorBufferTexture();
        fogMaskFrameBufferTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        fogMaskTextureRegion = new TextureRegion(fogMaskFrameBufferTexture);
        fogMaskTextureRegion.flip(false, true);

        worldCamera.position.set(gameWidth/2f, gameHeight/2f, 1f);
        worldCamera.update();

        //DEBUG or maybe just the start
        fogOfWar.addFogCircle(gameWidth/2f, gameHeight/2f, 300, 1.5f);
        fogOfWar.addFogRectangle(799, 400, 1000, 1000, .2f);


        Gdx.input.setInputProcessor(new InputMultiplexer(uiStage, launcher, cameraController));
//        levelMusic.setLooping(true);
//        levelMusic.play();
//        audioManager.playMusic(AudioManager.Musics.mainTheme);
//        audioManager.playMusic(AudioManager.Musics.mainThemeLowpass);
        levelMusic.setVolume(audioManager.musicVolume.floatValue());
        levelMusicLowpass.setVolume(audioManager.musicVolume.floatValue());
        levelMusic.setLooping(true);
        levelMusicLowpass.setLooping(true);
        levelMusic.play();

        gameScreenUI = new GameScreenUI(assets, player);
        uiStage.addActor(gameScreenUI);
        miniMap = new MiniMap(this);
    }

    @Override
    public void alwaysUpdate(float delta) {
        //uistage and audio should always update even when paused
        if (encounterShown) {
            Time.pause_timer = 2f;
        }
        uiStage.act(delta);
        gameScreenUI.update();
        audioManager.update(delta);
    }

    @Override
    public void update(float dt) {
        if (Gdx.app.getType() == Application.ApplicationType.Desktop && Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            Gdx.app.exit();
        }


        // TODO: DEBUG REMOVE ME
        if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
            if (!encounterShown) {
                startEncounter(getRandomEncounter());
            } else {
                finishEncounter();
            }
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.F)) {
            audioManager.swapMusic(levelMusic, levelMusicLowpass);

//            audioManager.fadeMusic(AudioManager.Musics.mainTheme);
//            audioManager.fadeMusic(AudioManager.Musics.mainThemeLowpass);
        }

        miniMap.update(dt);
        physics.update(dt, physicsObjects, influencers);

        uiStage.act();

        accum += dt;
        worldCamera.unproject(mousePos.set(Gdx.input.getX(), Gdx.input.getY(), 0));

        launcher.update(dt);

        background.update(dt);
        fogOfWar.update(dt);
        planets.forEach(p -> p.update(dt));
        playerShipParts.forEach(p -> p.update(dt));
        playerShips.forEach(x -> {
            x.update(dt);
            if (x.trackMovement) {
                currentShip = x;
                cameraController.targetPos.set(x.pos.x, x.pos.y, 0);
            }
        });
        asteroids.forEach(Asteroid::update);

        cameraController.update(dt);
        checkCurrentSector();
        super.update(dt);
    }

    private void checkCurrentSector() {
        if (currentShip != null) {
            for (int i = 0; i < sectors.size - 1; i++) {
                var sector = sectors.get(i);
                if (sector.bounds.contains(currentShip.pos)) {
                    if (currentShip.currentSector != i) {
                        currentShip.currentSector = i;
                        if (sector.encounter != null && sector != homeSector && sector != goalSector && !sector.isVisited()) {
                            startEncounter(sector.encounter);
                        }
                        sector.setVisited(true);
                        Gdx.app.log("Sector", "Entered sector " + i);
                    }
                    break;
                }
            }
        }
    }

    @Override
    public void renderFrameBuffers(SpriteBatch batch) {
        fogOfWar.render(batch);

        fogMaskBuffer.begin();
        ScreenUtils.clear(Color.BLACK);
        batch.setProjectionMatrix(worldCamera.combined);
        batch.begin();
        batch.draw(fogOfWar.fogMaskTexture, 0, gameHeight, gameWidth, -gameHeight);
        batch.end();
        fogMaskBuffer.end();

        foggedBuffer.begin();
        renderFogArea(batch);
        foggedBuffer.end();

        exploredBuffer.begin();
        renderExploredArea(batch);
        exploredBuffer.end();
    }

    @Override
    public void render(SpriteBatch batch) {
        ScreenUtils.clear(Color.DARK_GRAY);

        ShaderProgram fogShader = assets.fogOfWarShader;
        batch.setProjectionMatrix(windowCamera.combined);
        batch.setShader(fogShader);
        batch.begin();

        fogShader.setUniformf("u_time", fogOfWar.accum);
        fogShader.setUniformf("u_screenSize", worldCamera.viewportWidth, worldCamera.viewportHeight);
        fogShader.setUniformf("u_showFog", Config.Debug.general ? 0f : 1f);

        fogMaskTextureRegion.getTexture().bind(2);
        fogShader.setUniformi("u_texture2", 2);
        foggedTextureRegion.getTexture().bind(1);
        fogShader.setUniformi("u_texture1", 1);
        exploredTextureRegion.getTexture().bind(0);

        batch.draw(exploredTextureRegion.getTexture(), 0, worldCamera.viewportHeight, worldCamera.viewportWidth, -worldCamera.viewportHeight);

        batch.setShader(null);
        launcher.render(batch);
        miniMap.render(batch);
        if (Config.Debug.general) {
            batch.draw(fogOfWar.fogMaskTexture, 0, 0, windowCamera.viewportWidth / 6, windowCamera.viewportHeight / 6);

        }
        batch.end();

        uiStage.draw();

    }

    /**
     * Use this to render things that should be seen when fog is removed.
     * It is ok to render things that will be covered
     *
     * @param batch the Spritebatch to draw with
     */
    private void renderExploredArea(SpriteBatch batch) {
        ScreenUtils.clear(Color.BLACK);

        batch.setProjectionMatrix(worldCamera.combined);
        batch.begin();
        background.render(batch, true);

        sectors.forEach(sector -> sector.draw(assets.shapes));
        goalSector.draw(batch);

        planets.forEach(p -> p.render(batch));
        playerShips.forEach(ps -> ps.draw(batch));
        playerShipParts.forEach(p -> p.draw(batch));
        launcher.render(batch);

        asteroids.forEach(a -> a.draw(batch));

        // TEMP - 'world' bounds
        assets.shapes.rectangle(0, 0, gameWidth, gameHeight, Color.MAGENTA, 4);
        if (Config.Debug.general){
            for (Collidable collidable : physicsObjects) {
                collidable.renderDebug(assets.shapes);
            }
        }
        batch.end();
    }

    /**
     * This should render things that should be in the fagged area
     * Indicators of things to find etc
     * @param batch the SpriteBatch to draw with
     */
    private void renderFogArea(SpriteBatch batch) {
        ScreenUtils.clear(Color.BLACK);
        batch.setProjectionMatrix(worldCamera.combined);
        batch.begin();
        background.render(batch, false);

        // TEMP - 'world' bounds
        assets.shapes.rectangle(0, 0, gameWidth, gameHeight, Color.MAGENTA, 4);
        sectors.forEach(sector -> sector.draw(assets.shapes));
        batch.end();

        // Background
        var margin = 400;
        batch.setShader(assets.plasmaShader);
        batch.begin();
        assets.plasmaShader.setUniformf("u_time", accum);
        batch.draw(assets.noiseTexture, -margin / 2f, -margin / 2f, gameWidth + margin, gameHeight + margin);
        batch.end();
        batch.setShader(null);
    }

    private void resetWorldCamera() {
        var initialZoom = PanZoomCameraController.INITIAL_ZOOM;
        var centerShiftX = (gameWidth / 2f);
        var centerShiftY = (gameHeight / 2f);

        worldCamera = new OrthographicCamera();
        worldCamera.setToOrtho(false, Config.Screen.window_width, Config.Screen.window_height);
        worldCamera.position.set(0, 0, 0);
        worldCamera.translate(centerShiftX, centerShiftY);
        worldCamera.zoom = initialZoom;
        worldCamera.update();

        if (cameraController == null) {
            cameraController = new PanZoomCameraController(worldCamera);
        } else {
            cameraController.reset(worldCamera);
        }
    }

    public void launchShip(float angle, float power) {
        var ship = new PlayerShip(this);
        physicsObjects.add(ship);
        ship.launch(angle, power);
        playerShips.add(ship);
        useFuel(power * .01f); //TODO: debug, place it in the drag controller and limit drag per fuel level
        resetWorldCamera();
    }

    private Encounter getRandomEncounter() {
        var file = Gdx.files.internal("encounters/battle_encounters.json");
        var encounters = json.fromJson(Array.class, Encounter.class, file);
        var index = MathUtils.random(encounters.size - 1);
        return (Encounter) encounters.get(index);
    }

    private void startEncounter(Encounter encounter) {
        encounterShown = true;
        game.audioManager.stopAllSounds();
        encounterUI = new EncounterUI(this, assets, skin, audioManager);
        encounterUI.setEncounter(encounter);
        uiStage.addActor(encounterUI);
        game.audioManager.swapMusic(levelMusic, levelMusicLowpass);
    }

    public void finishEncounter() {
        encounterShown = false;
        Time.pause_timer = 0f;
        encounterUI.remove();
        game.audioManager.swapMusic(levelMusicLowpass, levelMusic);
    }

    public void addFuel(float value) {
        // add fuel to player
        player.addFuel(value);
    }

    public void useFuel(float value) {
        // use fuel from player
        player.useFuel(value);
    }

}
