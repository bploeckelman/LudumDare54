package lando.systems.ld54.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import lando.systems.ld54.Assets;
import lando.systems.ld54.Config;
import lando.systems.ld54.objects.Asteroid;
import lando.systems.ld54.objects.Debris;
import lando.systems.ld54.objects.PlayerShip;
import lando.systems.ld54.objects.Sector;
import lando.systems.ld54.screens.GameScreen;

public class MiniMap {

    static float WIDTH = Config.Screen.window_width * .2f;
    static float HEIGHT = Config.Screen.window_height * .2f;

    GameScreen screen;
    Rectangle bounds;
    Rectangle targetBounds;

    float accum;


    public MiniMap(GameScreen screen) {
        this.screen = screen;
        bounds = new Rectangle(5, screen.windowCamera.viewportHeight - HEIGHT - 5, WIDTH, HEIGHT);
        targetBounds = new Rectangle(bounds);

    }

    public void update(float dt) {
        accum += dt;
        if (screen.worldCamera.position.x < GameScreen.gameWidth/3) {
            targetBounds.x = screen.windowCamera.viewportWidth - WIDTH - 5;
        } else  if (screen.worldCamera.position.x > GameScreen.gameWidth * 2f/ 3f){
            targetBounds.x = 5;
        }
        if (targetBounds.x != bounds.x){
            float speed = 3.5f;
            float dist = bounds.x - targetBounds.x;
            float moveDist = Config.Screen.window_width * dt * speed;
            if (Math.abs(dist) < moveDist){
                bounds.x = targetBounds.x;
            } else {
                bounds.x -= moveDist * Math.signum(dist);
            }
        }
    }

    public void render(SpriteBatch batch) {
        float alpha = .4f;
        batch.setColor(1f, 1f, 1f, alpha);
        Assets.NinePatches.glass_blue.draw(batch, bounds.x-5, bounds.y-5, bounds.width+10, bounds.height+10);
        // draw behind fog
        batch.setColor(1f, .3f, .3f, 1f);
        for (Asteroid asteroid : screen.asteroids){
            Vector2 miniPos = convertToMiniMapSpace(asteroid.pos);
            batch.draw(screen.assets.whitePixel, bounds.x + miniPos.x, bounds.y + miniPos.y, 2, 2);
        }
        batch.setColor(.6f, .6f, 1f, 1f);
        for (Debris debris : screen.debris) {
            Vector2 miniPos = convertToMiniMapSpace(debris.getPosition());
            batch.draw(screen.assets.whitePixel, bounds.x + miniPos.x, bounds.y + miniPos.y, 2, 2);
        }
        for (Sector sector : screen.sectors){
            if (sector.encounter == null) continue;
            if (sector.isEncounterActive){
                float flashAmount = (MathUtils.sin(accum * 10f) * .5f + 1) * .8f + .2f;
                batch.setColor(flashAmount, flashAmount, 0, 1f);
            } else {
                batch.setColor(.3f, .3f, .3f, 1f);
            }
            Vector2 miniPos = convertToMiniMapSpace(sector.encounterBounds.getCenter(tempVec));
            batch.draw(screen.assets.fuzzyCircle, bounds.x + miniPos.x - 2, bounds.y + miniPos.y -2, 5, 5);
        }
        batch.setColor(1f, 1f, 1f, 1f);
        for (PlayerShip ship : screen.playerShips) {
            Vector2 miniPos = convertToMiniMapSpace(ship.pos);
            batch.draw(screen.assets.fuzzyCircle, bounds.x + miniPos.x - 2, bounds.y + miniPos.y -2, 5, 5);
        }

        // fog
        batch.setColor(.2f, .2f, .6f, 1f);
        batch.setShader(screen.assets.minimapShader);
        batch.draw(screen.fogOfWar.fogMaskTexture, bounds.x, bounds.y + bounds.height, bounds.width, -bounds.height);
        batch.setShader(null);

        // draw on top of fog
        for (Sector sector : screen.sectors){
            batch.setColor(.2f, .2f, .9f, .3f);
            Rectangle miniRect = convertToMiniMapSpace(sector.bounds);
            drawLine(batch, miniRect.x, miniRect.y, miniRect.x, miniRect.y + miniRect.height, 2f);
            drawLine(batch, miniRect.x, miniRect.y + miniRect.height, miniRect.x + miniRect.width, miniRect.y + miniRect.height, 2f);
            drawLine(batch, miniRect.x + miniRect.width, miniRect.y + miniRect.height, miniRect.x + miniRect.width, miniRect.y, 2f);
            drawLine(batch, miniRect.x + miniRect.width, miniRect.y, miniRect.x, miniRect.y, 2f);

            // draw a pixel at the encounter location above the fog if it's been scanned
            if (sector.scanned && sector.encounter != null) {
                if (!sector.isVisited()) {
                    var flashAmount = (MathUtils.sin(accum * 10f) * .5f + 1) * .8f + .2f;
                    batch.setColor(flashAmount, flashAmount, 0, 1f);
                } else {
                    batch.setColor(.3f, .3f, .3f, 1f);
                }
                Vector2 miniPos = convertToMiniMapSpace(sector.encounterBounds.getCenter(tempVec));
                batch.draw(screen.assets.fuzzyCircle, bounds.x + miniPos.x - 2, bounds.y + miniPos.y -2, 5, 5);
            }
        }

        batch.setColor(Color.WHITE);
    }

    Vector2 tempVec = new Vector2();
    private Vector2 convertToMiniMapSpace(Vector2 inVec) {
        tempVec.set(inVec);
        tempVec.x = tempVec.x / GameScreen.gameWidth * bounds.width;
        tempVec.y = tempVec.y / GameScreen.gameHeight * bounds.height;
        return tempVec;
    }

    Rectangle tempRec = new Rectangle();
    private Rectangle convertToMiniMapSpace(Rectangle inRec){
        tempRec.set(inRec);
        tempRec.x = tempRec.x / GameScreen.gameWidth * bounds.width;
        tempRec.y = tempRec.y / GameScreen.gameHeight * bounds.height;
        tempRec.width = tempRec.width / GameScreen.gameWidth * bounds.width;
        tempRec.height = tempRec.height / GameScreen.gameHeight * bounds.height;
        return tempRec;
    }

    private void drawLine(SpriteBatch batch, float x1, float y1, float x2, float y2, float lineWidth) {
        tempVec.set(x2, y2).sub(x1, y1);
        batch.draw(screen.assets.pixelRegion, x1 + bounds.x, y1 + bounds.y  - lineWidth/2f, 0, lineWidth/2f, tempVec.len(), lineWidth, 1, 1, tempVec.angleDeg());
    }
}
