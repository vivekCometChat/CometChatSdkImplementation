package com.example.cometimplementation.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cometchat.pro.constants.CometChatConstants;
import com.cometchat.pro.core.Call;
import com.cometchat.pro.core.CallManager;
import com.cometchat.pro.core.CallSettings;
import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.AudioMode;
import com.cometchat.pro.models.User;
import com.example.cometimplementation.utilities.ApiCalls;
import com.example.cometimplementation.Interfaces.CallStatus;
import com.example.cometimplementation.Interfaces.Listeners;
import com.example.cometimplementation.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CallingActivity extends AppCompatActivity implements CallStatus, Listeners, CometChat.OngoingCallListener {
    RelativeLayout container;
    FrameLayout outgoing_call_lay;
    String sessionId = "123456";
    boolean audioOnly = true;
    private String TAG = "calling_check";
    private String receiverID = "", receiver_img = "", receiver_name = "";
    private String listenerId = "123456";
    ImageView persons_image;
    TextView name;
    ImageView chat, mute, unmute, cancel_call, off_camera, on_camera, off_speaker, on_speaker;
    CallManager callManager;
    LinearLayout call_controls;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        setContentView(R.layout.activity_calling);
        initView();
    }

    private void initView() {

        chat = findViewById(R.id.chat);
        mute = findViewById(R.id.mute);
        unmute = findViewById(R.id.unmute);
        cancel_call = findViewById(R.id.cancel_call);
        off_camera = findViewById(R.id.off_camera);
        on_camera = findViewById(R.id.on_camera);
        off_speaker = findViewById(R.id.off_speaker);
        on_speaker = findViewById(R.id.on_speaker);
        call_controls = findViewById(R.id.call_controls);


        container = findViewById(R.id.container);
        outgoing_call_lay = findViewById(R.id.outgoing_call_lay);
        persons_image = findViewById(R.id.persons_image);
        name = findViewById(R.id.name);
        container.setVisibility(View.GONE);
        outgoing_call_lay.setVisibility(View.VISIBLE);

        receiver_img = getIntent().getStringExtra("user_img");
        receiverID = getIntent().getStringExtra("receiverUid");
        receiver_name = getIntent().getStringExtra("receiver_name");
        name.setText(receiver_name);
        if (!receiver_img.isEmpty()) {
            Picasso.get().load(receiver_img).into(persons_image);
        }

        setCalling();
        callManager = CallManager.getInstance();


    }

    private void startCall(String sessionId) {


        CallSettings callSettings = new CallSettings.CallSettingsBuilder(this, container)
                .setSessionId(sessionId).enableDefaultLayout(false)
                .build();

        CometChat.startCall(callSettings, new CometChat.OngoingCallListener() {
            @Override
            public void onUserJoined(User user) {
                Log.d(TAG, "onUserJoined: Name " + user.getName());
                outgoing_call_lay.setVisibility(View.GONE);
                container.setVisibility(View.VISIBLE);
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
                finish();
                Toast.makeText(CallingActivity.this, "call ended", Toast.LENGTH_SHORT).show();
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

    String call_sessionId_ = "";

    private void setCalling() {

        Call call = new Call(receiverID, CometChatConstants.RECEIVER_TYPE_USER, CometChatConstants.CALL_TYPE_VIDEO);

        CometChat.initiateCall(call, new CometChat.CallbackListener<Call>() {
            @Override
            public void onSuccess(Call call) {
                call_sessionId_ = call.getSessionId();
                call_controls.setVisibility(View.VISIBLE);
            }

            @Override
            public void onError(CometChatException e) {

                Log.d(TAG, "Call initialization failed with exception: " + e.getMessage());

            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        ApiCalls.callInformation(this, this);

    }

    @Override
    public void reject(Call call) {
        finish();
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
        ApiCalls.startCall(CallingActivity.this, this,container,call.getSessionId());
//        startCall(call.getSessionId());

    }

    @Override
    public void rejectedOutGoingCall(Call call) {
        finish();
        Toast.makeText(CallingActivity.this, "call Rejected", Toast.LENGTH_SHORT).show();
        Log.d("check_cancelled", "rejectedOutGoingCall: ");

    }

    @Override
    public void canceledOutGoingCall(Call call) {
        finish();

    }


    //UI methods
    public void chat(View view) {
        Toast.makeText(this, "yet to implement!", Toast.LENGTH_SHORT).show();
    }

    public void mute(View view) {

        callManager.muteAudio(true);
        mute.setVisibility(View.GONE);
        unmute.setVisibility(View.VISIBLE);


    }

    public void unMute(View view) {
        callManager.muteAudio(false);
        mute.setVisibility(View.VISIBLE);
        unmute.setVisibility(View.GONE);
    }

    public void endCall(View view) {
        CometChat.endCall(call_sessionId_, new CometChat.CallbackListener<Call>() {
            @Override
            public void onSuccess(Call call) {
                finish();
            }

            @Override
            public void onError(CometChatException e) {
                Toast.makeText(CallingActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                // handled end call error
            }

        });

    }

    public void camOff(View view) {
        callManager.pauseVideo(true);
        off_camera.setVisibility(View.GONE);
        on_camera.setVisibility(View.VISIBLE);

    }

    public void onCam(View view) {
        callManager.pauseVideo(false);
        off_camera.setVisibility(View.VISIBLE);
        on_camera.setVisibility(View.GONE);


    }

    public void offSpeaker(View view) {


    }

    public void onSpeaker(View view) {


    }
    //UI methods end


    //start call methods
    @Override
    public void onUserJoined(User user) {
        outgoing_call_lay.setVisibility(View.GONE);
        container.setVisibility(View.VISIBLE);
    }

    @Override
    public void onUserLeft(User user) {

    }

    @Override
    public void onError(CometChatException e) {

    }

    @Override
    public void onCallEnded(Call call) {
        finish();
        Toast.makeText(CallingActivity.this, "call ended", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUserListUpdated(List<User> list) {

    }

    @Override
    public void onAudioModesUpdated(List<AudioMode> list) {

    }
}