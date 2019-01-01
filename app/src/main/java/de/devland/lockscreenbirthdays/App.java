package de.devland.lockscreenbirthdays;

import android.app.Application;

import android.content.ComponentName;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import de.devland.esperandro.Esperandro;
import de.devland.lockscreenbirthdays.prefs.DefaultPrefs;
import de.devland.lockscreenbirthdays.service.UserPresentReceiver;
import de.devland.lockscreenbirthdays.util.Icon;
import de.devland.lockscreenbirthdays.util.NotificationChannels;


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
        Log.d("App", "onCreate: called");
        DefaultPrefs defaultPrefs = Esperandro.getPreferences(DefaultPrefs.class, this);
        defaultPrefs.initDefaults();
        Icon.valueOf(defaultPrefs.icon()).set();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // init nofification channel
            new NotificationChannels().init(this);
            // disable implicit user present receiver
            PackageManager pm = getPackageManager();
            ComponentName receiverComponent = new ComponentName(this, UserPresentReceiver.class);
            pm.setComponentEnabledSetting(receiverComponent, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, 0);
        }
    }



}
