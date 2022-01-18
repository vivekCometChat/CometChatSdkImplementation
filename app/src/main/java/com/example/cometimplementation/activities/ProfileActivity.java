package com.example.cometimplementation.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.models.TextMessage;
import com.cometchat.pro.models.User;
import com.example.cometimplementation.R;
import com.example.cometimplementation.utilities.ApiCalls;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {
    LinearLayout update_profile, see_online_users, block_users, logout, help_center, see_Friends;
    CircleImageView profile_img;
    TextView name, status;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = CometChat.getLoggedInUser();
        if (user != null) {
            initViews();
        } else {
            startActivity(new Intent(this, LoginActivity.class));
            finishAffinity();
        }

    }

    @Override
    protected void onStart() {
        setDataInToView();
        super.onStart();
    }

    private void setDataInToView() {
        user = CometChat.getLoggedInUser();
        Picasso.get().load(user.getAvatar()).into(profile_img);
        name.setText(user.getName());
        status.setText(user.getStatusMessage() == null ? "Hey There I am using CometChat" : user.getStatusMessage());

    }

    private void initViews() {
        setContentView(R.layout.activity_profile);
        getSupportActionBar().setTitle("Profile Settings");
        update_profile = findViewById(R.id.update_profile);
        see_online_users = findViewById(R.id.see_online_users);
        block_users = findViewById(R.id.block_users);
        help_center = findViewById(R.id.help_center);
        profile_img = findViewById(R.id.profile_img);
        name = findViewById(R.id.name);
        status = findViewById(R.id.status);
        logout = findViewById(R.id.logout);
        see_Friends = findViewById(R.id.see_Friends);

        update_profile.setOnClickListener(this);
        see_Friends.setOnClickListener(this);
        see_online_users.setOnClickListener(this);
        block_users.setOnClickListener(this);
        help_center.setOnClickListener(this);
        logout.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.update_profile:
                startActivity(new Intent(ProfileActivity.this, UpdateProfileActivity.class));
                break;
            case R.id.see_online_users:
                startActivity(new Intent(ProfileActivity.this, OnlineUsersActivity.class));
                break;
            case R.id.block_users:
                startActivity(new Intent(ProfileActivity.this, BlockedUserActivity.class));
                break;
            case R.id.logout:
                ApiCalls.logOutCurrentUser(ProfileActivity.this);
                break;
            case R.id.see_Friends:
                startActivity(new Intent(ProfileActivity.this, FriendListActivity.class));
                break;
        }

    }
}