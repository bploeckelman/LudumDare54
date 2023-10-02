package lando.systems.ld54.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Scaling;
import com.kotcrab.vis.ui.widget.*;
import lando.systems.ld54.Assets;
import lando.systems.ld54.Config;
import lando.systems.ld54.audio.AudioManager;
import lando.systems.ld54.encounters.Encounter;
import lando.systems.ld54.encounters.EncounterOption;
import lando.systems.ld54.encounters.EncounterOptionOutcome;
import lando.systems.ld54.screens.GameScreen;

import java.util.ArrayList;
import java.util.Objects;

public class EncounterUI extends Group {
    private Assets assets;
    private Skin skin;
    private AudioManager audio;
    private VisWindow encounterWindow;
    private VisLabel encounterTextLabel;
    VisImage encounterImageBox;
    VisImage characterImageBox;
    private GameScreen screen;
    private float animTimer = 0f;
    private Encounter encounter;

    private String encounterTitle = "";
    private String encounterText = "";
    private Animation<TextureRegion> encounterAnimation;
    private Animation<TextureRegion> characterAnimation;
    private ArrayList<EncounterOption> encounterOptions;
    private ArrayList<VisTextButton> optionButtons;
    private VisTextButton.VisTextButtonStyle optionStyle;

    private ObjectMap<TextureRegion, TextureRegionDrawable> encounterFrames = new ObjectMap();
    private ObjectMap<TextureRegion, TextureRegionDrawable> characterFrames;

    public EncounterUI(GameScreen screen, Assets assets, Skin skin, AudioManager audio) {
        super();
        this.screen = screen;
        this.assets = assets;
        this.skin = skin;
        this.audio = audio;
        encounterAnimation = null;
        characterAnimation = null;
        encounterOptions = new ArrayList<>();
    }

    public void setEncounter(Encounter encounter) {
        encounterTitle = encounter.title;
        encounterText = encounter.text;
        encounterAnimation = assets.encounterAnimationHashMap.get(encounter.imageKey);
        for (var enFrame : encounterAnimation.getKeyFrames()) {
            encounterFrames.put(enFrame, new TextureRegionDrawable(enFrame));
        }

        if (encounter.characterKey != null && encounter.characterKey != "") {
            characterAnimation = assets.encounterAnimationHashMap.get(encounter.characterKey);
            if (characterAnimation != null) {
                characterFrames = new ObjectMap<>();
                for (var charFrame : characterAnimation.getKeyFrames()) {
                    characterFrames.put(charFrame, new TextureRegionDrawable(charFrame));
                }
            }
        }

        encounterOptions.clear();
        this.encounter = encounter;
        for (EncounterOption option : encounter.options) {
            encounterOptions.add(option);
        }
        initializeUI();
    }

   @Override
    public void act(float delta) {
        super.act(delta);
        animTimer += delta;
        if (encounterAnimation != null) {
            encounterImageBox.setDrawable(encounterFrames.get(encounterAnimation.getKeyFrame(animTimer)));
        }

        if (characterAnimation != null) {
            characterImageBox.setDrawable(characterFrames.get(characterAnimation.getKeyFrame(animTimer)));
        }
    }

