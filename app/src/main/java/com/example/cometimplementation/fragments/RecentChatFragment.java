package com.example.cometimplementation.fragments;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cometchat.pro.constants.CometChatConstants;
import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.core.ConversationsRequest;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.helpers.CometChatHelper;
import com.cometchat.pro.models.BaseMessage;
import com.cometchat.pro.models.Conversation;
import com.cometchat.pro.models.CustomMessage;
import com.cometchat.pro.models.MediaMessage;
import com.cometchat.pro.models.TextMessage;
import com.cometchat.pro.models.TypingIndicator;
import com.example.cometimplementation.Interfaces.CallBackUnreadMessageCount;
import com.example.cometimplementation.R;
import com.example.cometimplementation.adapter.ConversationAdapter;
import com.example.cometimplementation.adapter.RecyclerAdapter;
import com.example.cometimplementation.models.UserPojo;
import com.example.cometimplementation.utilities.ApiCalls;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;


public class RecentChatFragment extends Fragment implements CallBackUnreadMessageCount {
    private List<Conversation> conversationList = new ArrayList<>();
    private RecyclerView user_conversation_recyclerView;
    private ConversationAdapter recyclerAdapter;
    private TextView message;
    private ConversationsRequest conversationsRequest;
    private String listenerID = "RecentChatFragment.java";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recent_chat, container, false);

        Log.d("hey_check_lifecycle", "onCreateView: ");

        initViews(view);

        return view;
    }
    @Override
    public void onResume() {
        Log.d("hey_check_lifecycle", "onResume: ");
        super.onResume();
        CometChat.addMessageListener(listenerID, new CometChat.MessageListener() {
            @Override
            public void onTextMessageReceived(TextMessage textMessage) {
                toGetUnreadMessageCount(textMessage);
            }

            @Override
            public void onMediaMessageReceived(MediaMessage mediaMessage) {
                toGetUnreadMessageCount(mediaMessage);
            }

            @Override
            public void onCustomMessageReceived(CustomMessage customMessage) {

                Log.d("check", "Custom message received successfully: " + customMessage.toString());
            }

            @Override
            public void onTypingStarted(TypingIndicator typingIndicator) {

            }

            @Override
            public void onTypingEnded(TypingIndicator typingIndicator) {

            }
        });
    }

    @Override
    public void onPause() {
        Log.d("hey_check_lifecycle", "onPause: ");
        CometChat.removeMessageListener(listenerID);
        super.onPause();
    }

    @Override
    public void onStart() {
        conversationList.clear();
        getConversations();
        Log.d("hey_check_lifecycle", "onStart: ");
        super.onStart();
    }



    private void initViews(View view) {

        user_conversation_recyclerView = view.findViewById(R.id.recyclerView);
        message = view.findViewById(R.id.message);
        setUsersRecycler();

    }

    private void getConversations() {
        conversationList.clear();
        conversationsRequest = new ConversationsRequest.ConversationsRequestBuilder()
                .setLimit(50)
                .setConversationType(CometChatConstants.CONVERSATION_TYPE_USER)
                .build();

        conversationsRequest.fetchNext(new CometChat.CallbackListener<List<Conversation>>() {
            @Override
            public void onSuccess(List<Conversation> conversations) {
                conversationList.addAll(conversations);
                recyclerAdapter.notifyDataSetChanged();

            }

            @Override
            public void onError(CometChatException e) {
                // Hanlde failure
            }
        });

    }

    private void setUsersRecycler() {

        user_conversation_recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerAdapter = new ConversationAdapter(conversationList, getActivity());
        user_conversation_recyclerView.setAdapter(recyclerAdapter);
    }


    public void toGetUnreadMessageCount(BaseMessage message) {
        Conversation conversation = CometChatHelper.getConversationFromMessage(message);
        ApiCalls.getUnreadMessageCountForAllUsers(getContext(), conversation, this);


    }

    @Override
    public void unreadMessageCountSuccess(Conversation conversation) {
        conversationList.set(conversationList.indexOf(conversation), conversation);
        recyclerAdapter.notifyItemChanged(conversationList.indexOf(conversation));
    }

    @Override
    public void unreadMessageCountError(CometChatException e) {

    }
}