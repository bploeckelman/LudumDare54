package lando.systems.ld54.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.widget.*;
import lando.systems.ld54.Assets;
import lando.systems.ld54.Config;
import lando.systems.ld54.audio.AudioManager;
import lando.systems.ld54.encounters.Encounter;
import lando.systems.ld54.encounters.EncounterOption;
import lando.systems.ld54.encounters.EncounterOptionOutcome;
import lando.systems.ld54.screens.GameScreen;

import java.util.ArrayList;
import java.util.HashMap;

public class EncounterUI extends Group {
    private Assets assets;
    private Skin skin;
    private AudioManager audio;
    private VisWindow encounterWindow;
    private VisLabel encounterTextLabel;
    private GameScreen screen;

    private String encounterTitle = "";
    private String encounterText = "";
    private TextureRegion encounterImage;
    private ArrayList<EncounterOption> encounterOptions;
    private ArrayList<VisTextButton> optionButtons;
    private HashMap<String, TextureRegion> textureRegionHashMap = new HashMap<>();
    private VisTextButton.VisTextButtonStyle optionStyle;

    public EncounterUI(GameScreen screen, Assets assets, Skin skin, AudioManager audio) {
        super();
        this.screen = screen;
        this.assets = assets;
        this.skin = skin;
        this.audio = audio;
        textureRegionHashMap.put("asuka", assets.asuka.getKeyFrame(0));
        textureRegionHashMap.put("cherry", assets.cherry.getKeyFrame(0));
        textureRegionHashMap.put("spaceship", assets.playerShip.getKeyFrame(0));
        textureRegionHashMap.put("nebula", assets.marsSpin.getKeyFrame(0));
        textureRegionHashMap.put("blackhole", assets.fuzzyCircle);
        textureRegionHashMap.put("mysteriousplanet", assets.venusSpin.getKeyFrame(0));
        textureRegionHashMap.put("asteroidfield", assets.fuzzyCircle);
        textureRegionHashMap.put("abandonedstation", assets.earthSpin.getKeyFrame(0));
        textureRegionHashMap.put("spacepirates", assets.obi.getKeyFrame(0));
        textureRegionHashMap.put("meteorshower", assets.mercurySpin.getKeyFrame(0));
        textureRegionHashMap.put("gasgiantmoon", assets.yoda.getKeyFrame(0));
        textureRegionHashMap.put("interstellaranomaly", assets.osha.getKeyFrame(0));
        textureRegionHashMap.put("asteroidmining", assets.earthSpin.getKeyFrame(0));
        encounterImage = assets.obi.getKeyFrame(0);
        encounterOptions = new ArrayList<>();
    }

    public void setEncounter(Encounter encounter) {
        encounterTitle = encounter.title;
        encounterText = encounter.text;
        encounterImage = textureRegionHashMap.get(encounter.imageKey);
        encounterOptions.clear();
        for (EncounterOption option : encounter.options) {
            encounterOptions.add(option);
        }
        initializeUI();
    }

