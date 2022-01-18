package com.example.cometimplementation.Interfaces;

import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.Group;

import java.util.List;

public interface FetchGroupListCallBack {
    void onGroupFetchSuccess(List<Group> list,boolean isScrolling);
    void onError(CometChatException e);
}
