package com.example.cometimplementation.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cometchat.pro.constants.CometChatConstants;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.Group;
import com.cometchat.pro.models.User;
import com.example.cometimplementation.Interfaces.FetchGroupCallBack;
import com.example.cometimplementation.Interfaces.FetchUserCallBack;
import com.example.cometimplementation.Interfaces.UserListeners;
import com.example.cometimplementation.R;
import com.example.cometimplementation.utilities.ApiCalls;
import com.google.android.material.imageview.ShapeableImageView;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class UserDatailScreen extends AppCompatActivity implements UserListeners, FetchGroupCallBack, View.OnClickListener {
    String uid = "",profile_image="",user_name="";
    boolean isGroup;
    ShapeableImageView user_avatar;
    TextView name;
    ImageView status;
    TextView status_message;
    LinearLayout audio_call, video_call;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail_main);
        initView();
    }

    private void initView() {
        uid = getIntent().getStringExtra("uid");
        isGroup = getIntent().getBooleanExtra("isGroup", false);
        audio_call = findViewById(R.id.audio_call);
        video_call = findViewById(R.id.video_call);
        name = findViewById(R.id.name);
        status = findViewById(R.id.status);
        status_message = findViewById(R.id.status_message);
        user_avatar = findViewById(R.id.user_avatar);
        video_call.setOnClickListener(this);
        audio_call.setOnClickListener(this);
        if (!isGroup)
            ApiCalls.getUserDetailsById(this, uid, this);
        else
            ApiCalls.getGroupDetails(this, uid, this);

    }


    @Override
    public void onSuccess(User user) {
        if (uid.equals(user.getUid()))
            setUserData(user);
    }


    @Override
    public void onGroupFetchedSuccess(Group group) {
        setGroupData(group);

    }

    private void setUserData(User user) {
        Picasso.get().load(user.getAvatar()).into(user_avatar);
        name.setText(user.getName());
        status.setVisibility(View.VISIBLE);
        profile_image=user.getAvatar();
        user_name=user.getName();
        if (user.getStatus().equals(CometChatConstants.USER_STATUS_ONLINE))
            status.setColorFilter(ContextCompat.getColor(this, R.color.online_green), android.graphics.PorterDuff.Mode.MULTIPLY);
        else
            status.setColorFilter(ContextCompat.getColor(this, R.color.red), android.graphics.PorterDuff.Mode.MULTIPLY);

        status_message.setText(user.getStatus());

    }

    private void setGroupData(Group group) {
        user_name=group.getName();
        profile_image=group.getIcon();
        Picasso.get().load(group.getIcon()).into(user_avatar);
        name.setText(group.getName());
        status.setVisibility(View.GONE);
        profile_image=group.getIcon();
        status_message.setText("Members : "+group.getMembersCount());

    }

    @Override
    public void onError(CometChatException e) {

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.audio_call:
                startCall(isGroup,true);
                break;
            case R.id.video_call:
                startCall(isGroup,false);
                break;
        }

    }

    private void startCall(boolean isGroup, boolean isAudio) {
        Dexter.withActivity(this).withPermissions(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                Intent intent = new Intent(UserDatailScreen.this, CallingActivity.class);
                intent.putExtra("receiverUid", uid);
                intent.putExtra("session_id", "");
                intent.putExtra("user_img", profile_image);
                intent.putExtra("receiver_name", user_name);
                intent.putExtra("isGroup", isGroup);
                intent.putExtra("isAudio", isAudio);
                startActivity(intent);
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {

            }
        }).check();


    }

    public void back(View view) {
        onBackPressed();
    }
}