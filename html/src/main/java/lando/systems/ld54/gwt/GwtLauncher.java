package lando.systems.ld54.gwt;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;
import com.badlogic.gdx.graphics.g2d.freetype.gwt.FreetypeInjector;
import lando.systems.ld54.Config;
import lando.systems.ld54.Main;

/** Launches the GWT application. */
public class GwtLauncher extends GwtApplication {
        @Override
        public GwtApplicationConfiguration getConfig () {
            // Resizable application, uses available space in browser with no padding:
//            GwtApplicationConfiguration cfg = new GwtApplicationConfiguration(true);
//            cfg.padVertical = 0;
//            cfg.padHorizontal = 0;
//            return cfg;
            // If you want a fixed size application, comment out the above resizable section,
            // and uncomment below:
            GwtApplicationConfiguration config = new GwtApplicationConfiguration(Config.Screen.window_width, Config.Screen.window_height);
            config.useGL30 = true;
            return config;
        }

        @Override
        public ApplicationListener createApplicationListener () {
            return new Main();
        }

    @Override
    public void onModuleLoad () {
        FreetypeInjector.inject(GwtLauncher.super::onModuleLoad);
    }
}
