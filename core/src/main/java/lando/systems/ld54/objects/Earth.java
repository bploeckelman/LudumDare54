package lando.systems.ld54.objects;

import lando.systems.ld54.Assets;

public class Earth extends Planet {

    public Earth(Assets assets, float x, float y) {
        super(assets.earthSpin, 96, 16);
        setPosition(x, y);
    }
}