    public void initializeUI() {
        Window.WindowStyle defaultWindowStyle = skin.get("default", Window.WindowStyle.class);
        Window.WindowStyle glassWindowStyle = new Window.WindowStyle(defaultWindowStyle);
        glassWindowStyle.background = Assets.Patch.metal.drawable;
        glassWindowStyle.background = new TextureRegionDrawable(new TextureRegion(assets.encounterTexture));
        Rectangle encounterWindowBound = new Rectangle(Config.Screen.window_width / 8, 0, Config.Screen.window_width * 3 / 4, Config.Screen.window_height);

        encounterWindow = new VisWindow("", glassWindowStyle);
        encounterWindow.setSize(encounterWindowBound.width, encounterWindowBound.height);
        encounterWindow.setPosition(encounterWindowBound.x, encounterWindowBound.y);
        encounterWindow.setMovable(false);
        encounterWindow.align(Align.top | Align.center);
        encounterWindow.setModal(true);
        encounterWindow.setKeepWithinStage(false);
        encounterWindow.setColor(.8f, .9f, 1f, 0.9f);

        VisLabel encounterTitleLabel = new VisLabel(encounterTitle);
        Label.LabelStyle style = encounterTitleLabel.getStyle();
        style.font = assets.starJediFont50;
        encounterTitleLabel.setStyle(style);
        encounterTitleLabel.setAlignment(Align.center);
        encounterTitleLabel.setColor(Color.CYAN);
        encounterWindow.add(encounterTitleLabel).padTop(20f).padBottom(10f).width(encounterWindow.getWidth() - 100f);
        encounterWindow.row();

        encounterImageBox = new VisImage(encounterAnimation.getKeyFrame(0));
        float height = encounterWindow.getHeight() / 3f;
        float width = encounterWindow.getWidth() * 0.75f;

        var table = new Table();
        if (characterAnimation != null) {
            characterImageBox = new VisImage(characterAnimation.getKeyFrame(0));
            characterImageBox.setScaling(Scaling.stretch);
            table.add(characterImageBox).padRight(100);
        }

        table.add(encounterImageBox);
        encounterWindow.add(table).padTop(10f).padBottom(10f).width(width).height(height);

        encounterWindow.row();

        encounterTextLabel = new VisLabel(encounterText);
        Label.LabelStyle textStyle = encounterTitleLabel.getStyle();
        textStyle.font = assets.starJediFont20;
        textStyle.font.getData().setLineHeight(25f);

        encounterTextLabel.setStyle(style);
        encounterTextLabel.setAlignment(Align.left);
        encounterTextLabel.setColor(Color.YELLOW);
        encounterWindow.add(encounterTextLabel).padLeft(25f).padBottom(5f).width(encounterWindow.getWidth() - 100f).height(150f).align(Align.topLeft);
        encounterWindow.row();

        optionStyle = new VisTextButton.VisTextButtonStyle();
        optionStyle.font = assets.starJediFont20;
        optionStyle.fontColor = Color.YELLOW;
        optionStyle.up = Assets.Patch.glass.drawable;
        optionStyle.down = Assets.Patch.glass_dim.drawable;
        optionStyle.over = Assets.Patch.glass_dim.drawable;
        optionButtons = new ArrayList<>();
        float margin = 0f;
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
            encounterWindow.add(optionButton).padTop(10f).padBottom(5f).width(encounterWindow.getWidth() - 90f- margin).height(50f);
            optionButtons.add(optionButton);
            margin += 16f;
            encounterWindow.row();
        }

        addActor(encounterWindow);
    }

    private void optionClicked(EncounterOptionOutcome[] outcomes) {
        EncounterOptionOutcome outcome = calculateOutcome(outcomes);
//        audio.playSound(AudioManager.Sounds.coin);
        audio.playSound(AudioManager.Sounds.stingAliens2);
        encounterWindow.setBackground(new TextureRegionDrawable(new TextureRegion(assets.encounterOutcomeTexture)));
        encounterTextLabel.setText(outcome.text);
        destroyOptions();
        VisTextButton optionButton = new VisTextButton("You collect: " + outcome.value + " " + outcome.type.name().toLowerCase() , optionStyle);
        if(outcome.value == 0) {
            optionButton.setText("Continue");
        }
        else if (outcome.value > 0) {
            optionButton.setText("Collect " + outcome.value + " " + outcome.type.name().toLowerCase());
        }
        else if (outcome.value < 0) {
            optionButton.setText("Lose " + Math.abs(outcome.value) + " " + outcome.type.name().toLowerCase());
        }
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
                        screen.player.addScrap(outcome.value);
                        break;
                    case RADAR:
                        break;
                    case BOMB:
                        break;
                    case NOTHING:
                        break;
                }
                screen.finishEncounter(encounter);
            }
        });
        encounterWindow.add(optionButton).padTop(45f).padBottom(10f).width(encounterWindow.getWidth() - 106f).height(85f);
    }

    private void destroyOptions() {
        for (VisTextButton button : optionButtons) {
            Cell cell = encounterWindow.getCell(button);
            button.remove();
            // remove cell from table
            encounterWindow.getCells().removeValue(cell, true);
            encounterWindow.invalidate();
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
