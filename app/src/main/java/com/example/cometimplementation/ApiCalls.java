package com.example.cometimplementation;

import android.content.Context;
import android.widget.Toast;

import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.User;

public class ApiCalls {

    public static void createUser(User user, Context context,Listeners listeners){

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


}
