package com.example.cometimplementation.Interfaces;

import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.MessageReceipt;

import java.util.List;

public interface MessageReadByListCallBack {

    void onMessageReadBySuccess(List<MessageReceipt> messageReceipts);
    void onError(CometChatException e);

}
