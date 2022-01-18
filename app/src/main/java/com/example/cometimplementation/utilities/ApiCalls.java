package com.example.cometimplementation.utilities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.cometchat.pro.constants.CometChatConstants;
import com.cometchat.pro.core.AppSettings;
import com.cometchat.pro.core.BlockedUsersRequest;
import com.cometchat.pro.core.Call;
import com.cometchat.pro.core.CallSettings;
import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.core.ConversationsRequest;
import com.cometchat.pro.core.GroupMembersRequest;
import com.cometchat.pro.core.GroupsRequest;
import com.cometchat.pro.core.MessagesRequest;
import com.cometchat.pro.core.UsersRequest;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.AudioMode;
import com.cometchat.pro.models.BaseMessage;
import com.cometchat.pro.models.Conversation;
import com.cometchat.pro.models.Group;
import com.cometchat.pro.models.GroupMember;
import com.cometchat.pro.models.MediaMessage;
import com.cometchat.pro.models.MessageReceipt;
import com.cometchat.pro.models.TextMessage;
import com.cometchat.pro.models.User;
import com.example.cometimplementation.Interfaces.BlockUnBlockUserCallBackListener;
import com.example.cometimplementation.Interfaces.CallBackMessageListener;
import com.example.cometimplementation.Interfaces.CallBackUnreadMessageCount;
import com.example.cometimplementation.Interfaces.CallStatus;
import com.example.cometimplementation.Interfaces.ConversationsListener;
import com.example.cometimplementation.Interfaces.DeleteConversationCallBack;
import com.example.cometimplementation.Interfaces.FetchGroupCallBack;
import com.example.cometimplementation.Interfaces.FetchGroupListCallBack;
import com.example.cometimplementation.Interfaces.FetchGroupMemberList;
import com.example.cometimplementation.Interfaces.FetchUserCallBack;
import com.example.cometimplementation.Interfaces.Listeners;
import com.example.cometimplementation.Interfaces.MessageListiners;
import com.example.cometimplementation.Interfaces.MessageReadByListCallBack;
import com.example.cometimplementation.Interfaces.OnGroupCreateCallBackListener;
import com.example.cometimplementation.Interfaces.OnMembersAddedCallBack;
import com.example.cometimplementation.Interfaces.SuccessMessageCallBack;
import com.example.cometimplementation.Interfaces.UserListeners;
import com.example.cometimplementation.activities.ContactImportingAndProcessingActivity;
import com.example.cometimplementation.activities.LoginActivity;
import com.example.cometimplementation.activities.MainActivity;
import com.example.cometimplementation.activities.ProfileActivity;
import com.example.cometimplementation.activities.SplashScreen;
import com.facebook.react.uimanager.events.ContentSizeChangeEvent;

import java.io.File;
import java.util.HashMap;
import java.util.List;

public class ApiCalls {

    public static void addMembersInGroup(Context context, String guid, List<GroupMember> members, OnMembersAddedCallBack listener) {
        CometChat.addMembersToGroup(guid, members, null, new CometChat.CallbackListener<HashMap<String, String>>() {
            @Override
            public void onSuccess(HashMap<String, String> successMap) {
                listener.onMembersAddedSuccess(successMap);
            }

            @Override
            public void onError(CometChatException e) {

                listener.onError(e);
            }
        });

    }

    public static void createGroup(Context context, String GUID, String name, String groupType, String password, String icon, String description, OnGroupCreateCallBackListener listener) {

        Group group = new Group(GUID, name, groupType, password, icon, description);
        CometChat.createGroup(group, new CometChat.CallbackListener<Group>() {
            @Override
            public void onSuccess(Group group) {
                listener.onGroupCreateSuccess(group);
            }

            @Override
            public void onError(CometChatException e) {
                listener.onError(e);
            }
        });

    }

    public static void deleteConversationWithUser(Context context, String uid, DeleteConversationCallBack conversationCallBack, int position) {
        CometChat.deleteConversation(uid, CometChatConstants.RECEIVER_TYPE_USER, new CometChat.CallbackListener<String>() {
            @Override
            public void onSuccess(String s) {
                conversationCallBack.onConversationDeleteSuccess(s, position);
            }

            @Override
            public void onError(CometChatException e) {
                conversationCallBack.onConversationDeleteError(e, position);
            }
        });
    }