    public void initializeUI() {
        Window.WindowStyle defaultWindowStyle = skin.get("default", Window.WindowStyle.class);
        Window.WindowStyle glassWindowStyle = new Window.WindowStyle(defaultWindowStyle);
        glassWindowStyle.background = Assets.Patch.metal.drawable;

        Rectangle encounterWindowBound = new Rectangle(Config.Screen.window_width / 8, 0, Config.Screen.window_width * 3 / 4, Config.Screen.window_height);

        encounterWindow = new VisWindow("", glassWindowStyle);
        encounterWindow.setSize(encounterWindowBound.width, encounterWindowBound.height);
        encounterWindow.setPosition(encounterWindowBound.x, encounterWindowBound.y);
        encounterWindow.setMovable(false);
        encounterWindow.align(Align.top | Align.center);
        encounterWindow.setModal(true);
        encounterWindow.setKeepWithinStage(false);
        encounterWindow.setColor(1f, 1f, 1f, 0.8f);

        VisLabel encounterTitleLabel = new VisLabel(encounterTitle);
        Label.LabelStyle style = encounterTitleLabel.getStyle();
        style.font = assets.starJediFont50;
        encounterTitleLabel.setStyle(style);
        encounterTitleLabel.setAlignment(Align.center);
        encounterTitleLabel.setColor(Color.BLACK);
        encounterWindow.add(encounterTitleLabel).padTop(10f).padBottom(10f).width(encounterWindow.getWidth() - 100f);
        encounterWindow.row();

        VisImage encounterImageBox = new VisImage(encounterImage);
        float size = encounterWindow.getHeight() / 3f;
        encounterWindow.add(encounterImageBox).padTop(10f).padBottom(10f).width(size).height(size);
        encounterWindow.row();

        encounterTextLabel = new VisLabel(encounterText);
        Label.LabelStyle textStyle = encounterTitleLabel.getStyle();
        textStyle.font = assets.starJediFont20;
        encounterTextLabel.setStyle(style);
        encounterTextLabel.setAlignment(Align.left);
        encounterTextLabel.setColor(Color.BLACK);
        encounterWindow.add(encounterTextLabel).padTop(10f).padBottom(10f).width(encounterWindow.getWidth() - 100f);
        encounterWindow.row();

        optionStyle = new VisTextButton.VisTextButtonStyle();
        optionStyle.font = assets.starJediFont20;
        optionStyle.fontColor = Color.BLACK;
        optionStyle.up = Assets.Patch.glass.drawable;
        optionStyle.down = Assets.Patch.glass_dim.drawable;
        optionStyle.over = Assets.Patch.glass_dim.drawable;
        optionButtons = new ArrayList<>();
        for (EncounterOption option : encounterOptions) {
            VisTextButton optionButton = new VisTextButton(option.text, optionStyle);
            optionButton.setHeight(20f);
            optionButton.setStyle(optionStyle);
            optionButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    optionClicked(option.possibleOutcomes);
                }
            });
            encounterWindow.add(optionButton).padTop(10f).padBottom(10f).width(encounterWindow.getWidth() - 100f).height(50f);
            optionButtons.add(optionButton);
            encounterWindow.row();
        }

        addActor(encounterWindow);
    }

    private void optionClicked(EncounterOptionOutcome[] outcomes) {
        EncounterOptionOutcome outcome = calculateOutcome(outcomes);
        audio.playSound(AudioManager.Sounds.coin);
        encounterTextLabel.setText(outcome.text);
        destroyOptions();
        VisTextButton optionButton = new VisTextButton(outcome.type.name() + " " + outcome.value, optionStyle);
        optionButton.setHeight(20f);
        optionButton.setStyle(optionStyle);
        optionButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                screen.encounterShown = false;
                switch (outcome.type) {
                    case FUEL:
                        screen.addFuel(outcome.value);
                        break;
                    case SCRAP:
                        break;
                    case RADAR:
                        break;
                    case BOMB:
                        break;
                    case NOTHING:
                        break;
                }
                screen.finishEncounter();
            }
        });
        encounterWindow.add(optionButton).padTop(10f).padBottom(10f).width(encounterWindow.getWidth() - 100f).height(50f);
    }

    private void destroyOptions() {
        for (VisTextButton button : optionButtons) {
            button.remove();
        }
    }


    private EncounterOptionOutcome calculateOutcome(EncounterOptionOutcome[] outcomes) {
        float totalWeight = 0;
        for (EncounterOptionOutcome outcome : outcomes) {
            totalWeight += outcome.weight;
        }
        float random = (float) Math.random() * totalWeight;
        float currentWeight = 0;
        for (EncounterOptionOutcome outcome : outcomes) {
            currentWeight += outcome.weight;
            if (random <= currentWeight) {
                return outcome;
            }
        }
        return outcomes[0];
    }
}
