package com.example.cometimplementation.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.cometchat.pro.core.Call;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.User;
import com.example.cometimplementation.Interfaces.FetchUserCallBack;
import com.example.cometimplementation.Interfaces.Listeners;
import com.example.cometimplementation.R;
import com.example.cometimplementation.adapter.ContactRecycler;
import com.example.cometimplementation.models.UserPojo;
import com.example.cometimplementation.utilities.ApiCalls;
import com.example.cometimplementation.utilities.SharedPrefData;
import com.example.cometimplementation.utilities.Utilities;

import java.util.ArrayList;
import java.util.List;

public class ContactsActivity extends AppCompatActivity implements Listeners, FetchUserCallBack {
    private RecyclerView contact_recycler;
    private TextView message;
    ProgressDialog progressDialog;
    ContactRecycler contactRecycler;
    List<UserPojo> userPojos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        setContentView(R.layout.activity_contacts);
        initViews();
    }

    private void initViews() {
        getSupportActionBar().setTitle("Contacts");
        contact_recycler = findViewById(R.id.contact_recycler);
        message = findViewById(R.id.message);
        message.setVisibility(View.GONE);
        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); // Progress Dialog Style Spinner
        progressDialog.setMessage("Syncing...");
        progressDialog.setCancelable(false);
        progressDialog.getWindow().setGravity(Gravity.CENTER);
        setContactRecycler();

    }

    private void setContactRecycler() {
        contact_recycler.setLayoutManager(new LinearLayoutManager(this));
        contactRecycler = new ContactRecycler(this, this, userPojos);
        contact_recycler.setAdapter(contactRecycler);
        if (SharedPrefData.getSaveContacts(this) != null) {
            userPojos.addAll(SharedPrefData.getSaveContacts(this));
            contactRecycler.notifyDataSetChanged();
        } else {
            contact_recycler.setVisibility(View.GONE);
            message.setVisibility(View.VISIBLE);
            message.setText("No Contact Found");
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.action_bar_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.sync) {
            progressDialog.show();
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            checkPermissions();
        }

        return super.onOptionsItemSelected(item);
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
        ApiCalls.fetchCometChatUsers(this, this);
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 100 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getContacts();

        } else {
            progressDialog.dismiss();
            Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void addedNewUser(User user) {

    }

    @Override
    public void receiveCall(Call call) {

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
    public void onSuccess(List<User> list) {
        if (list != null && list.size() > 0 && SharedPrefData.getSaveContacts(this) != null && SharedPrefData.getSaveContacts(this).size() > 0) {
            Utilities.filterUsers(this, list, SharedPrefData.getSaveContacts(this));

            if (SharedPrefData.getCommonUser(this) != null && SharedPrefData.getCommonUser(this).size() > 0) {
                userPojos.clear();
                userPojos.addAll(SharedPrefData.getSaveContacts(this));
                contactRecycler.notifyDataSetChanged();

            }

        }
        progressDialog.dismiss();

    }

    @Override
    public void onError(CometChatException e) {
        progressDialog.dismiss();
    }


    public void showDialog(){

    }


}