    public static void deleteConversationWithGroup(Context context, String uid, DeleteConversationCallBack conversationCallBack, int position) {
        CometChat.deleteConversation(uid, CometChatConstants.RECEIVER_TYPE_GROUP, new CometChat.CallbackListener<String>() {
            @Override
            public void onSuccess(String s) {
                conversationCallBack.onConversationDeleteSuccess(s, position);
            }

            @Override
            public void onError(CometChatException e) {
                conversationCallBack.onConversationDeleteError(e, position);
            }
        });
    }

    public static void logOutCurrentUser(Context context) {

        CometChat.logout(new CometChat.CallbackListener<String>() {
            @Override
            public void onSuccess(String s) {
                SharedPrefData.clearAllPrefData(context);
                context.startActivity(new Intent(context, LoginActivity.class));
                ((ProfileActivity) context).finishAffinity();
            }

            @Override
            public void onError(CometChatException e) {

            }
        });
    }

    public static void getUserDetailsById(Context context, String uid, UserListeners listeners) {
        CometChat.getUser(uid, new CometChat.CallbackListener<User>() {
            @Override
            public void onSuccess(User user) {
                listeners.onSuccess(user);
            }

            @Override
            public void onError(CometChatException e) {
                listeners.onError(e);
            }
        });
    }

    public static void unBlockUsers(Context context, List<String> uids, BlockUnBlockUserCallBackListener listener) {
        CometChat.unblockUsers(uids, new CometChat.CallbackListener<HashMap<String, String>>() {
            @Override
            public void onSuccess(HashMap<String, String> resultMap) {
                listener.onUserBlockUnBlockSuccess(resultMap);
                // Handle block users success.
            }

            @Override
            public void onError(CometChatException e) {
                listener.onUserBlockUnBlockError(e);
                // Handle block users failure
            }
        });
    }

    public static void blockUser(Context context, List<String> uids, BlockUnBlockUserCallBackListener listener) {

        CometChat.blockUsers(uids, new CometChat.CallbackListener<HashMap<String, String>>() {
            @Override
            public void onSuccess(HashMap<String, String> resultMap) {
                listener.onUserBlockUnBlockSuccess(resultMap);
                // Handle block users success.
            }

            @Override
            public void onError(CometChatException e) {
                listener.onUserBlockUnBlockError(e);
                // Handle block users failure
            }
        });
    }

    public static void updateUserDetails(User user, Context context, UserListeners listeners) {
        Log.e("see_object", "updateUserDetails: " + user.getStatusMessage() + "\n" + user.getUid());
        CometChat.updateCurrentUserDetails(user, new CometChat.CallbackListener<User>() {
            @Override
            public void onSuccess(User user) {
                listeners.onSuccess(user);

            }

            @Override
            public void onError(CometChatException e) {
                listeners.onError(e);

            }
        });
    }

    public static void createUser(User user, Context context, Listeners listeners) {

        user.setStatusMessage("Hey! there i am using CometChat");
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
                context.startActivity(new Intent(context, MainActivity.class));
                ((SplashScreen) context).finish();
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
                messageListiners.onMessageReceivedSuccess(list, isScrolling);

            }

