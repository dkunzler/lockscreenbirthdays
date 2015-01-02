package de.devland.lockscreenbirthdays.model;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;

import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.Years;

import java.io.InputStream;

import lombok.Getter;

/**
 * Created by David Kunzler on 02.01.2015.
 */
@Getter
public class Contact {
    private int id;

    private String displayName;
    private String photoUri;

    private String birthday;

    public static Contact fromCursor(Cursor cursor) {
        int bDayColumn = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Event.START_DATE);
        int nameColumn = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
        int photoColumn = cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_URI);
        int idColumn = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Event.CONTACT_ID);

        Contact contact = new Contact();
        contact.id = cursor.getInt(idColumn);
        contact.birthday = cursor.getString(bDayColumn);
        contact.displayName = cursor.getString(nameColumn);
        contact.photoUri = cursor.getString(photoColumn);

        return contact;
    }

    public static Cursor getAllContactsWithBirthdays(Context context) {
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
        return context.getContentResolver().query(uri, projection, where, selectionArgs, sortOrder);
    }

    public int daysTillBirthday() {
        LocalDate now = LocalDate.now();
        LocalDate then = getNextBirthday();

        return Days.daysBetween(now, then).getDays();
    }

    private LocalDate getNextBirthday() {
        LocalDate now = LocalDate.now();
        Birthday birthday = Birthday.fromString(this.birthday);
        LocalDate potentialBirthday = now.withDayOfMonth(birthday.getDay()).withMonthOfYear(birthday.getMonth());
        if (now.isAfter(potentialBirthday)) {
            potentialBirthday = potentialBirthday.plusYears(1);
        }
        return potentialBirthday;
    }


    public int getNewAge() {
        Birthday birthday = Birthday.fromString(this.birthday);
        int newAge = -1;
        if (birthday.hasYear()) {
            LocalDate nextBirthday = getNextBirthday();
            LocalDate originalBirthday = birthday.toLocalDate();
            newAge = Years.yearsBetween(originalBirthday, nextBirthday).getYears();
        }
        return newAge;
    }

    public Bitmap getContactBitmap(Context context) {
        Bitmap photo = null;
        try {
            if (photoUri != null) {
                InputStream input = context.getContentResolver().openInputStream(Uri.parse(photoUri));
                if (input != null) {
                    photo = BitmapFactory.decodeStream(input);
                }
            }
        } finally {
            return photo;
        }
    }

    @Getter
    public static class Birthday {
        private int year = -1;
        private int month;
        private int day;


        public static Birthday fromString(String birthdayString) {
            Birthday birthday = new Birthday();

            String[] results = birthdayString.split("-");
            if (results.length == 4) {
                birthday.day = Integer.parseInt(results[3]);
                birthday.month = Integer.parseInt(results[2]);
            } else if (results.length == 3) {
                birthday.day = Integer.parseInt(results[2]);
                birthday.month = Integer.parseInt(results[1]);
                birthday.year = Integer.parseInt(results[0]);
            }

            return birthday;
        }

        public boolean hasYear() {
            return year != -1;
        }

        public LocalDate toLocalDate() {
            LocalDate date = null;
            if (hasYear()) {
                date = LocalDate.now().withDayOfMonth(day).withMonthOfYear(month).withYear(year);
            }
            return date;
        }
    }

}
