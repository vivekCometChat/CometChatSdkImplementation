package com.example.cometimplementation.Interfaces;

import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.User;

public interface UserListeners {

    void onSuccess(User user);
    void onError(CometChatException e);
}
