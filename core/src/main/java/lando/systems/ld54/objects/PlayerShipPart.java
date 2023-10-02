package lando.systems.ld54.objects;

import lando.systems.ld54.Assets;
import lando.systems.ld54.screens.GameScreen;

public class PlayerShipPart extends Debris {

    public enum Type { nose, cabin, tail }

    public PlayerShipPart(GameScreen screen, Type type, Assets assets, float x, float y) {
        super(screen, assets.playerShipParts.get(type), x, y);
    }

}
