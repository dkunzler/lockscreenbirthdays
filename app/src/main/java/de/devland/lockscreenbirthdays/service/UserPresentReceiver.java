package de.devland.lockscreenbirthdays.service;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.Date;

import android.support.v4.content.ContextCompat;
import de.devland.esperandro.Esperandro;
import de.devland.lockscreenbirthdays.prefs.DefaultPrefs;

public class UserPresentReceiver extends BroadcastReceiver {
    public UserPresentReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        DefaultPrefs defaultPrefs = Esperandro.getPreferences(DefaultPrefs.class, context);

        int keepAfterLogin = Integer.parseInt(defaultPrefs.keepAfterLogin());
        if (keepAfterLogin > 0) {
            PendingIntent removeNotification = PendingIntent.getBroadcast(context, 55, new Intent(context, RemoveNotificationReceiver.class), 0);

            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            alarmManager.setExact(AlarmManager.RTC, new Date().getTime() + keepAfterLogin * 1000, removeNotification);
        } else {
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancelAll();
            BirthdayService.lastNotificationUpdate = null;
        }

        // if the service was stopped due to memory problems, restart it
        if (defaultPrefs.serviceEnabled() && !BirthdayService.isRunning) {
            BirthdayService.start(context);
        }
    }
}
