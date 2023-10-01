package lando.systems.ld54.objects;

import lando.systems.ld54.Assets;

public class PlayerShipPart extends Debris {

    public enum Type { nose, cabin, tail }

    public PlayerShipPart(Type type, Assets assets, float x, float y) {
        super(assets.playerShipParts.get(type), x, y);
    }

}
