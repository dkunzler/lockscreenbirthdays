package de.devland.lockscreenbirthdays;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import de.devland.esperandro.Esperandro;
import de.devland.lockscreenbirthdays.prefs.DefaultPrefs;

public class BootRececeiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        DefaultPrefs defaultPrefs = Esperandro.getPreferences(DefaultPrefs.class, context);
        if (defaultPrefs.serviceEnabled()) {
            Intent service = new Intent(context, BirthdayService.class);
            context.startService(service);
        }
    }
}
