package com.example.cometimplementation.Interfaces;

import com.cometchat.pro.exceptions.CometChatException;

import java.util.HashMap;

public interface OnMembersAddedCallBack {
    void onMembersAddedSuccess(HashMap<String, String> successMap);
    void onError(CometChatException e);
}
