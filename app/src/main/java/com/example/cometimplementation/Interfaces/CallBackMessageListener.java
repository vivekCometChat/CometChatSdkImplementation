package com.example.cometimplementation.Interfaces;

import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.BaseMessage;

public interface CallBackMessageListener {
    void onMessageSuccess(BaseMessage baseMessage);
    void onMessageFailure(CometChatException e);
    void onMessageDeleted(BaseMessage baseMessage);
}
