package de.devland.lockscreenbirthdays.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import android.support.v4.content.ContextCompat;
import android.util.Log;

import de.devland.esperandro.Esperandro;
import de.devland.lockscreenbirthdays.prefs.DefaultPrefs;

public class BootReceiver extends BroadcastReceiver {
    private static final String TAG = "BootReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive: booted");
        DefaultPrefs defaultPrefs = Esperandro.getPreferences(DefaultPrefs.class, context);
        if (defaultPrefs.serviceEnabled()) {
            BirthdayService.start(context);
        }
    }
}
