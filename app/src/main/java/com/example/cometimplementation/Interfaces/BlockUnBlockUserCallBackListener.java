package com.example.cometimplementation.Interfaces;

import com.cometchat.pro.exceptions.CometChatException;

import java.util.HashMap;

public interface BlockUnBlockUserCallBackListener {
    void onUserBlockUnBlockSuccess(HashMap<String, String> resultMap);
    void onUserBlockUnBlockError(CometChatException e);

}
