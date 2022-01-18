package com.example.cometimplementation.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cometchat.pro.constants.CometChatConstants;
import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.core.ConversationsRequest;
import com.cometchat.pro.core.MessagesRequest;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.helpers.CometChatHelper;
import com.cometchat.pro.models.BaseMessage;
import com.cometchat.pro.models.Conversation;
import com.cometchat.pro.models.CustomMessage;
import com.cometchat.pro.models.Group;
import com.cometchat.pro.models.MediaMessage;
import com.cometchat.pro.models.TextMessage;
import com.cometchat.pro.models.TypingIndicator;
import com.cometchat.pro.models.User;
import com.example.cometimplementation.Interfaces.CallBackUnreadMessageCount;
import com.example.cometimplementation.Interfaces.ConversationsListener;
import com.example.cometimplementation.Interfaces.DeleteConversationCallBack;
import com.example.cometimplementation.R;
import com.example.cometimplementation.activities.BlockedUserActivity;
import com.example.cometimplementation.adapter.ConversationAdapter;
import com.example.cometimplementation.adapter.RecyclerAdapter;
import com.example.cometimplementation.models.UserPojo;
import com.example.cometimplementation.utilities.ApiCalls;
import com.example.cometimplementation.utilities.SharedPrefData;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;


public class RecentChatFragment extends Fragment implements CallBackUnreadMessageCount, ConversationsListener, DeleteConversationCallBack {
    private List<Conversation> conversationList = new ArrayList<>();
    private RecyclerView user_conversation_recyclerView;
    private ConversationAdapter recyclerAdapter;
    private TextView message;
    private ConversationsRequest conversationsRequest;
    private String listenerID = "RecentChatFragment.java";

    LinearLayoutManager linearLayoutManager;
    boolean isScrolling = false;
    View v;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recent_chat, container, false);
        v = view;
        Log.d("hey_check_lifecycle", "onCreateView: ");

        initViews(view);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(user_conversation_recyclerView);
        setHasOptionsMenu(true);

        return view;
    }

    ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
            int position = viewHolder.getAdapterPosition();
            Conversation conversation = conversationList.get(position);


            if (conversation.getConversationType().equals("user")) {
                User receiverUser = (User) conversation.getLastMessage().getReceiver();
                if (!conversation.getLastMessage().getSender().getUid().equals(SharedPrefData.getUserId(getContext()))) {
                    deleteConversation(conversation.getLastMessage().getSender().getName(), conversation.getLastMessage().getSender().getUid(), position, false);
                } else {
                    deleteConversation(receiverUser.getName(), receiverUser.getUid(), position, false);
                }
            } else if (conversation.getConversationType().equals("group")) {
                Group group = (Group) conversation.getLastMessage().getReceiver();
                deleteConversation(group.getName(), group.getGuid(), position, true);
            }
        }

    };

    @SuppressLint("ResourceAsColor")
    private void deleteConversation(String name, String uid, int position, boolean isGroup) {
        Snackbar snackbar = Snackbar.make(v, "Are you sure you want to Delete Conversation with " + name, Snackbar.LENGTH_LONG);
        snackbar.setDuration(4000);
        snackbar.setAction("Delete", view -> {
            if (!isGroup)
                ApiCalls.deleteConversationWithUser(getContext(), uid, this, position);
            else if (isGroup)
                ApiCalls.deleteConversationWithGroup(getContext(), uid, this, position);

        });
        recyclerAdapter.notifyDataSetChanged();
        snackbar.show();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.action_bar_profile_menu, menu);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onResume() {
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
                .build();
        fetchData(false);
        Log.d("hey_check_lifecycle", "onStart: ");
        super.onStart();
    }


    private void initViews(View view) {

        user_conversation_recyclerView = view.findViewById(R.id.recyclerView);
        message = view.findViewById(R.id.message);
        linearLayoutManager = new LinearLayoutManager(getContext());

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


    private void setUsersRecycler() {

        user_conversation_recyclerView.setLayoutManager(linearLayoutManager);
        recyclerAdapter = new ConversationAdapter(conversationList, getActivity(), false);
        user_conversation_recyclerView.setAdapter(recyclerAdapter);

    }


    public void toGetUnreadMessageCount(BaseMessage message) {
        int pos = -1;
        Conversation conversation = CometChatHelper.getConversationFromMessage(message);


        if (conversationList.contains(conversation)) {
            pos = conversationList.indexOf(conversation);
            conversationList.remove(pos);
        }
        conversationList.add(0, conversation);

        if (pos != -1 && pos != 0) {
            recyclerAdapter.notifyItemMoved(pos, 0);
        }
        if (conversation.getConversationType().equals("user"))
            ApiCalls.getUnreadMessageCountForAllUsers(getContext(), conversation, this);
        else if (conversation.getConversationType().equals("group"))
            ApiCalls.getUnreadMessageCountForGroup(getContext(), conversation, this);

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

    @Override
    public void onConversationDeleteSuccess(String s, int position) {

        conversationList.remove(position);
        recyclerAdapter.notifyItemRemoved(position);
        Snackbar snackbar = Snackbar.make(v, "Conversation Deleted Successfully", Snackbar.LENGTH_LONG);
        snackbar.setDuration(4000);
        snackbar.show();


    }

    @Override
    public void onConversationDeleteError(CometChatException e, int position) {
        recyclerAdapter.notifyDataSetChanged();
    }
}