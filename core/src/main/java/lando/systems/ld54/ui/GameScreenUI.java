package lando.systems.ld54.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisProgressBar;
import com.kotcrab.vis.ui.widget.VisTable;
import lando.systems.ld54.Assets;
import lando.systems.ld54.Config;
import lando.systems.ld54.objects.Player;
import lando.systems.ld54.objects.PlayerShip;

public class GameScreenUI extends Group {

    private VisProgressBar fuelBar;
    private Player player;
    private VisLabel scrapValueLabel;

    public GameScreenUI(Assets assets, Player player) {
        super();
        this.player = player;
        setX(Config.Screen.window_width - 100f);
        setY(Config.Screen.window_height / 2f);
        setWidth(40f);
        setHeight(100f);
        VisTable infoWindow = new VisTable();
        infoWindow.setFillParent(true);
        infoWindow.setColor(Color.WHITE);

        VisLabel fuelLabel = new VisLabel("Fuel: ");
        Label.LabelStyle style = new Label.LabelStyle();
        style.font = assets.starJediFont20;
        fuelLabel.setStyle(style);
        infoWindow.add(fuelLabel).left().pad(5f);
        infoWindow.row();

        fuelBar = new VisProgressBar(0f, player.MAX_FUEL, 1f, false);
        fuelBar.setValue(player.fuelLevel);
        fuelBar.setWidth(100f);
        fuelBar.setHeight(10f);
        infoWindow.add(fuelBar).left().pad(5f);
        infoWindow.row();

        VisLabel scrapLabel = new VisLabel("Scrap: ");
        scrapLabel.setStyle(style);
        infoWindow.add(scrapLabel).left().pad(5f);
        infoWindow.row();

        scrapValueLabel = new VisLabel("0");
        scrapValueLabel.setStyle(style);
        infoWindow.add(scrapValueLabel).left().pad(5f);
        infoWindow.row();



        addActor(infoWindow);
    }

    public void update() {
        fuelBar.setValue(player.fuelLevel);
        scrapValueLabel.setText(String.valueOf(player.scrap));
    }

}
