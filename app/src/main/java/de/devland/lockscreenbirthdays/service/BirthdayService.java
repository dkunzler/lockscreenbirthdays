package de.devland.lockscreenbirthdays.service;

import android.app.*;
import android.content.*;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.provider.ContactsContract;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import de.devland.esperandro.Esperandro;
import de.devland.lockscreenbirthdays.App;
import de.devland.lockscreenbirthdays.R;
import de.devland.lockscreenbirthdays.ServiceInfoActivity;
import de.devland.lockscreenbirthdays.model.Contact;
import de.devland.lockscreenbirthdays.prefs.ActionPrefs;
import de.devland.lockscreenbirthdays.prefs.DefaultPrefs;
import de.devland.lockscreenbirthdays.util.Icon;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import java.util.*;

public class BirthdayService extends Service {
    public static final String CHANNEL_BIRTHDAYS = "channelBirthdays";
    public static final String CHANNEL_SERVICE = "channelService";

    private NotificationManager notificationManager;
    private KeyguardManager keyguardManager;
    private PowerManager powerManager;

    private DefaultPrefs defaultPrefs;
    private ActionPrefs actionPrefs;

    private UserPresentReceiver userPresentReceiver = new UserPresentReceiver();

    public static volatile boolean isRunning = false;

    private List<Contact> allContactsWithBirthdays;
    private List<Contact> birthdaysInRange;

    private DateTime lastNotificationUpdate = new DateTime();

    public static void start(Context context) {
        Intent service = new Intent(context, BirthdayService.class);
//        context.startService(service);
        ContextCompat.startForegroundService(App.getInstance(), service);
    }

    public static void stop(Context context) {
        Intent service = new Intent(context, BirthdayService.class);
        context.stopService(service);
    }

    @Override
    public void onCreate() {
        defaultPrefs = Esperandro.getPreferences(DefaultPrefs.class,
                getApplicationContext());
        actionPrefs = Esperandro.getPreferences(ActionPrefs.class,
                getApplicationContext());
        notificationManager = (NotificationManager) getSystemService(
                Context.NOTIFICATION_SERVICE);
        keyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Icon icon = Icon.valueOf(defaultPrefs.icon());
            NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_SERVICE);
            builder.setContentTitle(getString(R.string.app_name))
                    .setSmallIcon(icon.getNotificationIconId())
                    .setContentIntent(PendingIntent.getActivity(getApplicationContext(), -2,
                            new Intent(getApplicationContext(), ServiceInfoActivity.class), PendingIntent.FLAG_UPDATE_CURRENT));

