package com.example.cometimplementation.Interfaces;

import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.Conversation;

import java.util.List;

public interface  ConversationsListener {

    void onGetConversationsSuccess(List<Conversation> conversations,boolean isScrolling);
    void onGetConversationsError(CometChatException e);
}
