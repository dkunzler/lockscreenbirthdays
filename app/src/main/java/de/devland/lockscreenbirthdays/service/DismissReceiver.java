package de.devland.lockscreenbirthdays.service;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.joda.time.DateTime;

import java.util.HashMap;

import de.devland.esperandro.Esperandro;
import de.devland.lockscreenbirthdays.prefs.ActionPrefs;

/**
 * Created by deekay on 28.06.2015.
 */
public class DismissReceiver extends BroadcastReceiver {

    public static final String EXTRA_CONTACT_ID = "de.devland.lockscreenbirthdays.service.DismissReceiver.contact";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.hasExtra(EXTRA_CONTACT_ID)) {
            int contactId = intent.getIntExtra(EXTRA_CONTACT_ID, -1);
            Log.d("DismissReceiver", "Dismissing contact '" + contactId + "'");
            ActionPrefs actionPrefs = Esperandro.getPreferences(ActionPrefs.class, context);
            HashMap<Integer, Long> dismissedBirthdays = actionPrefs.dismissedBirthdays();
            if (dismissedBirthdays == null) {
                dismissedBirthdays = new HashMap<>(1);
            }
            dismissedBirthdays.put(contactId, new DateTime().withTimeAtStartOfDay().getMillis());
            actionPrefs.dismissedBirthdays(dismissedBirthdays);
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(contactId);
        } else {
            BirthdayService.lastNotificationUpdate = null;
        }
    }
}
