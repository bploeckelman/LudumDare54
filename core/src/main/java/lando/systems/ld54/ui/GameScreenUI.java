package lando.systems.ld54.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisProgressBar;
import com.kotcrab.vis.ui.widget.VisTable;
import lando.systems.ld54.Assets;
import lando.systems.ld54.Config;
import lando.systems.ld54.objects.PlayerShip;

public class GameScreenUI extends Group {

    private VisProgressBar fuelBar;
    private PlayerShip currentPlayerShip;

    public GameScreenUI(Assets assets) {
        super();
        setX(Config.Screen.window_width - 100f);
        setY(Config.Screen.window_height - 100f);
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

        fuelBar = new VisProgressBar(0f, 500f, 1f, false);
        fuelBar.setValue(0f);
        fuelBar.setWidth(100f);
        fuelBar.setHeight(10f);
        infoWindow.add(fuelBar).left().pad(5f);


        addActor(infoWindow);
    }

    public void setPlayerShip(PlayerShip playerShip) {
        currentPlayerShip = playerShip;
        fuelBar.setValue(currentPlayerShip.currentFuelLevel);
    }

}