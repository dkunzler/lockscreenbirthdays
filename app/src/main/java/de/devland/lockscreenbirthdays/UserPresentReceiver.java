package de.devland.lockscreenbirthdays;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import de.devland.esperandro.Esperandro;
import de.devland.lockscreenbirthdays.prefs.DefaultPrefs;

public class UserPresentReceiver extends BroadcastReceiver {
    public UserPresentReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();

        // if the service was stopped due to memory problems, restart it
        DefaultPrefs defaultPrefs = Esperandro.getPreferences(DefaultPrefs.class, context);
        if (defaultPrefs.serviceEnabled() && !BirthdayService.isRunning) {
            Intent service = new Intent(context, BirthdayService.class);
            context.startService(service);
        }
    }
}
