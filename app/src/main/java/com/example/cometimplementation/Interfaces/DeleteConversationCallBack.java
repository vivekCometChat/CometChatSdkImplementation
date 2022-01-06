package com.example.cometimplementation.Interfaces;

import com.cometchat.pro.exceptions.CometChatException;

public interface DeleteConversationCallBack {

    void onConversationDeleteSuccess(String s,int position);
    void onConversationDeleteError(CometChatException e,int position);
}
