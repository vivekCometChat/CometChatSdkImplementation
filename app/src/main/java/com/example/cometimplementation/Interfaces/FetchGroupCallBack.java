package com.example.cometimplementation.Interfaces;

import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.Group;

public interface FetchGroupCallBack {

    void onGroupFetchedSuccess(Group group);
    void onError(CometChatException e);

}
