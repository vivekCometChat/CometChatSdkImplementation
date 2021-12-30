package com.example.cometimplementation.Interfaces;

import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.User;

import java.util.List;

public interface FetchUserCallBack {

    void onSuccess(List<User> list);
    void onError(CometChatException e);

}
