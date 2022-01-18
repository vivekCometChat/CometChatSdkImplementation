package com.example.cometimplementation.Interfaces;

import com.cometchat.pro.exceptions.CometChatException;

public interface SuccessMessageCallBack {

    void onActionSuccess(String successMessage);
    void onError(CometChatException e);
}
