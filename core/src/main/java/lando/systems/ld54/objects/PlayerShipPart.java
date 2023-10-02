package lando.systems.ld54.objects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import lando.systems.ld54.Assets;
import lando.systems.ld54.screens.GameScreen;

public class PlayerShipPart extends Debris {

    public enum Type { nose, cabin, tail, derelict }

    private static final Color DIM = new Color(0.5f, 0.5f, 0.5f, 1);

    public PlayerShipPart(GameScreen screen, Type type, Assets assets, float x, float y) {
        super(screen, assets.playerShipParts.get(type), x, y);
    }

    @Override
    public void draw(SpriteBatch batch) {
        batch.setColor(DIM);
        super.draw(batch);
        batch.setColor(Color.WHITE);
    }

}
