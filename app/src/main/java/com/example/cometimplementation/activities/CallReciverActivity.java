package com.example.cometimplementation.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.Manifest;
import android.content.Context;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cometchat.pro.core.Call;
import com.cometchat.pro.core.CallManager;
import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.AudioMode;
import com.cometchat.pro.models.User;
import com.example.cometimplementation.utilities.ApiCalls;
import com.example.cometimplementation.Interfaces.CallStatus;
import com.example.cometimplementation.R;
import com.google.android.material.imageview.ShapeableImageView;
import com.jgabrielfreitas.core.BlurImageView;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CallReciverActivity extends AppCompatActivity implements CallStatus, CometChat.OngoingCallListener {

    String sender_img = "", name = "", session_id = "";
    ImageView reject_call, accept_call;
    TextView caller_name;
    FrameLayout call_notify_container;
    RelativeLayout calling_container;
    LinearLayout call_controls;
    ImageView chat, mute, unmute, cancel_call,off_speaker, on_speaker;
    CallManager callManager;
    BlurImageView persons_image;
    ShapeableImageView receiver_img_view;
    Vibrator v;
    Ringtone currentRingtone;
    Uri currentRintoneUri;
    CountDownTimer countDownTimer;
    boolean ring = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN, WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        setContentView(R.layout.activity_call_reciver);
        initViews();
        accept_call.setOnClickListener(view -> {
            Dexter.withActivity(this).withPermissions(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO).withListener(new MultiplePermissionsListener() {
                @Override
                public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                    setAccept_call();
                }

                @Override
                public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                    permissionToken.continuePermissionRequest();
                    Toast.makeText(CallReciverActivity.this, "Please give permission to start call", Toast.LENGTH_SHORT).show();
                }
            }).check();
            ApiCalls.acceptCall(CallReciverActivity.this, this, session_id);

        });
        reject_call.setOnClickListener(view -> setReject_call());


    }

    private void initViews() {
        chat = findViewById(R.id.chat);
        mute = findViewById(R.id.mute);
        unmute = findViewById(R.id.unmute);
        cancel_call = findViewById(R.id.cancel_call);

        off_speaker = findViewById(R.id.off_speaker);
        on_speaker = findViewById(R.id.on_speaker);


        persons_image = findViewById(R.id.persons_image);
        reject_call = findViewById(R.id.reject_call);
        accept_call = findViewById(R.id.accept_call);
        caller_name = findViewById(R.id.caller_name);
        receiver_img_view = findViewById(R.id.receiver_img_view);
        calling_container = findViewById(R.id.calling_container);
        call_notify_container = findViewById(R.id.call_notify_container);
        call_controls = findViewById(R.id.call_controls);
        session_id = getIntent().getStringExtra("session_id");
        sender_img = getIntent().getStringExtra("caller_image");
        name = getIntent().getStringExtra("caller_name");
        caller_name.setText(name);

        calling_container.setVisibility(View.GONE);
        call_controls.setVisibility(View.GONE);

        call_notify_container.setVisibility(View.VISIBLE);
        callManager = CallManager.getInstance();
        if (!sender_img.isEmpty()) {
            Picasso.get().load(sender_img).error(R.drawable.img_person_test).into(persons_image);
            persons_image.setBlur(20);
            Picasso.get().load(sender_img).error(R.drawable.img_person_test).into(receiver_img_view);
        }


        v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        currentRintoneUri = RingtoneManager.getActualDefaultRingtoneUri(this
                .getApplicationContext(), RingtoneManager.TYPE_RINGTONE);
        currentRingtone = RingtoneManager.getRingtone(this, currentRintoneUri);

    }

    private void setAccept_call() {
        ApiCalls.acceptCall(CallReciverActivity.this, this, session_id);
    }

    private void setReject_call() {
        ApiCalls.rejectCall(CallReciverActivity.this, this, session_id);

    }

    @Override
    public void reject(Call call) {
        ring = false;
        finish();

    }

    @Override
    public void accept(Call call) {
        ring = false;
        ApiCalls.startCall(this, this, calling_container, call.getSessionId());
    }

    @Override
    public void onUserJoined(User user) {
        call_controls.setVisibility(View.VISIBLE);
        calling_container.setVisibility(View.VISIBLE);
        call_notify_container.setVisibility(View.GONE);
    }

    @Override
    public void onUserLeft(User user) {

    }

    @Override
    public void onError(CometChatException e) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
        try {
            currentRingtone.play();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                currentRingtone.setLooping(true);
            }
            countDownTimer = new CountDownTimer(30000, 2000) {
                @Override
                public void onTick(long l) {
                    if (ring) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            v.vibrate(VibrationEffect.createOneShot(1000, VibrationEffect.DEFAULT_AMPLITUDE));
                        } else {
                            //deprecated in API 26
                            v.vibrate(1000);
                        }
                    } else {
                        countDownTimer.cancel();
                        currentRingtone.stop();
                        v.cancel();
                    }
                }

                @Override
                public void onFinish() {
                    currentRingtone.stop();
                    setReject_call();
                }
            };
            countDownTimer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onCallEnded(Call call) {
        finish();
        Toast.makeText(this, "call ended", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUserListUpdated(List<User> list) {

    }

    @Override
    public void onAudioModesUpdated(List<AudioMode> list) {

    }


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
        CometChat.endCall(session_id, new CometChat.CallbackListener<Call>() {
            @Override
            public void onSuccess(Call call) {
                finish();
            }

            @Override
            public void onError(CometChatException e) {
                Toast.makeText(CallReciverActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                // handled end call error
            }

        });

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
}