package com.example.cometimplementation.utilities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.cometchat.pro.constants.CometChatConstants;
import com.cometchat.pro.core.AppSettings;
import com.cometchat.pro.core.Call;
import com.cometchat.pro.core.CallSettings;
import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.core.ConversationsRequest;
import com.cometchat.pro.core.MessagesRequest;
import com.cometchat.pro.core.UsersRequest;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.AudioMode;
import com.cometchat.pro.models.BaseMessage;
import com.cometchat.pro.models.Conversation;
import com.cometchat.pro.models.MediaMessage;
import com.cometchat.pro.models.TextMessage;
import com.cometchat.pro.models.User;
import com.example.cometimplementation.Interfaces.CallBackListener;
import com.example.cometimplementation.Interfaces.CallBackUnreadMessageCount;
import com.example.cometimplementation.Interfaces.CallStatus;
import com.example.cometimplementation.Interfaces.ConversationsListener;
import com.example.cometimplementation.Interfaces.FetchUserCallBack;
import com.example.cometimplementation.Interfaces.Listeners;
import com.example.cometimplementation.Interfaces.MessageListiners;
import com.example.cometimplementation.activities.ContactImportingAndProcessingActivity;
import com.example.cometimplementation.activities.LoginActivity;
import com.example.cometimplementation.activities.MainActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class ApiCalls {

    public static void createUser(User user, Context context, Listeners listeners) {

        CometChat.createUser(user, AppConfig.AppDetails.AUTH_KEY, new CometChat.CallbackListener<User>() {
            @Override
            public void onSuccess(User user) {
                listeners.addedNewUser(user);
                Toast.makeText(context, "Successfully added", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(CometChatException e) {
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }

    public static void callInformation(Context context, Listeners listeners) {

        String listenerId = "123456";

        CometChat.addCallListener(listenerId, new CometChat.CallListener() {
            @Override
            public void onIncomingCallReceived(Call call) {
                listeners.receiveCall(call);
                Log.d("awsedrfgscsc", "onIncomingCallReceived: ");
            }

            @Override
            public void onOutgoingCallAccepted(Call call) {
                listeners.acceptedOutGoingCall(call);
                Log.d("awsedrfgscsc", "onOutgoingCallAccepted: ");
            }

            @Override
            public void onOutgoingCallRejected(Call call) {
                listeners.rejectedOutGoingCall(call);

            }

            @Override
            public void onIncomingCallCancelled(Call call) {
                listeners.canceledOutGoingCall(call);
            }
        });

    }

    public static void rejectCall(Context context, CallStatus callStatus, String session_id) {
        CometChat.rejectCall(session_id, CometChatConstants.CALL_STATUS_REJECTED, new CometChat.CallbackListener<Call>() {
            @Override
            public void onSuccess(Call call) {
                callStatus.reject(call);
            }

            @Override
            public void onError(CometChatException e) {

            }
        });

    }

    public static void acceptCall(Context context, CallStatus callStatus, String session_id) {

        CometChat.acceptCall(session_id, new CometChat.CallbackListener<Call>() {
            @Override
            public void onSuccess(Call call) {
                callStatus.accept(call);
            }

            public void onError(CometChatException e) {

            }
        });

    }

    public static void startCall(Activity context, CometChat.OngoingCallListener listener, RelativeLayout container, String session_id) {

        CallSettings callSettings = new CallSettings.CallSettingsBuilder(context, container)
                .setSessionId(session_id).enableDefaultLayout(false)
                .build();
        CometChat.startCall(callSettings, new CometChat.OngoingCallListener() {
            @Override
            public void onUserJoined(User user) {
                listener.onUserJoined(user);
            }

            @Override
            public void onUserLeft(User user) {
                listener.onUserLeft(user);
            }

            @Override
            public void onError(CometChatException e) {
                listener.onError(e);
            }

            @Override
            public void onCallEnded(Call call) {
                listener.onCallEnded(call);
            }

            @Override
            public void onUserListUpdated(List<User> list) {
                listener.onUserListUpdated(list);
            }

            @Override
            public void onAudioModesUpdated(List<AudioMode> list) {
                listener.onAudioModesUpdated(list);
            }

        });


    }

    public static void cometChatInitialize(Context context) {
        AppSettings appSettings = new AppSettings.AppSettingsBuilder().subscribePresenceForAllUsers().setRegion(AppConfig.AppDetails.REGION).build();

        CometChat.init(context, AppConfig.AppDetails.APP_ID, appSettings, new CometChat.CallbackListener<String>() {
            @Override
            public void onSuccess(String successMessage) {
                Log.d("check", "Initialization completed successfully");


            }

            @Override
            public void onError(CometChatException e) {

            }
        });

    }

    public static void cometChatLogin(Context context, String uid) {

        CometChat.login(uid, AppConfig.AppDetails.AUTH_KEY, new CometChat.CallbackListener<User>() {
            @Override
            public void onSuccess(User user) {
                SharedPrefData.saveUserIdName(context, user.getUid(), user.getName());
                if (SharedPrefData.getSaveContacts(context)!=null && SharedPrefData.getCommonUser(context)!=null) {
                    context.startActivity(new Intent(context, MainActivity.class));
                } else {
                    context.startActivity(new Intent(context, ContactImportingAndProcessingActivity.class));
                }
                ((LoginActivity) context).finish();
            }

            @Override
            public void onError(CometChatException e) {
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

    }

    public static void fetchPreviousMessages(Context context, MessagesRequest messagesRequest, boolean isScrolling, MessageListiners messageListiners) {
        messagesRequest.fetchPrevious(new CometChat.CallbackListener<List<BaseMessage>>() {
            @Override
            public void onSuccess(List<BaseMessage> list) {
//                listener.onSuccess(list);
                messageListiners.onMessageReceivedSuccess(list,isScrolling);

            }

            @Override
            public void onError(CometChatException e) {
//                listener.onError(e);
                messageListiners.onMessageReceivedError(e);
            }
        });
    }

    public static void fetchCometChatUsers(Context context, FetchUserCallBack fetchUserCallBack) {

        UsersRequest usersRequest = new UsersRequest.UsersRequestBuilder().setLimit(30).build();

        usersRequest.fetchNext(new CometChat.CallbackListener<List<User>>() {
            @Override
            public void onSuccess(List<User> list) {
                Log.d("check", "User list received: " + list.size());
                fetchUserCallBack.onSuccess(list);
            }

            @Override
            public void onError(CometChatException e) {
                fetchUserCallBack.onError(e);
                Log.d("check", "User list fetching failed with exception: " + e.getMessage());
            }
        });

    }

    public static void getUnreadMessageCountForAllUsers(Context context, Conversation conversation, CallBackUnreadMessageCount callBackUnreadMessageCount){

        CometChat.getUnreadMessageCountForAllUsers(new CometChat.CallbackListener<HashMap<String, Integer>>() {
            @Override
            public void onSuccess(HashMap<String, Integer> stringIntegerHashMap) {

                conversation.setUnreadMessageCount(stringIntegerHashMap.get(conversation.getLastMessage().getSender().getUid()));
                callBackUnreadMessageCount.unreadMessageCountSuccess(conversation);

            }

            @Override
            public void onError(CometChatException e) {
                Log.d("check_the_count", "onSuccess: "+e.getMessage());

                callBackUnreadMessageCount.unreadMessageCountError(e);
            }

        });

    }

    public static void getConversations(Context context, ConversationsRequest conversationsRequest, ConversationsListener listener,boolean isScrolling){

        conversationsRequest.fetchNext(new CometChat.CallbackListener<List<Conversation>>() {
            @Override
            public void onSuccess(List<Conversation> conversations) {
//                conversationList.addAll(conversations);
//                recyclerAdapter.notifyDataSetChanged();

                listener.onGetConversationsSuccess(conversations,isScrolling);

            }

            @Override
            public void onError(CometChatException e) {
                // Hanlde failure
                listener.onGetConversationsError(e);
            }
        });

    }

}
