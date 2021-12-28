package com.example.cometimplementation.fragments;

import android.os.Bundle;

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
import com.cometchat.pro.models.Conversation;
import com.example.cometimplementation.R;
import com.example.cometimplementation.adapter.ConversationAdapter;
import com.example.cometimplementation.adapter.RecyclerAdapter;
import com.example.cometimplementation.models.UserPojo;
import com.example.cometimplementation.utilities.ApiCalls;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;


public class RecentChatFragment extends Fragment {
    private List<Conversation> conversationList = new ArrayList<>();
    private RecyclerView user_conversation_recyclerView;
    private ConversationAdapter recyclerAdapter;
    private TextView message;
    private ConversationsRequest conversationsRequest;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_recent_chat, container, false);

        initViews(view);

        return view;
    }

    private void initViews(View view) {

        user_conversation_recyclerView = view.findViewById(R.id.recyclerView);
        message = view.findViewById(R.id.message);
        setUsersRecycler();
//        getConversations();

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
//                Log.d("see_conversations", "onSuccess: " +conversations.toString());
//                String s=conversations.toString();

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
        recyclerAdapter = new ConversationAdapter(conversationList,getActivity());
        user_conversation_recyclerView.setAdapter(recyclerAdapter);
    }


    @Override
    public void onResume() {
        super.onResume();
        getConversations();
    }
}