package com.example.cometimplementation.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.TextView;

import com.cometchat.pro.constants.CometChatConstants;
import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.core.ConversationsRequest;
import com.cometchat.pro.core.MessagesRequest;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.helpers.CometChatHelper;
import com.cometchat.pro.models.BaseMessage;
import com.cometchat.pro.models.Conversation;
import com.cometchat.pro.models.CustomMessage;
import com.cometchat.pro.models.MediaMessage;
import com.cometchat.pro.models.TextMessage;
import com.cometchat.pro.models.TypingIndicator;
import com.example.cometimplementation.Interfaces.CallBackUnreadMessageCount;
import com.example.cometimplementation.Interfaces.ConversationsListener;
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


public class RecentChatFragment extends Fragment implements CallBackUnreadMessageCount, ConversationsListener {
    private List<Conversation> conversationList = new ArrayList<>();
    private RecyclerView user_conversation_recyclerView;
    private ConversationAdapter recyclerAdapter;
    private TextView message;
    private ConversationsRequest conversationsRequest;
    private String listenerID = "RecentChatFragment.java";

    LinearLayoutManager linearLayoutManager;
    boolean isScrolling = false;

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
//        if(conversationList.size()<1){
//            fetchData(false);
//        }

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
        conversationsRequest = new ConversationsRequest.ConversationsRequestBuilder()
                .setLimit(10)
                .setConversationType(CometChatConstants.CONVERSATION_TYPE_USER)
                .build();
        fetchData(false);
        Log.d("hey_check_lifecycle", "onStart: ");
        super.onStart();
    }


    private void initViews(View view) {

        user_conversation_recyclerView = view.findViewById(R.id.recyclerView);
        message = view.findViewById(R.id.message);
        linearLayoutManager = new LinearLayoutManager(getContext());
//        conversationsRequest = new ConversationsRequest.ConversationsRequestBuilder()
//                .setLimit(10)
//                .setConversationType(CometChatConstants.CONVERSATION_TYPE_USER)
//                .build();
        user_conversation_recyclerView.addOnScrollListener(onScrollListener);
        setUsersRecycler();


    }

    private RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);

            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                isScrolling = true;
            }


        }

        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            if (!recyclerView.canScrollVertically(1)) {
                fetchData(true);
            }


        }
    };

    private void fetchData(boolean b) {
        ApiCalls.getConversations(getContext(), conversationsRequest, this, b);
    }

    private void fetchDataOnStart(boolean b) {
       ConversationsRequest conversationsRequest = new ConversationsRequest.ConversationsRequestBuilder()
                .setLimit(10)
                .setConversationType(CometChatConstants.CONVERSATION_TYPE_USER)
                .build();
        ApiCalls.getConversations(getContext(), conversationsRequest, this, b);
    }

    private void setUsersRecycler() {

        user_conversation_recyclerView.setLayoutManager(linearLayoutManager);
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

    @Override
    public void onGetConversationsSuccess(List<Conversation> conversations, boolean isScrolling) {
        if (!isScrolling)
            conversationList.clear();

        conversationList.addAll(conversations);
        recyclerAdapter.notifyDataSetChanged();

    }

    @Override
    public void onGetConversationsError(CometChatException e) {

    }
}