            @Override
            public void onError(CometChatException e) {
                messageListiners.onMessageReceivedError(e);
            }
        });
    }

    public static void fetchCometChatUsers(Context context, UsersRequest usersRequest, FetchUserCallBack fetchUserCallBack) {

//        UsersRequest usersRequest = new UsersRequest.UsersRequestBuilder().setLimit(30).build();

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

    public static void getUnreadMessageCountForAllUsers(Context context, Conversation conversation, CallBackUnreadMessageCount callBackUnreadMessageCount) {

        CometChat.getUnreadMessageCountForAllUsers(new CometChat.CallbackListener<HashMap<String, Integer>>() {
            @Override
            public void onSuccess(HashMap<String, Integer> stringIntegerHashMap) {

                conversation.setUnreadMessageCount(stringIntegerHashMap.get(conversation.getLastMessage().getSender().getUid()));

                callBackUnreadMessageCount.unreadMessageCountSuccess(conversation);

            }

            @Override
            public void onError(CometChatException e) {
                Log.d("check_the_count", "onSuccess: " + e.getMessage());

                callBackUnreadMessageCount.unreadMessageCountError(e);
            }

        });

    }

    public static void getUnreadMessageCountForGroup(Context context, Conversation conversation, CallBackUnreadMessageCount callBackUnreadMessageCount) {
        Group group = (Group) conversation.getLastMessage().getReceiver();

        CometChat.getUnreadMessageCountForGroup(group.getGuid(), new CometChat.CallbackListener<HashMap<String, Integer>>() {
            @Override
            public void onSuccess(HashMap<String, Integer> stringIntegerHashMap) {
                Group group = (Group) conversation.getLastMessage().getReceiver();
                int count = stringIntegerHashMap.get(group.getGuid());
                conversation.setUnreadMessageCount(count);
                callBackUnreadMessageCount.unreadMessageCountSuccess(conversation);

            }

            @Override
            public void onError(CometChatException e) {
                Log.d("check_the_count", "onSuccess: " + e.getMessage());
                e.printStackTrace();
                callBackUnreadMessageCount.unreadMessageCountError(e);
            }

        });

    }

    public static void getConversations(Context context, ConversationsRequest conversationsRequest, ConversationsListener listener, boolean isScrolling) {

        conversationsRequest.fetchNext(new CometChat.CallbackListener<List<Conversation>>() {
            @Override
            public void onSuccess(List<Conversation> conversations) {

                listener.onGetConversationsSuccess(conversations, isScrolling);

            }

            @Override
            public void onError(CometChatException e) {
                listener.onGetConversationsError(e);
            }
        });

    }

    public static void sendTextMessage(Context context, int message_id, boolean isReplyMessage, String message, String receiverUid, CallBackMessageListener listener, String messageType, boolean isGroup) {
        TextMessage textMessage;
        if (isGroup) {
            textMessage = new TextMessage(receiverUid, message, CometChatConstants.RECEIVER_TYPE_GROUP);
        } else {
            textMessage = new TextMessage(receiverUid, message, CometChatConstants.RECEIVER_TYPE_USER);
        }

        if (isReplyMessage)
            textMessage.setParentMessageId(message_id);

        CometChat.sendMessage(textMessage, new CometChat.CallbackListener<TextMessage>() {
            @Override
            public void onSuccess(TextMessage textMessage) {
                listener.onMessageSuccess(textMessage);
            }

            @Override
            public void onError(CometChatException e) {
                listener.onMessageFailure(e);
                Log.d("check", "Message sending failed with exception: " + e.getMessage());

            }
        });
    }

    public static void sendMediaMessage(Context context, int message_id, boolean isReplyMessage, String message, String receiverUid, CallBackMessageListener listener, String messageType, String media_path, boolean isGroup) {
        MediaMessage mediaMessage;
        if (isGroup) {
            mediaMessage = new MediaMessage(receiverUid, new File(media_path), CometChatConstants.MESSAGE_TYPE_IMAGE, CometChatConstants.RECEIVER_TYPE_GROUP);

        } else {
            mediaMessage = new MediaMessage(receiverUid, new File(media_path), CometChatConstants.MESSAGE_TYPE_IMAGE, CometChatConstants.RECEIVER_TYPE_USER);

        }

        if (isReplyMessage)
            mediaMessage.setParentMessageId(message_id);

        CometChat.sendMediaMessage(mediaMessage, new CometChat.CallbackListener<MediaMessage>() {
            @Override
            public void onSuccess(MediaMessage mediaMessage) {
                listener.onMessageSuccess(mediaMessage);

            }

            @Override
            public void onError(CometChatException e) {
                listener.onMessageFailure(e);
                Log.d("check", "Media message sending failed with exception: " + e.getMessage());
            }
        });


    }

    public static void deleteMessage(Context context, int message_id, CallBackMessageListener listener) {

        CometChat.deleteMessage(message_id, new CometChat.CallbackListener<BaseMessage>() {
            @Override
            public void onSuccess(BaseMessage message) {
                listener.onMessageDeleted(message);
            }

            @Override
            public void onError(CometChatException e) {
//                Log.d(TAG, e.getMessage());
                listener.onMessageFailure(e);
            }
        });

    }

    public static void getUserActiveStatus(Context context, String unique_listener, UserListeners listeners) {
        CometChat.addUserListener(unique_listener, new CometChat.UserListener() {
            @Override
            public void onUserOnline(User user) {
                listeners.onSuccess(user);
            }

            @Override
            public void onUserOffline(User user) {
                listeners.onSuccess(user);

            }
        });
    }

    public static void getUsersById(Context context, List<String> uid_list, FetchUserCallBack fetchUserCallBack) {

        UsersRequest usersRequest = new UsersRequest.UsersRequestBuilder().setLimit(25).hideBlockedUsers(true).setUIDs(uid_list).build();
        usersRequest.fetchNext(new CometChat.CallbackListener<List<User>>() {
            @Override
            public void onSuccess(List<User> users) {
                fetchUserCallBack.onSuccess(users);
            }

            @Override
            public void onError(CometChatException e) {
                fetchUserCallBack.onError(e);

            }
        });

    }

    public static void getBlockedUsersByMe(Context context, FetchUserCallBack fetchUserCallBack, BlockedUsersRequest blockedUsersRequest) {
        blockedUsersRequest.fetchNext(new CometChat.CallbackListener<List<User>>() {
            @Override
            public void onSuccess(List<User> users) {
                fetchUserCallBack.onSuccess(users);

            }

            @Override
            public void onError(CometChatException e) {
                fetchUserCallBack.onError(e);
            }
        });
    }

    public static void getGroups(Context context, GroupsRequest groupsRequest, FetchGroupListCallBack fetchGroupListCallBack, boolean isScrolling) {


        groupsRequest.fetchNext(new CometChat.CallbackListener<List<Group>>() {
            @Override
            public void onSuccess(List<Group> list) {
                fetchGroupListCallBack.onGroupFetchSuccess(list, isScrolling);
            }

            @Override
            public void onError(CometChatException e) {
                fetchGroupListCallBack.onError(e);
            }
        });

    }

    public static void getGroupDetails(Context context, String GUID, FetchGroupCallBack fetchGroupCallBack) {
        CometChat.getGroup(GUID, new CometChat.CallbackListener<Group>() {
            @Override
            public void onSuccess(Group group) {
                fetchGroupCallBack.onGroupFetchedSuccess(group);
            }

            @Override
            public void onError(CometChatException e) {
                fetchGroupCallBack.onError(e);
            }
        });
    }

    public static void getGroupMembersList(Context context, GroupMembersRequest groupMembersRequest, FetchGroupMemberList fetchGroupMemberList) {

        groupMembersRequest.fetchNext(new CometChat.CallbackListener<List<GroupMember>>() {
            @Override
            public void onSuccess(List<GroupMember> list) {
                fetchGroupMemberList.onGroupMemberListSuccess(list);
            }

            @Override
            public void onError(CometChatException e) {
                fetchGroupMemberList.onError(e);
            }

        });

    }

    public static void removeGroupMember(Context context, String member_uid, String GUID, SuccessMessageCallBack callBack) {
        CometChat.kickGroupMember(member_uid, GUID, new CometChat.CallbackListener<String>() {
            @Override
            public void onSuccess(String successMessage) {
                callBack.onActionSuccess(successMessage);
            }

            @Override
            public void onError(CometChatException e) {
                callBack.onError(e);
            }
        });
    }

    public static void updateGroupInfo(Context context, String group_id, String name, String group_type, String password, FetchGroupCallBack fetchGroupCallBack) {

        Group group = new Group(group_id, name, group_type, password);
        CometChat.updateGroup(group, new CometChat.CallbackListener<Group>() {
            @Override
            public void onSuccess(Group group) {
                fetchGroupCallBack.onGroupFetchedSuccess(group);
            }

            @Override
            public void onError(CometChatException e) {
                fetchGroupCallBack.onError(e);

            }
        });

    }

    public static void leaveGroup(Context context, String GUID, SuccessMessageCallBack callBack) {

        CometChat.leaveGroup(GUID, new CometChat.CallbackListener<String>() {
            @Override
            public void onSuccess(String successMessage) {
                callBack.onActionSuccess(successMessage);
            }

            @Override
            public void onError(CometChatException e) {
                callBack.onError(e);
            }
        });
    }

    public static void deleteGroup(Context context, String GUID, SuccessMessageCallBack successMessageCallBack) {
        CometChat.deleteGroup(GUID, new CometChat.CallbackListener<String>() {
            @Override
            public void onSuccess(String successMessage) {
                successMessageCallBack.onActionSuccess(successMessage);
            }

            @Override
            public void onError(CometChatException e) {
                successMessageCallBack.onError(e);
            }
        });
    }

    public static void getMessageReadByUsers(Context context, int id, MessageReadByListCallBack callBack) {

        CometChat.getMessageReceipts(id, new CometChat.CallbackListener<List<MessageReceipt>>() {
            @Override
            public void onSuccess(List<MessageReceipt> messageReceipts) {
                callBack.onMessageReadBySuccess(messageReceipts);
            }

            @Override
            public void onError(CometChatException e) {
                // Handle error
                callBack.onError(e);
            }
        });

    }

}
