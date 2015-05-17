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
import java.util.ArrayList;
import java.util.List;

import de.devland.lockscreenbirthdays.R;
import lombok.Getter;

/**
 * Created by David Kunzler on 02.01.2015.
 */
@Getter
public class Contact implements Comparable<Contact> {
    private int id;

    private String displayName;
    private String photoUri;

    private String birthday;
    private Birthday birthdayObject;

    private Bitmap contactPhoto;

    public void setBirthday(String birthday) {
        this.birthday = birthday;
        this.birthdayObject = Birthday.fromString(birthday);
    }

    public static Contact fromCursor(Cursor cursor) {
        int bDayColumn = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Event.START_DATE);
        int nameColumn = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
        int photoColumn = cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_URI);
        int idColumn = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Event.CONTACT_ID);

        Contact contact = new Contact();
        contact.id = cursor.getInt(idColumn);
        contact.setBirthday(cursor.getString(bDayColumn));
        contact.displayName = cursor.getString(nameColumn);
        contact.photoUri = cursor.getString(photoColumn);

        if (contact.getBirthdayObject().isCorrectBirthday()) {
            return contact;
        } else {
            return null;
        }
    }

    public static List<Contact> getAllContactsWithBirthdays(Context context) {
        Uri uri = ContactsContract.Data.CONTENT_URI;
        List<Contact> contacts = new ArrayList<>();

        String[] projection = new String[]{
                ContactsContract.Contacts.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Event.CONTACT_ID,
                ContactsContract.CommonDataKinds.Event.START_DATE,
                ContactsContract.Contacts.PHOTO_URI
        };

        String where =
                ContactsContract.Data.MIMETYPE + "= ? AND " +
                        ContactsContract.CommonDataKinds.Event.TYPE + "=" +
                        ContactsContract.CommonDataKinds.Event.TYPE_BIRTHDAY;
        String[] selectionArgs = new String[]{
                ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE
        };
        String sortOrder = null;
        Cursor cursor = context.getContentResolver().query(uri, projection, where, selectionArgs, sortOrder);
        while (cursor.moveToNext()) {
            Contact contact = Contact.fromCursor(cursor);
            if (contact != null) {
                contacts.add(contact);
            }
        }
        cursor.close();

        return contacts;
    }

    public int daysTillBirthday() {
        LocalDate now = LocalDate.now();
        LocalDate then = getNextBirthday();

        return Days.daysBetween(now, then).getDays();
    }

    private LocalDate getNextBirthday() {
        LocalDate now = LocalDate.now();

        Birthday birthday = getBirthdayObject();
        LocalDate potentialBirthday = new LocalDate(now.getYear(), birthday.getMonth(), birthday.getDay());
        if (now.isAfter(potentialBirthday)) {
            potentialBirthday = potentialBirthday.plusYears(1);
        }
        return potentialBirthday;
    }


    public int getNewAge() {
        Birthday birthday = getBirthdayObject();
        int newAge = -1;
        if (birthday.hasYear()) {
            LocalDate nextBirthday = getNextBirthday();
            LocalDate originalBirthday = birthday.toLocalDate();
            newAge = Years.yearsBetween(originalBirthday, nextBirthday).getYears();
        }
        return newAge;
    }

    public Bitmap getContactBitmap(Context context) {
        Bitmap photo = contactPhoto;
        try {
            if (photo == null) {
                if (photoUri != null) {
                    InputStream input = context.getContentResolver().openInputStream(Uri.parse(photoUri));
                    if (input != null) {
                        photo = BitmapFactory.decodeStream(input);
                    }
                }
            }
        } finally {
            contactPhoto = photo;
            return photo;
        }
    }

    public String getMessageText(Context context) {
        Birthday birthday = getBirthdayObject();
        int daysTillBirthday = daysTillBirthday();
        String message;
        if (birthday.hasYear()) {
            if (daysTillBirthday == 0) {
                message = context.getString(R.string.birthday_todayAge);
                message = String.format(message, getDisplayName(), getNewAge());
            } else if (daysTillBirthday == 1) {
                message = context.getString(R.string.birthday_tomorrowAge);
                message = String.format(message, getDisplayName(), getNewAge());
            } else {
                message = context.getString(R.string.birthday_daysAge);
                message = String.format(message, getDisplayName(), getNewAge(), daysTillBirthday);
            }
        } else {
            if (daysTillBirthday == 0) {
                message = context.getString(R.string.birthday_today);
                message = String.format(message, getDisplayName());
            } else if (daysTillBirthday == 1) {
                message = context.getString(R.string.birthday_tomorrow);
                message = String.format(message, getDisplayName());
            } else {
                message = context.getString(R.string.birthday_days);
                message = String.format(message, getDisplayName(), daysTillBirthday);
            }
        }
        return message;
    }

    @Override
    public int compareTo(Contact another) {
        return Integer.compare(this.daysTillBirthday(), another.daysTillBirthday());
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

        public boolean isCorrectBirthday() {
            return day != 0 && month != 0;
        }

        public LocalDate toLocalDate() {
            LocalDate date = null;
            if (hasYear()) {
                date = new LocalDate(year, month, day);
            }
            return date;
        }
    }

}
