package com.example.cometimplementation.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.core.UsersRequest;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.User;
import com.example.cometimplementation.R;
import com.example.cometimplementation.fragments.RecentChatFragment;
import com.example.cometimplementation.models.UserPojo;

import java.util.ArrayList;
import java.util.List;

public class ContactImportingAndProcessingActivity extends AppCompatActivity {
    List<UserPojo> userPojos;
    List<UserPojo> recycler = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_importing_and_processing);
        getSupportActionBar().setTitle("Import Contacts");
        checkPermissions();
        
    }
    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, 100);

        } else {
            getContactList();
            fetchUsers();
        }

    }

    private void getContactList() {
        userPojos = new ArrayList<>();
        Uri uri = ContactsContract.Contacts.CONTENT_URI;
        String sort = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC";

        Cursor cursor = this.getContentResolver().query(uri, null, null, null, sort);

        if (cursor.getCount() > 0) {

            while (cursor.moveToNext()) {
                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                Uri uriPhone = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
                String selection = ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " =?";
                Cursor phoneCursor = this.getContentResolver().query(uriPhone, null, selection, new String[]{id}, null);
                if (phoneCursor.moveToNext()) {
                    String number = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    number = number.replaceAll("()-", "");
                    userPojos.add(new UserPojo(name, number));
                    phoneCursor.close();
                }
            }
            cursor.close();
        }

    }

    private void fetchUsers() {
        UsersRequest usersRequest = new UsersRequest.UsersRequestBuilder().setLimit(30).build();

        usersRequest.fetchNext(new CometChat.CallbackListener<List<User>>() {
            @Override
            public void onSuccess(List<User> list) {
                Log.d("check", "User list received: " + list.size());
                checkIfUserExistInPhoneBook(list);
            }

            @Override
            public void onError(CometChatException e) {
                Log.d("check", "User list fetching failed with exception: " + e.getMessage());
            }
        });
    }
    @SuppressLint("NotifyDataSetChanged")
    private void checkIfUserExistInPhoneBook(List<User> list) {
        Log.d("check", "if its present" + list.toString());
        recycler.clear();
        if (userPojos.size() > 0) {
            for (int i = 0; i < userPojos.size(); i++) {
                for (int j = 0; j < list.size(); j++) {
                    if (list.get(j).getUid().equals(userPojos.get(i).getNumber())) {
                        recycler.add(new UserPojo(userPojos.get(i).getName(), userPojos.get(i).getNumber(), list.get(j).getAvatar()));
                    }
                }

            }
            if (recycler.size() == 0) {

            } else {
            }
        } else {
            Log.d("check", "\n\n" + "You Don't have Any Contact To Look For");

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 100 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {


        } else {
            Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
        }
    }
}