package com.example.cometimplementation.Interfaces;

import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.GroupMember;

import java.util.List;

public interface FetchGroupMemberList {

    void onGroupMemberListSuccess(List<GroupMember> list);
    void onError(CometChatException e);


}
