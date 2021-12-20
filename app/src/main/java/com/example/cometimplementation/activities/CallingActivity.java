package com.example.cometimplementation.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.RelativeLayout;

import com.cometchat.pro.core.CallSettings;
import com.example.cometimplementation.R;

public class CallingActivity extends AppCompatActivity {
    RelativeLayout container;
    String sessionId = "CallingActivity";
    boolean audioOnly;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calling);
        initView();
    }

    private void initView() {
        container = findViewById(R.id.container);

    }


    private void setCalling() {
        CallSettings callSettings = new CallSettings.CallSettingsBuilder(this, container)
                .setSessionId(sessionId)
                .setAudioOnlyCall(true).build();
    }
}