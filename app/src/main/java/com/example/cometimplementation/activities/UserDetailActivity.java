package com.example.cometimplementation.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NavUtils;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cometchat.pro.constants.CometChatConstants;
import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.User;
import com.example.cometimplementation.Interfaces.BlockUnBlockUserCallBackListener;
import com.example.cometimplementation.Interfaces.UserListeners;
import com.example.cometimplementation.R;
import com.example.cometimplementation.utilities.ApiCalls;
import com.example.cometimplementation.utilities.Utilities;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.squareup.picasso.Picasso;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;

public class UserDetailActivity extends AppCompatActivity implements AppBarLayout.OnOffsetChangedListener, UserListeners, View.OnClickListener, BlockUnBlockUserCallBackListener {
    Toolbar toolbar;
    LinearLayout collapsing_layout;
    TextView status, user_name, user_number, active_status, toolbar_name, block_text, unblock_text, report_text;
    FloatingActionButton chat, toolbar_chat;
    LinearLayout block, report, unblock;
    ShapeableImageView user_avatar, toolbar_profile;
    String uid = "";
    private String listenerID = "UserDetailActivity";
    boolean isBlocked=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);
        initViews();


    }

    private void initViews() {
        uid = getIntent().getStringExtra("uid");
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appBarLayout);
        appBarLayout.addOnOffsetChangedListener(this);
        toolbar = findViewById(R.id.toolbar);
        block_text = findViewById(R.id.block_text);
        unblock_text = findViewById(R.id.unblock_text);
        report_text = findViewById(R.id.report_text);
        toolbar_name = findViewById(R.id.toolbar_name);
        toolbar_chat = findViewById(R.id.toolbar_chat);
        toolbar_profile = findViewById(R.id.toolbar_profile);
        collapsing_layout = findViewById(R.id.collapsing_layout);
        user_name = findViewById(R.id.user_name);
        user_number = findViewById(R.id.user_number);
        status = findViewById(R.id.status);
        active_status = findViewById(R.id.active_status);
        chat = findViewById(R.id.chat);
        block = findViewById(R.id.block);
        report = findViewById(R.id.report);
        unblock = findViewById(R.id.unblock);
        user_avatar = findViewById(R.id.user_avatar);

        chat.setOnClickListener(this);
        toolbar_chat.setOnClickListener(this);
        block.setOnClickListener(this);
        unblock.setOnClickListener(this);

        toolbar.setTitle("Vivek");
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        if (!uid.isEmpty() && !uid.equals("null"))
            ApiCalls.getUserDetailsById(this, uid, this);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        Log.d("check_offset", "onOffsetChanged: " + verticalOffset + "  " + appBarLayout.getTotalScrollRange());
        if (verticalOffset == 0) {
            toolbar.setVisibility(View.GONE);
            collapsing_layout.setVisibility(View.VISIBLE);
            // Fully expanded
        } else {
            if (verticalOffset < (150 - appBarLayout.getTotalScrollRange()) || verticalOffset == -appBarLayout.getTotalScrollRange()) {
                toolbar.setVisibility(View.VISIBLE);
                collapsing_layout.setVisibility(View.GONE);
            } else {
                toolbar.setVisibility(View.GONE);
                collapsing_layout.setVisibility(View.VISIBLE);
            }
            // Not fully expanded or collapsed
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
//        if (!uid.isEmpty() && !uid.equals("null"))
//            ApiCalls.getUserDetailsById(this, uid, this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        CometChat.removeUserListener(listenerID);

    }

    @Override
    protected void onResume() {
        super.onResume();
        ApiCalls.getUserActiveStatus(this, listenerID, this);

    }

    @Override
    public void onSuccess(User user) {
        setUserData(user);

    }

    private void setUserData(User user) {

        Picasso.get().load(user.getAvatar()).into(user_avatar);
        Picasso.get().load(user.getAvatar()).into(toolbar_profile);
        user_name.setText(user.getName());
        toolbar_name.setText(user.getName());
        user_number.setText("+91 " + user.getUid());
        status.setText(user.getStatusMessage() == null ? "Hey there! I am using CometChat" : user.getStatusMessage());
        block_text.setText("Block " + user.getName());
        unblock_text.setText("Unblock " + user.getName());
        report_text.setText("Report " + user.getName());
        if (user.getStatus().equals(CometChatConstants.USER_STATUS_ONLINE))
            active_status.setText("online");
        else
            active_status.setText("last seen at " + Utilities.convertMillisToTime(user.getLastActiveAt()));

        if (user.isBlockedByMe()) {
            block.setVisibility(View.GONE);
            unblock.setVisibility(View.VISIBLE);
            chat.setVisibility(View.GONE);
            toolbar_chat.setVisibility(View.GONE);
            isBlocked=true;
        } else {
            block.setVisibility(View.VISIBLE);
            unblock.setVisibility(View.GONE);
            chat.setVisibility(View.VISIBLE);
            toolbar_chat.setVisibility(View.VISIBLE);
            isBlocked=false;
        }

    }

    @Override
    public void onError(CometChatException e) {

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.chat:
            case R.id.toolbar_chat:
                onBackPressed();
                break;
            case R.id.block:
                blockUser();
                break;
            case R.id.unblock:
                unBlockUser();
                break;
        }

    }

    private void blockUser() {
        ApiCalls.blockUser(this, Arrays.asList(uid), this);
    }

    private void unBlockUser() {
        ApiCalls.unBlockUsers(this, Arrays.asList(uid), this);
    }

    @Override
    public void onUserBlockUnBlockSuccess(HashMap<String, String> resultMap) {
        if(isBlocked){
            block.setVisibility(View.VISIBLE);
            unblock.setVisibility(View.GONE);
            isBlocked=false; //unblocked
        }else{
            block.setVisibility(View.GONE);
            unblock.setVisibility(View.VISIBLE);
            isBlocked=true; //blocked
        }
        Log.e("block_status", "onUserBlockUnBlockSuccess: " + resultMap);

    }

    @Override
    public void onUserBlockUnBlockError(CometChatException e) {

        Log.e("block_status", "onUserBlockUnBlockError: " + e.getMessage());

    }
}