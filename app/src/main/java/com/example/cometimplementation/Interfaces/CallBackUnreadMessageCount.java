package com.example.cometimplementation.Interfaces;

import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.Conversation;

public interface CallBackUnreadMessageCount {

    void unreadMessageCountSuccess(Conversation conversation);

    void unreadMessageCountError(CometChatException e);

}