            startForeground(Integer.MAX_VALUE, builder.build());
        }
        if (!isRunning) {
            synchronized (BirthdayService.class) {
                if (!isRunning) {
                    isRunning = true;

                    registerReceiver(screenOffReceiver, new IntentFilter(Intent.ACTION_SCREEN_OFF));
                    registerReceiver(screenOnReceiver, new IntentFilter(Intent.ACTION_SCREEN_ON));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        registerReceiver(userPresentReceiver, new IntentFilter(Intent.ACTION_USER_PRESENT));
                    }
                    getContentResolver().registerContentObserver(
                            ContactsContract.Contacts.CONTENT_URI, true, contactObserver);
                    defaultPrefs.registerOnChangeListener(settingsChangeListener);

                }
            }
        }

        updateBirthdays(true);
        if (!powerManager.isInteractive()) {
            updateNotifications();
        }

        return START_STICKY;
    }

    private void updateBirthdays(boolean reload) {
        if (reload || allContactsWithBirthdays == null) {
            this.allContactsWithBirthdays = Contact.getAllContactsWithBirthdays(
                    getApplicationContext());
        }
        this.birthdaysInRange = new ArrayList<>();
        for (Contact contact : allContactsWithBirthdays) {
            if (contact.daysTillBirthday() <= Integer.parseInt(defaultPrefs.maxDaysTillBirthday())) {
                HashMap<Integer, Long> dismissedBirthdays = actionPrefs.dismissedBirthdays();
                boolean dismiss = false;
                if (dismissedBirthdays != null) {
                    if (dismissedBirthdays.containsKey(contact.getId())) {
                        DateTime dismissedAt = new DateTime(dismissedBirthdays.get(contact.getId()));
                        DateTime today = DateTime.now().withTimeAtStartOfDay();
                        if (dismissedAt.isBefore(today)) {
                            dismissedBirthdays.remove(contact.getId());
                            actionPrefs.dismissedBirthdays(dismissedBirthdays);
                        } else {
                            dismiss = dismissedAt.isEqual(today);
                        }
                    }
                }
                if (!dismiss) {
                    birthdaysInRange.add(contact);
                }
            }
        }
        Collections.sort(birthdaysInRange);
        Collections.reverse(birthdaysInRange);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(screenOffReceiver);
        unregisterReceiver(screenOnReceiver);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            unregisterReceiver(userPresentReceiver);
        }
        getContentResolver().unregisterContentObserver(contactObserver);
        defaultPrefs.unregisterOnChangeListener(settingsChangeListener);
        isRunning = false;
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private ContentObserver contactObserver = new ContentObserver(new Handler()) {
        @Override
        public void onChange(boolean selfChange, Uri uri) {
            super.onChange(selfChange, uri);
            start(getApplicationContext());
        }
    };

    private BroadcastReceiver screenOnReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (!keyguardManager.inKeyguardRestrictedInputMode()) {
                int keepAfterLogin = Integer.parseInt(defaultPrefs.keepAfterLogin());
                if (keepAfterLogin > 0) {
                    PendingIntent removeNotification = PendingIntent.getBroadcast(context, -1,
                            new Intent(context, RemoveNotificationReceiver.class), PendingIntent.FLAG_UPDATE_CURRENT);

                    AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                    alarmManager.setExact(AlarmManager.RTC, new Date().getTime() + keepAfterLogin * 1000, removeNotification);
                } else {
                    notificationManager.cancelAll();
                }
            } else {
                if (lastNotificationUpdate.isBefore(new LocalDate().toDateTimeAtStartOfDay())) {
                    start(getApplicationContext());
                }
            }
        }
    };

    private BroadcastReceiver screenOffReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            start(getApplicationContext());
        }
    };

    private SharedPreferences.OnSharedPreferenceChangeListener settingsChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (key.equals("maxDaysTillBirthday")) {
                updateBirthdays(true);
            }
        }
    };

    private void updateNotifications() {
        updateBirthdays(false);
        lastNotificationUpdate = new DateTime();
        notificationManager.cancelAll();
        for (Contact contact : birthdaysInRange) {
            Intent contactIntent = new Intent(Intent.ACTION_VIEW);
            Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI,
                    String.valueOf(contact.getId()));
            contactIntent.setData(uri);
            PendingIntent mainAction = PendingIntent.getActivity(getApplicationContext(),
                    contact.getId(), contactIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            Intent dismissIntent = new Intent(getApplicationContext(), DismissReceiver.class);
            dismissIntent.putExtra(DismissReceiver.EXTRA_CONTACT_ID, contact.getId());
            PendingIntent dismissAction = PendingIntent.getBroadcast(getApplicationContext(),
                    contact.getId(), dismissIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(
                    getApplicationContext(), CHANNEL_BIRTHDAYS);
            Icon icon = Icon.valueOf(defaultPrefs.icon());


            notificationBuilder.setLargeIcon(contact.getContactBitmap(getApplicationContext()))
                    .setContentTitle(contact.getDisplayName())
                    .setContentText(contact.getMessageText(getApplicationContext()))
                    .setPriority(Notification.PRIORITY_MAX)
                    .setShowWhen(false)
                    .setContentIntent(mainAction)
                    .setChannelId(CHANNEL_BIRTHDAYS)
                    .addAction(R.drawable.ic_action_done, getBaseContext().getString(R.string.action_dismiss), dismissAction)
                    .setSmallIcon(icon.getNotificationIconId());
            Notification notif = notificationBuilder.build();
            notificationManager.notify(contact.getId(), notif);
        }
    }

}
