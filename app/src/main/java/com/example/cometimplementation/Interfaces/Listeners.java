package com.example.cometimplementation.Interfaces;

import com.cometchat.pro.core.Call;
import com.cometchat.pro.models.User;

public interface Listeners {

    void addedNewUser(User user);
    void receiveCall(Call call);
    void acceptedOutGoingCall(Call call);
    void rejectedOutGoingCall(Call call);
    void canceledOutGoingCall(Call call);

}
