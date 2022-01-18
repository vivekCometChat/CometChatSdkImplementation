package com.example.cometimplementation.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.core.UsersRequest;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.User;
import com.example.cometimplementation.Interfaces.FetchUserCallBack;
import com.example.cometimplementation.R;
import com.example.cometimplementation.fragments.RecentChatFragment;
import com.example.cometimplementation.models.UserPojo;
import com.example.cometimplementation.utilities.ApiCalls;
import com.example.cometimplementation.utilities.SharedPrefData;
import com.example.cometimplementation.utilities.Utilities;

import java.util.ArrayList;
import java.util.List;

public class ContactImportingAndProcessingActivity extends AppCompatActivity implements FetchUserCallBack {
    private ProgressBar progress;
    private TextView progress_message;
    private Button go_next, req_per;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        setContentView(R.layout.activity_contact_importing_and_processing);

        initViews();
//        checkPermissions();

    }

    private void initViews() {
        getSupportActionBar().setTitle("Import Contacts");
        progress = findViewById(R.id.progress);
        progress_message = findViewById(R.id.progress_message);
        go_next = findViewById(R.id.go_next);
        req_per = findViewById(R.id.req_per);
    }

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, 100);

        } else {
            getContacts();
        }

    }

    public void getContacts() {
        Utilities.getContacts(this);
        if (SharedPrefData.getSaveContacts(this) != null) {
            progress_message.setText("Contacts Imported");
            progress.setProgress(50);
        } else {
            progress.setProgress(0);
            progress_message.setText("Can't able to Store Contacts");
        }
        UsersRequest usersRequest = new UsersRequest.UsersRequestBuilder().setLimit(30).build();
        ApiCalls.fetchCometChatUsers(this, usersRequest,this);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 100 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            progress_message.setText("Permission Granted");
            getContacts();
            progress_message.setTextColor(R.color.app_theme);
            go_next.setVisibility(View.VISIBLE);
            progress.setVisibility(View.VISIBLE);
            req_per.setVisibility(View.GONE);
        } else {
            progress_message.setText("Permission denied");
            progress_message.setTextColor(R.color.catalyst_redbox_background);
            go_next.setVisibility(View.GONE);
            progress.setVisibility(View.GONE);
            req_per.setVisibility(View.VISIBLE);
            Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkPermissions();

    }

    public void gotoHome(View view) {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    @Override
    public void onSuccess(List<User> list) {

        if (list != null && list.size() > 0 && SharedPrefData.getSaveContacts(this) != null && SharedPrefData.getSaveContacts(this).size() > 0) {
            Utilities.filterUsers(this, list, SharedPrefData.getSaveContacts(this));

            if (SharedPrefData.getCommonUser(this) != null && SharedPrefData.getCommonUser(this).size() > 0) {
                progress.setProgress(100);
                progress_message.setText("All set up! Enjoy Chatting");
                go_next.setEnabled(true);

            }else{
                progress_message.setText("We didn't able to find common user from your phone book and CometChat, Please invite your friends!");

            }
        } else {
            progress.setProgress(0);
            progress_message.setText("Either you dont have contacts or you have no Comet chat users");
            Toast.makeText(this, "Either you dont have contacts or you have no Comet chat users", Toast.LENGTH_SHORT).show();
        }


    }

    @Override
    public void onError(CometChatException e) {

        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();

    }
}