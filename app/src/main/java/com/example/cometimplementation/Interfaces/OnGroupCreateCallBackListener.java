package com.example.cometimplementation.Interfaces;

import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.Group;

public interface OnGroupCreateCallBackListener {

    void onGroupCreateSuccess(Group group);
    void onError(CometChatException e);

}
