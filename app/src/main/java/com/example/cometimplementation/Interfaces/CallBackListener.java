package com.example.cometimplementation.Interfaces;

import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.BaseMessage;

import java.util.List;

public interface CallBackListener {

    void onSuccess(List<BaseMessage> list);
    void onError(CometChatException e);

}
