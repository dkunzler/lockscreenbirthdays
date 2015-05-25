package de.devland.lockscreenbirthdays;

import android.app.Application;

import de.devland.esperandro.Esperandro;
import de.devland.lockscreenbirthdays.prefs.DefaultPrefs;
import de.devland.lockscreenbirthdays.util.Icon;


/**
 * Created by David Kunzler on 23.08.2014.
 */
public class App extends Application {
    private static App instance;

    public static App getInstance() {
        return App.instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        DefaultPrefs defaultPrefs = Esperandro.getPreferences(DefaultPrefs.class, this);
        defaultPrefs.initDefaults();
        Icon.valueOf(defaultPrefs.icon()).set();
    }

}
