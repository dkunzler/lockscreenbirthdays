package de.devland.lockscreenbirthdays;

import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.provider.ContactsContract;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.devland.esperandro.Esperandro;
import de.devland.lockscreenbirthdays.model.Contact;
import de.devland.lockscreenbirthdays.prefs.DefaultPrefs;

public class BirthdayService extends Service {

    private NotificationManager notificationManager;
    private KeyguardManager keyguardManager;

    private DefaultPrefs defaultPrefs;

    public static volatile boolean isRunning = false;

    private List<Contact> allContactsWithBirthdays;
    private List<Contact> birthdaysInRange;

    private DateTime lastNotificationUpdate = new DateTime();


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!isRunning) {
            synchronized (BirthdayService.class) {
                if (!isRunning) {
                    isRunning = true;
                    notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    keyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
                    defaultPrefs = Esperandro.getPreferences(DefaultPrefs.class, getApplicationContext());

                    registerReceiver(screenOffReceiver, new IntentFilter(Intent.ACTION_SCREEN_OFF));
                    registerReceiver(screenOnReceiver, new IntentFilter(Intent.ACTION_SCREEN_ON));
                    getContentResolver().registerContentObserver(ContactsContract.Contacts.CONTENT_URI, true, contactObserver);

                    updateBirthdays(true);
                }
            }
        }

        

        // TODO contentobserver
        return START_STICKY;
    }

    private void updateBirthdays(boolean reload) {
        if (reload || allContactsWithBirthdays == null) {
            this.allContactsWithBirthdays = Contact.getAllContactsWithBirthdays(
                    getApplicationContext());
        }
        this.birthdaysInRange = new ArrayList<>();
        for (Contact contact : allContactsWithBirthdays) {
            if (contact.daysTillBirthday() < Integer.parseInt(defaultPrefs.maxDaysTillBirthday())) {
                birthdaysInRange.add(contact);
            }
        }
        Collections.sort(birthdaysInRange);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(screenOffReceiver);
        unregisterReceiver(screenOnReceiver);
        getContentResolver().unregisterContentObserver(contactObserver);
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private ContentObserver contactObserver = new ContentObserver(new Handler()) {
        @Override
        public void onChange(boolean selfChange, Uri uri) {
            super.onChange(selfChange, uri);
            updateBirthdays(true);
        }
    };

    private BroadcastReceiver screenOnReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (!keyguardManager.inKeyguardRestrictedInputMode()) {
                notificationManager.cancelAll();
            }
        }
    };

    private BroadcastReceiver screenOffReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateNotifications();
        }
    };

    private void updateNotifications() {
        lastNotificationUpdate = new DateTime();
        for (Contact contact : birthdaysInRange) {
            Intent contactIntent = new Intent(Intent.ACTION_VIEW);
            Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, String.valueOf(contact.getId()));
            contactIntent.setData(uri);
            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), contact.getId(), contactIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            Notification.Builder notificationBuilder = new Notification.Builder(getApplicationContext());
            notificationBuilder.setLargeIcon(contact.getContactBitmap(getApplicationContext()))
                    .setContentTitle(contact.getDisplayName())
                    .setContentText(contact.getMessageText(getApplicationContext()))
                    .setPriority(Notification.PRIORITY_MAX)
                    .setShowWhen(false)
                    .setContentIntent(pendingIntent)
                    .setSmallIcon(R.drawable.ic_stat_birthday);
            Notification notif = notificationBuilder.build();
            // TODO update at 00:00
            // TODO order
            notificationManager.notify(contact.getId(), notif);
        }
    }

}
