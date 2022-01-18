package com.example.cometimplementation.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.annotation.SuppressLint;
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
import com.google.android.material.imageview.ShapeableImageView;
import com.jgabrielfreitas.core.BlurImageView;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CallingActivity extends AppCompatActivity implements CallStatus, Listeners, CometChat.OngoingCallListener {
    RelativeLayout container;
    FrameLayout outgoing_call_lay;
    String sessionId = "123456", receiver_type = "", videoOrNot = "";
    boolean isGroup;
    boolean audioOnly = false;
    private String TAG = "calling_check";
    private String receiverID = "", receiver_img = "", receiver_name = "";
//    ImageView persons_image;
    TextView name;
    ImageView chat, mute, unmute, cancel_call, off_speaker, on_speaker,end_call;
    CallManager callManager;
    LinearLayout call_controls,call_initiated;
    BlurImageView persons_image;
    ShapeableImageView receiver_img_view;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        setContentView(R.layout.activity_calling);
        initView();
    }

    private void initView() {

        chat = findViewById(R.id.chat);
        call_initiated = findViewById(R.id.call_initiated);
        mute = findViewById(R.id.mute);
        unmute = findViewById(R.id.unmute);
        cancel_call = findViewById(R.id.cancel_call);
        end_call = findViewById(R.id.end_call);

        off_speaker = findViewById(R.id.off_speaker);
        on_speaker = findViewById(R.id.on_speaker);
        call_controls = findViewById(R.id.call_controls);
        persons_image = findViewById(R.id.persons_image);
        receiver_img_view = findViewById(R.id.receiver_img_view);
        container = findViewById(R.id.container);
        outgoing_call_lay = findViewById(R.id.outgoing_call_lay);
//        persons_image = findViewById(R.id.persons_image);
        name = findViewById(R.id.name);
        container.setVisibility(View.GONE);
        outgoing_call_lay.setVisibility(View.VISIBLE);

        receiver_img = getIntent().getStringExtra("user_img");
        receiverID = getIntent().getStringExtra("receiverUid");
        receiver_name = getIntent().getStringExtra("receiver_name");
        isGroup = getIntent().getBooleanExtra("isGroup", false);
        audioOnly = getIntent().getBooleanExtra("isAudio", false);
        name.setText(receiver_name);
        if (!receiver_img.isEmpty()) {
            Picasso.get().load(receiver_img).into(persons_image);
            persons_image.setBlur(20);
            Picasso.get().load(receiver_img).into(receiver_img_view);
        }
        if (isGroup)
            receiver_type = CometChatConstants.RECEIVER_TYPE_GROUP;
        else
            receiver_type = CometChatConstants.RECEIVER_TYPE_USER;

        if (audioOnly)
            videoOrNot = CometChatConstants.CALL_TYPE_AUDIO;
        else
            videoOrNot = CometChatConstants.CALL_TYPE_VIDEO;

        setCalling();
        callManager = CallManager.getInstance();
//        cancel_call.setOnClickListener(view -> {
//            ApiCalls.rejectCall(CallingActivity.this, this, call_sessionId_);
//
//        });
        end_call.setOnClickListener(view -> {
            ApiCalls.rejectCall(CallingActivity.this, this, call_sessionId_);

        });


    }


    String call_sessionId_ = "";

    @SuppressLint("WrongConstant")
    private void setCalling() {
        Call call = new Call(receiverID, receiver_type, videoOrNot);
        CometChat.initiateCall(call, new CometChat.CallbackListener<Call>() {
            @Override
            public void onSuccess(Call call) {
                call_sessionId_ = call.getSessionId();
//                call_controls.setVisibility(View.VISIBLE);
                call_initiated.setVisibility(View.VISIBLE);
                Log.e("sasadddcsdcdsc", "Call initialization : ");

            }

            @Override
            public void onError(CometChatException e) {

                Log.e("sasadddcsdcdsc", "Call initialization failed with exception: " + e.getMessage());

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
        CometChat.removeCallListener(sessionId);


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
        ApiCalls.startCall(CallingActivity.this, this, container, call.getSessionId());
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

    private void endCall(){
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
      endCall();
    }


    public void camOff(View view) {
        callManager.pauseVideo(true);

    }

    public void onCam(View view) {
        callManager.pauseVideo(false);

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
        call_controls.setVisibility(View.VISIBLE);
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