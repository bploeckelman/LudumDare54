package lando.systems.ld54.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.widget.VisImage;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisProgressBar;
import com.kotcrab.vis.ui.widget.VisTable;
import lando.systems.ld54.Assets;
import lando.systems.ld54.Config;
import lando.systems.ld54.objects.Player;
import lando.systems.ld54.objects.PlayerShip;
import lando.systems.ld54.screens.GameScreen;
import lando.systems.ld54.utils.Utils;

import java.util.ArrayList;

public class GameScreenUI extends Group {

    private VisProgressBar fuelBar;
    private GameScreen screen;
    private VisLabel scrapValueLabel;
    ProgressBar.ProgressBarStyle progressBarStyle;
    ArrayList<VisImage> fuelImages = new ArrayList<>();
    ArrayList<Color> colors = new ArrayList<>() {{
        add(Color.BLACK); //0
        add(Color.RED); //1
        add(Color.SALMON); //2
        add(Color.ORANGE); //3
        add(Color.YELLOW); //4
        add(Color.LIME); //5
        add(Color.GREEN); //6
        add(Color.BLUE);//7
        add(Color.PURPLE); //8
    }};

    public GameScreenUI(Assets assets, GameScreen screen) {
        super();
        this.screen = screen;
        setX(Config.Screen.window_width - 200f);
        setY(Config.Screen.window_height / 2f);
        setWidth(200f);
        setHeight(300f);
        VisTable infoWindow = new VisTable();
        infoWindow.setBackground(Assets.Patch.glass.drawable);
        infoWindow.setFillParent(true);
        infoWindow.align(Align.center);
        VisLabel fuelLabel = new VisLabel("Fuel");
        Label.LabelStyle style = new Label.LabelStyle();
//        style.font = assets.smallFont;
        style.font = assets.abandonedFont20;
        fuelLabel.setStyle(style);
        infoWindow.add(fuelLabel).colspan(4).padBottom(5f);
//        fuelLabel.setColor(Color.BLACK);
        infoWindow.row();

        fuelBar = new VisProgressBar(0f, 1f, 0.01f, true);
        progressBarStyle = fuelBar.getStyle();
        fuelBar.setColor(Color.WHITE);
        float barValue;
        if (screen.currentShip == null) {
            barValue = screen.player.fuelLevel;
            fuelBar.setValue(barValue);
        } else {
            barValue = screen.currentShip.fuel / screen.currentShip.FUEL_PER_BAR_LEVEL;
            fuelBar.setValue(barValue);
        }
        infoWindow.add(fuelBar).center().colspan(4).padBottom(5f);
        infoWindow.row();
        // for dumb reason, i can't reuse the same image, so i have to make 4 of them
        VisImage fuelBarImage = new VisImage(new TextureRegionDrawable(assets.pickupsFuel.getKeyFrame(0f)));
        infoWindow.add(fuelBarImage).width(20f).height(20f);
        fuelImages.add(fuelBarImage);
        fuelBarImage = new VisImage(new TextureRegionDrawable(assets.pickupsFuel.getKeyFrame(0f)));
        infoWindow.add(fuelBarImage).width(20f).height(20f);
        fuelImages.add(fuelBarImage);
        fuelBarImage = new VisImage(new TextureRegionDrawable(assets.pickupsFuel.getKeyFrame(0f)));
        infoWindow.add(fuelBarImage).width(20f).height(20f);
        fuelImages.add(fuelBarImage);
        fuelBarImage = new VisImage(new TextureRegionDrawable(assets.pickupsFuel.getKeyFrame(0f)));
        infoWindow.add(fuelBarImage).width(20f).height(20f);
        fuelImages.add(fuelBarImage);
        infoWindow.row();
        fuelBarImage = new VisImage(new TextureRegionDrawable(assets.pickupsFuel.getKeyFrame(0f)));
        fuelImages.add(fuelBarImage);
        infoWindow.add(fuelBarImage).width(20f).height(20f);
        fuelBarImage = new VisImage(new TextureRegionDrawable(assets.pickupsFuel.getKeyFrame(0f)));
        fuelImages.add(fuelBarImage);
        infoWindow.add(fuelBarImage).width(20f).height(20f);
        fuelBarImage = new VisImage(new TextureRegionDrawable(assets.pickupsFuel.getKeyFrame(0f)));
        fuelImages.add(fuelBarImage);
        infoWindow.add(fuelBarImage).width(20f).height(20f);
        fuelBarImage = new VisImage(new TextureRegionDrawable(assets.pickupsFuel.getKeyFrame(0f)));
        fuelImages.add(fuelBarImage);
        infoWindow.add(fuelBarImage).width(20f).height(20f);
        infoWindow.row();
        VisLabel scrapLabel = new VisLabel("Scrap: ");

        scrapLabel.setStyle(style);
        infoWindow.add(scrapLabel).left().pad(5f).colspan(4).padTop(10f);
        infoWindow.row();

        scrapValueLabel = new VisLabel("0");
        scrapValueLabel.setStyle(style);
        infoWindow.add(scrapValueLabel).left().pad(5f).colspan(4);
        infoWindow.row();



        addActor(infoWindow);
    }

    public void update() {
        float barValue;
        if (screen.currentShip == null || screen.isLaunchPhase) {
            barValue = screen.player.fuelLevel;
        } else {
            barValue = screen.currentShip.fuel / screen.currentShip.FUEL_PER_BAR_LEVEL;
        }
        for (int i = 0; i < 8; i++) {
            if (barValue < i) {
                fuelImages.get(i).setVisible(false);
            } else {
                fuelImages.get(i).setVisible(true);
            }
        }
        progressBarStyle.knobAfter = new TextureRegionDrawable(Utils.getColoredTextureRegion(colors.get((int)barValue)));
        progressBarStyle.knobBefore = new TextureRegionDrawable(Utils.getColoredTextureRegion(colors.get((int)(barValue + 1))));
        for (int i = 7; i >= 0; i--) {
            if (barValue >= i) {
                fuelImages.get(i).setVisible(false);
                fuelBar.setValue(barValue - i);
                break;
            }
        }
        scrapValueLabel.setText(String.valueOf(screen.player.scrap));
    }

}
