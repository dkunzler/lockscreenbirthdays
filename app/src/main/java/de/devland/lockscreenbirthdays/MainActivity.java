package de.devland.lockscreenbirthdays;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.devland.lockscreenbirthdays.model.Contact;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                                .add(R.id.container, new PlaceholderFragment())
                                .commit();
        }

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // iterate through all Contact's Birthdays and print in log
        List<Contact> allContactsWithBirthdays = Contact.getAllContactsWithBirthdays(getApplicationContext());

        List<Contact> birthdaysInRange = new ArrayList<>();
        for (Contact contact : allContactsWithBirthdays) {

            Log.d("Tag", contact.getBirthday());

            if (contact.daysTillBirthday() < 30) {
                Notification.Builder notificationBuilder = new Notification.Builder(getApplicationContext());
                notificationBuilder.setLargeIcon(contact.getContactBitmap(getApplicationContext()))
                        .setContentTitle(contact.getDisplayName())
                        .setContentText(contact.getMessageText(getApplicationContext()))
                        .setPriority(Notification.PRIORITY_MAX)
                        .setShowWhen(false)
                        .setSmallIcon(R.drawable.ic_stat_birthday);
                Notification notif = notificationBuilder.build();
                // TODO open contact on click
                // TODO service
                // TODO BootReceiver
                // TODO settings
                // TODO update at 00:00
                // TODO order
                notificationManager.notify(contact.getId(), notif);
            }
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }
    }
}
