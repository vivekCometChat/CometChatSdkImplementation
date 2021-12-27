package com.example.cometimplementation.activities;

import android.app.Application;
import android.app.Dialog;
import android.content.Intent;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.cometchat.pro.core.Call;
import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.models.User;
import com.example.cometimplementation.ApiCalls;
import com.example.cometimplementation.Interfaces.CallStatus;
import com.example.cometimplementation.Interfaces.Listeners;
import com.example.cometimplementation.R;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class App extends Application implements Listeners, CallStatus {

    private String listenerId = "123456";

    @Override
    public void onCreate() {
        super.onCreate();
        ApiCalls.callInformation(this, this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        CometChat.removeMessageListener(listenerId);

    }

    @Override
    public void addedNewUser(User user) {

    }

    @Override
    public void receiveCall(Call call) {

        Intent i =new Intent(this,CallReciverActivity.class);
        i.putExtra("caller_image",call.getSender().getAvatar());
        i.putExtra("caller_name",call.getSender().getName());
        i.putExtra("session_id",call.getSessionId());
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);

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
    public void reject(Call call) {

    }

    @Override
    public void accept(Call call) {

    }
}
