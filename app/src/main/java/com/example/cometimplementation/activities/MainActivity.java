package com.example.cometimplementation.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import com.cometchat.pro.core.AppSettings;
import com.cometchat.pro.core.Call;
import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.core.UsersRequest;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.User;
import com.example.cometimplementation.ApiCalls;
import com.example.cometimplementation.AppConfig;
import com.example.cometimplementation.Interfaces.CallStatus;
import com.example.cometimplementation.Interfaces.Listeners;
import com.example.cometimplementation.R;
import com.example.cometimplementation.models.UserPojo;
import com.example.cometimplementation.adapter.ContactRecycler;
import com.example.cometimplementation.adapter.RecyclerAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements Listeners, CallStatus {

    List<UserPojo> userPojos = new ArrayList<>();
    private UsersRequest usersRequest = null;
    private int limit = 30;
    private RecyclerView recyclerView, contact_recycler;
    private TextView message;
    List<UserPojo> recycler = new ArrayList<>();
    RecyclerAdapter recyclerAdapter;
    ContactRecycler contactRecycler;
    Intent intent;
    private String listenerId = "123456";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.recyclerView);
        contact_recycler = findViewById(R.id.contact_recycler);
        message = findViewById(R.id.message);
        message.setVisibility(View.GONE);
        setRecyclerView();
        setContactRecycler();
        checkPermissions();

    }

    private void initCometChat() {

        AppSettings appSettings = new AppSettings.AppSettingsBuilder().subscribePresenceForAllUsers().setRegion(AppConfig.AppDetails.REGION).build();

        CometChat.init(this, AppConfig.AppDetails.APP_ID, appSettings, new CometChat.CallbackListener<String>() {
            @Override
            public void onSuccess(String successMessage) {
                Log.d("check", "Initialization completed successfully");

                loginToCometChat();

            }

            @Override
            public void onError(CometChatException e) {
                message.setVisibility(View.VISIBLE);
                message.setText(e.getMessage());
                recyclerView.setVisibility(View.GONE);
                Log.d("check", "Initialization failed with exception: " + e.getMessage());
            }
        });


    }

    private void setContactRecycler() {
        contact_recycler.setLayoutManager(new LinearLayoutManager(this));
        contactRecycler = new ContactRecycler(this, this, userPojos);
        contact_recycler.setAdapter(contactRecycler);

    }

    private void loginToCometChat() {

        CometChat.login(AppConfig.UID, AppConfig.AppDetails.AUTH_KEY, new CometChat.CallbackListener<User>() {
            @Override
            public void onSuccess(User user) {
                fetchUsers();
            }

            @Override
            public void onError(CometChatException e) {
                message.setVisibility(View.VISIBLE);
                message.setText(e.getMessage());
                recyclerView.setVisibility(View.GONE);
                Log.d("check", "loggin error" + e.getMessage());

            }
        });

    }

    private void setRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerAdapter = new RecyclerAdapter(this, recycler);
        recyclerView.setAdapter(recyclerAdapter);

    }

    private void fetchUsers() {
        usersRequest = new UsersRequest.UsersRequestBuilder().setLimit(limit).build();

        usersRequest.fetchNext(new CometChat.CallbackListener<List<User>>() {
            @Override
            public void onSuccess(List<User> list) {
                Log.d("check", "User list received: " + list.size());
                checkIfUserExistInPhoneBook(list);
            }

            @Override
            public void onError(CometChatException e) {
                message.setVisibility(View.VISIBLE);
                message.setText(e.getMessage());
                recyclerView.setVisibility(View.GONE);
                Log.d("check", "User list fetching failed with exception: " + e.getMessage());
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void checkIfUserExistInPhoneBook(List<User> list) {
        Log.d("check", "if its present" + list.toString());
        recycler.clear();
        if (userPojos.size() > 0) {
            recyclerView.setVisibility(View.VISIBLE);
            for (int i = 0; i < userPojos.size(); i++) {
                for (int j = 0; j < list.size(); j++) {
                    if (list.get(j).getUid().equals(userPojos.get(i).getNumber())) {
                        recycler.add(new UserPojo(userPojos.get(i).getName(), userPojos.get(i).getNumber(), list.get(j).getAvatar()));
                        Log.d("see_image", "checkIfUserExistInPhoneBook: " + list.get(j).getAvatar());
                    }
                }

            }
            if (recycler.size() == 0) {
                recyclerView.setVisibility(View.GONE);
                message.setVisibility(View.VISIBLE);
                message.setText("No User Found");

            } else {
                recyclerAdapter.notifyDataSetChanged();
            }
        } else {
            Log.d("check", "\n\n" + "You Don't have Any Contact To Look For");
            recyclerView.setVisibility(View.GONE);

        }

    }

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_CONTACTS}, 100);
        } else {
            getContactList();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        ApiCalls.callInformation(this, this);

    }

    private void ShowCallingAlertDialog(Call call) {

        Dialog dialog = new Dialog(this);
        View view = getLayoutInflater().inflate(R.layout.calling_request_alert_layout, null, false);
        dialog.setContentView(view);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        CircleImageView circleImageView = dialog.findViewById(R.id.image_view);

        FloatingActionButton reject = dialog.findViewById(R.id.reject);
        FloatingActionButton accept = dialog.findViewById(R.id.accept);
        Picasso.get().load(call.getSender().getAvatar()).into(circleImageView);
        accept.setOnClickListener(v -> {
            ApiCalls.acceptCall(this, this, call);
            dialog.dismiss();
        });

        reject.setOnClickListener(v -> {
            ApiCalls.rejectCall(this, this, call);
            dialog.dismiss();

        });
        dialog.show();

    }

    private void getContactList() {

        Uri uri = ContactsContract.Contacts.CONTENT_URI;
        String sort = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC";

        Cursor cursor = getContentResolver().query(uri, null, null, null, sort);

        if (cursor.getCount() > 0) {

            while (cursor.moveToNext()) {
                //contact id
                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));

                //contact name
                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                //initialize phone URI
                Uri uriPhone = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;

                //Initialize selection
                String selection = ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " =?";

                Cursor phoneCursor = getContentResolver().query(uriPhone, null, selection, new String[]{id}, null);

                //check condition
                if (phoneCursor.moveToNext()) {
                    String number = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    number = number.replaceAll("()-", "");
                    userPojos.add(new UserPojo(name, number));
                    Log.d("see_contacts", "name: " + name + "\nnumber" + number);
                    phoneCursor.close();

                }
            }
            cursor.close();
            Log.d("see_contacts", "total count" + userPojos.size());
            contactRecycler.notifyDataSetChanged();

            initCometChat();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            message.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            getContactList();
        } else {
            message.setVisibility(View.VISIBLE);
            message.setText("Permission Denied");
            recyclerView.setVisibility(View.GONE);
            Toast.makeText(MainActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void addedNewUser(User user) {
        fetchUsers();
    }

    @Override
    public void receiveCall(Call call) {
        ShowCallingAlertDialog(call);

    }

    @Override
    public void acceptedOutGoingCall(Call call) {

    }

    @Override
    public void rejectedOutGoingCall(Call call) {

    }

    @Override
    public void canceledOutGoingCall(Call call) {

    }

    @Override
    public void reject(Call call) {

    }

    @Override
    public void accept(Call call) {
        Intent intent = new Intent(this, CallingActivity.class);
        intent.putExtra("receiverUid", "");
        intent.putExtra("session_id",call.getSessionId());
        startActivity(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        CometChat.removeMessageListener(listenerId);

    }
}