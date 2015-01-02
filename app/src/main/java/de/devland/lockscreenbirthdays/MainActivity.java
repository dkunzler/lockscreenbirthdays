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
        Cursor cursor = getContactsBirthdays();
        int bDayColumn = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Event.START_DATE);
        int nameColumn = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
        int photoColumn = cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_URI);
        int idColumn = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Event.CONTACT_ID);
        List<Date> birthdaysInRange = new ArrayList<>();
        int count = 0;
        while (cursor.moveToNext()) {
            String bDay = cursor.getString(bDayColumn);
            String name = cursor.getString(nameColumn);
            String photoUri = cursor.getString(photoColumn);
            int id = cursor.getInt(idColumn);
            Log.d("BLUB", "Birthday: " + bDay);
            Notification.Builder notificationBuilder = new Notification.Builder(getApplicationContext());
            notificationBuilder.setLargeIcon(getContactBitmapFromURI(photoUri))
                               .setContentTitle(name)
                               .setContentText("Test")
                               .setPriority(Notification.PRIORITY_MAX)
                               .setShowWhen(false)
                               .setSmallIcon(R.drawable.ic_stat_birthday);
            Notification notif = notificationBuilder.build();
            // TODO open contact on click
            // TODO text
            notificationManager.notify(id, notif);
        }
    }

    private Bitmap getContactBitmapFromURI(String uri) {
        if (uri != null) {
            InputStream input = null;
            try {
                input = getContentResolver().openInputStream(Uri.parse(uri));
            } catch (FileNotFoundException e) {
                return null;
            }
            if (input == null) {
                return null;
            }
            return BitmapFactory.decodeStream(input);
        }
        return null;
    }

    private Cursor getContactsBirthdays() {
        Uri uri = ContactsContract.Data.CONTENT_URI;

        String[] projection = new String[] {
                ContactsContract.Contacts.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Event.CONTACT_ID,
                ContactsContract.CommonDataKinds.Event.START_DATE,
                ContactsContract.Contacts.PHOTO_URI
        };

        String where =
                ContactsContract.Data.MIMETYPE + "= ? AND " +
                        ContactsContract.CommonDataKinds.Event.TYPE + "=" +
                        ContactsContract.CommonDataKinds.Event.TYPE_BIRTHDAY;
        String[] selectionArgs = new String[] {
                ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE
        };
        String sortOrder = null;
        return managedQuery(uri, projection, where, selectionArgs, sortOrder);
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
