package com.example.cometimplementation.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.cometchat.pro.constants.CometChatConstants;
import com.cometchat.pro.core.Call;
import com.cometchat.pro.core.CallSettings;
import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.AudioMode;
import com.cometchat.pro.models.User;
import com.example.cometimplementation.ApiCalls;
import com.example.cometimplementation.Interfaces.CallStatus;
import com.example.cometimplementation.Interfaces.Listeners;
import com.example.cometimplementation.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CallingActivity extends AppCompatActivity implements CallStatus, Listeners {
    RelativeLayout container;
    String sessionId = "123456";
    boolean audioOnly = true;
    private String TAG = "calling_check";
    private String receiverID = "", calling_sessionId = "";
    private String listenerId = "123456";
    FloatingActionButton reject, accept;
    CircleImageView image_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calling);
        initView();
    }

    private void initView() {
        container = findViewById(R.id.container);
        reject = findViewById(R.id.reject);
        accept = findViewById(R.id.accept);
        image_view = findViewById(R.id.image_view);
        receiverID = getIntent().getStringExtra("receiverUid");
        calling_sessionId = getIntent().getStringExtra("session_id");

        if (receiverID.equals("") && !calling_sessionId.equals("")) {
            startCall(calling_sessionId);
        } else {
            setCalling();
        }


    }

    private void startCall(String sessionId) {
        CallSettings callSettings = new CallSettings.CallSettingsBuilder(this, container)
                .setSessionId(sessionId)
                .build();

        CometChat.startCall(callSettings, new CometChat.OngoingCallListener() {
            @Override
            public void onUserJoined(User user) {
                Log.d(TAG, "onUserJoined: Name " + user.getName());
            }

            @Override
            public void onUserLeft(User user) {
                Log.d(TAG, "onUserLeft: " + user.getName());
            }

            @Override
            public void onError(CometChatException e) {
                Log.d(TAG, "onError: " + e.getMessage());
            }

            @Override
            public void onCallEnded(Call call) {
                Log.d(TAG, "onCallEnded: " + call.toString());
            }

            @Override
            public void onUserListUpdated(List<User> list) {
                Log.d(TAG, "onUserListUpdated: " + list.toString());
            }

            @Override
            public void onAudioModesUpdated(List<AudioMode> list) {
                Log.d(TAG, "onAudioModesUpdated: " + list.toString());
            }

        });

    }

    private void setCalling() {

        Call call = new Call(receiverID, CometChatConstants.RECEIVER_TYPE_USER, CometChatConstants.CALL_TYPE_VIDEO);

        CometChat.initiateCall(call, new CometChat.CallbackListener<Call>() {
            @Override
            public void onSuccess(Call call) {
//                Log.d(TAG, "Call initiated successfully: " + );
            }

            @Override
            public void onError(CometChatException e) {

                Log.d(TAG, "Call initialization failed with exception: " + e.getMessage());

            }
        });


//        CallSettings callSettings = new CallSettings.CallSettingsBuilder(this, container)
//                .setSessionId(sessionId)
//                .setAudioOnlyCall(audioOnly).build();
//
//        CometChat.startCall(callSettings, new CometChat.OngoingCallListener() {
//            @Override
//            public void onUserJoined(User user) {
//                Log.d(TAG, "onUserJoined: Name " + user.getName());
//            }
//
//            @Override
//            public void onUserLeft(User user) {
//                Log.d(TAG, "onUserLeft: " + user.getName());
//            }
//
//            @Override
//            public void onError(CometChatException e) {
//                Log.d(TAG, "onError: " + e.getMessage());
//            }
//
//            @Override
//            public void onCallEnded(Call call) {
//                Log.d(TAG, "onCallEnded: " + call.toString());
//            }
//
//            @Override
//            public void onUserListUpdated(List<User> list) {
//                Log.d(TAG, "onUserListUpdated: " + list.toString());
//            }
//
//            @Override
//            public void onAudioModesUpdated(List<AudioMode> list) {
//                Log.d(TAG, "onAudioModesUpdated: " + list.toString());
//            }
//
//        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        ApiCalls.callInformation(this, this);

    }

    @Override
    public void reject(Call call) {

    }

    @Override
    public void accept(Call call) {

    }

    @Override
    protected void onPause() {
        super.onPause();
        CometChat.removeMessageListener(sessionId);


    }

    @Override
    public void addedNewUser(User user) {

    }

    @Override
    public void receiveCall(Call call) {

    }

    @Override
    public void acceptedOutGoingCall(Call call) {
        Log.d("acceptedOutGoingCall", "acceptedOutGoingCall: " + call.getReceiverUid());
        Toast.makeText(CallingActivity.this, "accepted", Toast.LENGTH_SHORT).show();
        startCall(call.getSessionId());

    }

    @Override
    public void rejectedOutGoingCall(Call call) {

    }

    @Override
    public void canceledOutGoingCall(Call call) {

    }
}