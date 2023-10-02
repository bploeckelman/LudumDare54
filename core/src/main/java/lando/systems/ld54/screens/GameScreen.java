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
import com.badlogic.gdx.math.Vector2;
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
import lando.systems.ld54.particles.Particles;
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
    public final Array<Debris> debris = new Array<>();
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
    public boolean isLaunchPhase = false;
    public Particles particles;
    public Array<Encounter> encounters;

    public GameScreen() {
        physics = new PhysicsSystem(new Rectangle(0, 0, gameWidth, gameHeight));
        physicsObjects = new Array<>();
        physicsObjects.add(new GameBoundry(0, 0, gameWidth, 0));
        physicsObjects.add(new GameBoundry(gameWidth, 0, gameWidth, gameHeight));
        physicsObjects.add(new GameBoundry(gameWidth, gameHeight, 0, gameHeight));
        physicsObjects.add(new GameBoundry(0, gameHeight, 0, 0));

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
        physicsObjects.addAll(asteroids);

        var possibleGoals = new IntArray();
        var numSectors = SECTORS_WIDE * SECTORS_HIGH;
        for (int i = 0; i < numSectors; i++) {
            var x = i / SECTORS_WIDE;
            var y = i % SECTORS_WIDE;
            var sector = new Sector(this, getRandomEncounter(), x, y);
            sectors.add(sector);

            // save index as possible goal
            // if this sector is on an edge
            if (x == 0 || x == SECTORS_WIDE - 1
             || y == 0 || y == SECTORS_HIGH - 1) {
                possibleGoals.add(i);
            }
        }
        homeSector = sectors.get(numSectors / 2);

        // TODO - lots of fiddly shit here related to encounters and influencers, need to rework a bit
        homeSector.encounter = null;
        homeSector.pullPlayerShip.deactivate();
        homeSector.pushJunk.position.set(
            homeSector.bounds.x + homeSector.bounds.width / 2f,
            homeSector.bounds.y + homeSector.bounds.height / 2f
        );

        goalSector = sectors.get(possibleGoals.random());
        goalSector.encounter = null;

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
//        fogOfWar.addFogRectangle(799, 400, 1000, 1000, .2f);


        Gdx.input.setInputProcessor(new InputMultiplexer(uiStage, launcher, cameraController));
//        levelMusic.setLooping(true);
//        levelMusic.play();
//        audioManager.playMusic(AudioManager.Musics.mainTheme);
//        audioManager.playMusic(AudioManager.Musics.mainThemeLowpass);
        levelMusic.setVolume(audioManager.musicVolume.floatValue());
        levelMusicLowpass.setVolume(audioManager.musicVolume.floatValue());
        levelMusic.setLooping(true);
        levelMusicLowpass.setLooping(true);
//        levelMusic.play();

//        audioManager.playMusic(AudioManager.Musics.mainTheme);
        assets.mainTheme.play();
        assets.intro.stop();

        gameScreenUI = new GameScreenUI(assets, this);
        uiStage.addActor(gameScreenUI);
        miniMap = new MiniMap(this);
        particles = new Particles(assets);

        placeSatellites(earth);
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
        {
            if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
//            Config.Debug.general = true;
                if (!encounterShown) {
                    Encounter encounter = encounters.get(0);
                    encounter.sector = sectors.get(0);
                    startEncounter(encounter);
                }
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.S)) {
                sectors.forEach(Sector::scan);
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.F)) {
                audioManager.swapMusic();

//            audioManager.fadeMusic(AudioManager.Musics.mainTheme);
//            audioManager.fadeMusic(AudioManager.Musics.mainThemeLowpass);
            }
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
        debris.forEach(d -> d.update(dt));
        playerShips.forEach(ship -> {
            ship.update(dt);
            if (ship.trackMovement) {
                currentShip = ship;
                cameraController.targetPos.set(ship.pos.x, ship.pos.y, 0);
            }
        });
        asteroids.forEach(a -> a.update(dt));
        sectors.forEach(s -> s.update(dt));

        cameraController.update(dt);
        checkCurrentSector();
        particles.update(dt);
        super.update(dt);
    }

    private void checkCurrentSector() {
        if (currentShip != null) {
            for (int i = 0; i < sectors.size - 1; i++) {
                var sector = sectors.get(i);
                if (sector.bounds.contains(currentShip.pos)) {
                    if (sector.encounter != null && sector != homeSector && sector != goalSector && sector.isEncounterActive && sector.encounterBounds.contains(currentShip.pos)) {
                        startEncounter(sector.encounter);
                        sector.isEncounterActive = false;
                    }
                    if (currentShip.currentSector != i) {
                        currentShip.currentSector = i;
                        if (sector.encounter != null && sector != homeSector && sector != goalSector && !sector.isVisited()) {
                            Gdx.app.log("Discovered new sector!", "Sector " + i);
                            game.audioManager.playSound(AudioManager.Sounds.radarPing);
                        }

                        sector.setVisited(true);
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
            uiStage.setDebugAll(true);
        } else {
            uiStage.setDebugAll(false);
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
        particles.draw(batch, Particles.Layer.background);

        sectors.forEach(sector -> sector.drawBelowFogStuff(batch));

        // TODO - these should probably just be encounters rather than decorations
        planets.forEach(p -> p.render(batch));

        playerShips.forEach(ps -> ps.draw(batch));
        debris.forEach(d -> d.draw(batch));

        asteroids.forEach(a -> a.draw(batch));

        // TEMP - 'world' bounds
        assets.shapes.rectangle(0, 0, gameWidth, gameHeight, Color.MAGENTA, 4);
        if (Config.Debug.general){
            for (Collidable collidable : physicsObjects) {
                collidable.renderDebug(assets.shapes);
            }
        }
        launcher.render(batch);
        particles.draw(batch, Particles.Layer.middle);
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
        sectors.forEach(sector -> sector.drawShape(assets.shapes));
        batch.end();

        // Background
        var margin = 400;
        batch.setShader(assets.plasmaShader);
        batch.begin();
        assets.plasmaShader.setUniformf("u_time", accum);
        batch.draw(assets.noiseTexture, -margin / 2f, -margin / 2f, gameWidth + margin, gameHeight + margin);
        batch.end();
        batch.setShader(null);

        batch.begin();
        particles.draw(batch, Particles.Layer.overFog);
        sectors.forEach(s -> s.drawAboveFogStuff(batch));
        batch.end();
    }

    private void resetWorldCamera() {
        var centerShiftX = (gameWidth / 2f);
        var centerShiftY = (gameHeight / 2f);
        isLaunchPhase = true;

        worldCamera = new OrthographicCamera();
        worldCamera.setToOrtho(false, Config.Screen.window_width, Config.Screen.window_height);
        worldCamera.position.set(0, 0, 0);
        worldCamera.translate(centerShiftX, centerShiftY);
        worldCamera.update();

        if (cameraController == null) {
            cameraController = new PanZoomCameraController(worldCamera);
        } else {
            cameraController.reset(worldCamera);
        }
    }

    public void launchShip(float angle, float speed) {
        // speed is 0 - 1
        isLaunchPhase = false;
        var power = 300 * speed;
        var ship = new PlayerShip(this);
        physicsObjects.add(ship);
        ship.launch(angle, power);
        playerShips.add(ship);
//        useFuel(power * .01f); //TODO: debug, place it in the drag controller and limit drag per fuel level
//        resetWorldCamera();
    }

    private Encounter getRandomEncounter() {
        var file = Gdx.files.internal("encounters/battle_encounters.json");
        encounters = json.fromJson(Array.class, Encounter.class, file);
        var index = MathUtils.random(encounters.size - 1);
//        if(!Config.Debug.general) {
            return (Encounter) encounters.get(index);
//        }
//        else {
//            return (Encounter) encounters.get(1);
//        }
    }

    private void startEncounter(Encounter encounter) {
        encounterShown = true;
//        game.audioManager.stopAllSounds();
        assets.engineRunning.stop();
        game.audioManager.playSound(AudioManager.Sounds.stingIntense);
        encounterUI = new EncounterUI(this, assets, skin, audioManager);
        encounterUI.setEncounter(encounter);
        uiStage.addActor(encounterUI);

        if(!Config.Debug.general) {
            encounter.sector.pullPlayerShip.deactivate();
            encounter.sector.pushJunk.deactivate();
        }

        game.audioManager.swapMusic();
//        game.audioManager.swapMusic(levelMusic, levelMusicLowpass);
    }

    public void finishEncounter(Encounter encounter) {
        encounterShown = false;
        Time.pause_timer = 0f;
        encounterUI.remove();
        // TODO: SOUND HERE (WOOSH as it scans the sector)
        game.audioManager.playSound(AudioManager.Sounds.radarReveal, 1.0f);
//        game.audioManager.playSound(AudioManager.Sounds.radarPing, 2f);
        Gdx.app.log("Logging the finish Encounter", "True");
        float fogMargin = 50;
        if(!Config.Debug.general) {
            fogOfWar.addFogRectangle(encounter.sector.bounds.x - fogMargin, encounter.sector.bounds.y - fogMargin, encounter.sector.bounds.width + fogMargin*2f, encounter.sector.bounds.height + fogMargin*2f, .2f);
        }

        game.audioManager.swapMusic();
    }

    public void addFuel(int value) {
        // add fuel to player
        player.addFuel(value);
    }

    private void placeSatellites(Planet planet) {
        var vector = new Vector2();
        float satellitePath = planet.size * 1.5f;

        int satelliteCount = MathUtils.random(8, 14);
        float aveAngle = 360f / satelliteCount;
        while (satelliteCount-- > 0) {
            vector.set(satellitePath + MathUtils.random(30f), 0);

            float angle = aveAngle * satelliteCount + MathUtils.random(-5f, 5f);
            vector.rotateDeg(angle).add(planet.centerPosition);

            var satellite = new Satellite(planet, assets.satellites.random(), vector.x, vector.y);
            physicsObjects.add(satellite);
            debris.add(satellite);
        }
    }
}
