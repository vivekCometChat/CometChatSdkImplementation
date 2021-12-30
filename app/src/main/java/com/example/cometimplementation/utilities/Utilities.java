package com.example.cometimplementation.utilities;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.Toast;

import com.cometchat.pro.models.User;
import com.example.cometimplementation.models.UserPojo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utilities {

    public static boolean isNumberValid(String number) {
        Pattern p = Pattern.compile("^\\d{10}$");
        Matcher m = p.matcher(number);
        return (m.matches());
    }

    public static void getContacts(Context context) {

        List<UserPojo> userPojos = new ArrayList<>();
        Uri uri = ContactsContract.Contacts.CONTENT_URI;
        String sort = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC";

        Cursor cursor = context.getContentResolver().query(uri, null, null, null, sort);

        if (cursor.getCount() > 0) {

            while (cursor.moveToNext()) {
                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                Uri uriPhone = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
                String selection = ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " =?";
                Cursor phoneCursor = context.getContentResolver().query(uriPhone, null, selection, new String[]{id}, null);
                if (phoneCursor.moveToNext()) {
                    String number = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    Log.d("see_numbers", "getContacts1: " + number);
                    number = number.replaceAll("[+ ()-]", "");
                    if (number.length() == 12 && number.startsWith("91")) {
                        number = number.substring(2);
                    }
                    Log.d("see_numbers", "getContacts2: " + number);

                    userPojos.add(new UserPojo(name, number));
                    phoneCursor.close();
                }
            }
            cursor.close();
            SharedPrefData.saveContacts(context, userPojos);

        }

    }

    public static void filterUsers(Context context, List<User> users, List<UserPojo> contactList) {
        if (users != null && users.size() > 0 && contactList != null && contactList.size() > 0) {
            List<UserPojo> userPojos = new ArrayList<>();

            for (int i = 0; i < contactList.size(); i++) {

                Log.d("check_numbers", "filterUsers: " + contactList.get(i).getNumber());
                for (int j = 0; j < users.size(); j++) {
                    if (users.get(j).getUid().equals(contactList.get(i).getNumber())) {
                        userPojos.add(new UserPojo(contactList.get(i).getName(), contactList.get(i).getNumber(), users.get(j).getAvatar()));
                    }
                }

            }
            if (userPojos.size() > 0) {
                SharedPrefData.saveCommonUser(context, userPojos);
            }
        } else {
            Toast.makeText(context, "can't filter users", Toast.LENGTH_SHORT).show();
        }

    }

    public static String convertMillisToTime(long deliveredAt) {

        SimpleDateFormat myFormat = new SimpleDateFormat("h:mm a");
        myFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return myFormat.format(deliveredAt);



    }

}
