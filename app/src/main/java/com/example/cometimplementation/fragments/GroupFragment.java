package com.example.cometimplementation.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
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
import com.cometchat.pro.core.GroupsRequest;
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
import com.example.cometimplementation.Interfaces.FetchGroupListCallBack;
import com.example.cometimplementation.R;
import com.example.cometimplementation.adapter.ConversationAdapter;
import com.example.cometimplementation.adapter.GroupConversationAdapter;
import com.example.cometimplementation.adapter.ShowGroupAdapter;
import com.example.cometimplementation.utilities.ApiCalls;
import com.example.cometimplementation.utilities.SharedPrefData;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;


public class GroupFragment extends Fragment implements FetchGroupListCallBack {
    private List<Group> groupList = new ArrayList<>();

    private RecyclerView recyclerView_group;
    private TextView message;
    ShowGroupAdapter showGroupAdapter;
    LinearLayoutManager linearLayoutManager;
    boolean isScrolling = false;
    View v;
    GroupsRequest groupsRequest;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_group, container, false);
        v = view;


        initViews(view);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {

        super.onPause();
    }

    @Override
    public void onStart() {
        groupsRequest = new GroupsRequest.GroupsRequestBuilder().setLimit(30).build();

        fetchGroup(false);
        super.onStart();
    }

    private void initViews(View view) {

        recyclerView_group = view.findViewById(R.id.recyclerView_group);
        message = view.findViewById(R.id.message);
        linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView_group.addOnScrollListener(onScrollListener);
        setGroupRecycler();


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
                fetchGroup(true);
            }


        }
    };

    private void fetchGroup(boolean b) {
        ApiCalls.getGroups(getActivity(),groupsRequest, this, b);
    }

    private void setGroupRecycler() {
        recyclerView_group.setLayoutManager(new LinearLayoutManager(getContext()));
        showGroupAdapter = new ShowGroupAdapter(getActivity(), groupList);
        recyclerView_group.setAdapter(showGroupAdapter);
    }


    @Override
    public void onGroupFetchSuccess(List<Group> list, boolean isScrolling) {
        if (!isScrolling)
            groupList.clear();

        groupList.addAll(list);
        showGroupAdapter.notifyDataSetChanged();
    }

    @Override
    public void onError(CometChatException e) {

    }
}