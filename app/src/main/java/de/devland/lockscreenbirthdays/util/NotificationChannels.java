package de.devland.lockscreenbirthdays.util;

import android.annotation.TargetApi;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import de.devland.lockscreenbirthdays.service.BirthdayService;
import de.devland.lockscreenbirthdays.R;

public class NotificationChannels {

    @TargetApi(Build.VERSION_CODES.O)
    public void init(Context context) {
        NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
        initBirthdaysChannel(context, notificationManager);
        initServiceChannel(context, notificationManager);
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void initBirthdaysChannel(Context context, NotificationManager notificationManager) {
        // The user-visible name of the channel.
        CharSequence name = context.getString(R.string.channel_birthdays_name);

        // The user-visible description of the channel.
        String description = context.getString(R.string.channel_birthdays_description);

        int importance = NotificationManager.IMPORTANCE_HIGH;

        NotificationChannel channel = new NotificationChannel(BirthdayService.CHANNEL_BIRTHDAYS, name, importance);

        // Configure the notification channel.
        channel.setDescription(description);
        channel.enableLights(false);
        channel.enableVibration(false);
        channel.setBypassDnd(true);
        channel.setSound(null, null);

        notificationManager.createNotificationChannel(channel);
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void initServiceChannel(Context context, NotificationManager notificationManager) {
        // The user-visible name of the channel.
        CharSequence name = context.getString(R.string.channel_service_name);

        // The user-visible description of the channel.
        String description = context.getString(R.string.channel_service_description);

        int importance = NotificationManager.IMPORTANCE_LOW;

        NotificationChannel channel = new NotificationChannel(BirthdayService.CHANNEL_SERVICE, name, importance);

        // Configure the notification channel.
        channel.setDescription(description);
        channel.enableLights(false);
        channel.enableVibration(false);
        channel.setBypassDnd(true);
        channel.setSound(null, null);

        notificationManager.createNotificationChannel(channel);
    }
}
