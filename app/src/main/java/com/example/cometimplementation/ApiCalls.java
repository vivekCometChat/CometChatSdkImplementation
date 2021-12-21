package com.example.cometimplementation;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.cometchat.pro.constants.CometChatConstants;
import com.cometchat.pro.core.Call;
import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.User;
import com.example.cometimplementation.Interfaces.CallStatus;
import com.example.cometimplementation.Interfaces.Listeners;
import com.example.cometimplementation.activities.CallingActivity;

public class ApiCalls {

    public static void createUser(User user, Context context, Listeners listeners) {

        CometChat.createUser(user, AppConfig.AppDetails.AUTH_KEY, new CometChat.CallbackListener<User>() {
            @Override
            public void onSuccess(User user) {
                listeners.addedNewUser(user);
                Toast.makeText(context, "Successfully added", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(CometChatException e) {
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }

    public static void callInformation(Context context, Listeners listeners) {

        String listenerId = "123456";

        CometChat.addCallListener(listenerId, new CometChat.CallListener() {
            @Override
            public void onIncomingCallReceived(Call call) {
                listeners.receiveCall(call);

            }

            @Override
            public void onOutgoingCallAccepted(Call call) {
                listeners.acceptedOutGoingCall(call);
            }

            @Override
            public void onOutgoingCallRejected(Call call) {


            }

            @Override
            public void onIncomingCallCancelled(Call call) {

            }

        });

    }

    public static void rejectCall(Context context, CallStatus callStatus, Call call) {
        CometChat.rejectCall(call.getSessionId(), CometChatConstants.CALL_STATUS_REJECTED, new CometChat.CallbackListener<Call>() {
            @Override
            public void onSuccess(Call call) {
                callStatus.reject(call);
            }

            @Override
            public void onError(CometChatException e) {

            }
        });

    }

    public static void acceptCall(Context context, CallStatus callStatus, Call call) {

        CometChat.acceptCall(call.getSessionId(), new CometChat.CallbackListener<Call>() {
            @Override
            public void onSuccess(Call call) {
                callStatus.accept(call);
            }

            public void onError(CometChatException e) {

            }
        });

    }

}
