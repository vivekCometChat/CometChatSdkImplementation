package com.example.cometimplementation.Interfaces;

import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.BaseMessage;

import java.util.List;

public interface MessageListiners {


    void onMessageReceivedSuccess(List<BaseMessage> list,boolean isScrolling);
    void onMessageReceivedError(CometChatException e);
}
