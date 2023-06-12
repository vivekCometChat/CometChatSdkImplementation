package com.example.cometimplementation.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.AbsListView;

import com.cometchat.pro.core.UsersRequest;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.User;
import com.example.cometimplementation.Interfaces.FetchUserCallBack;
import com.example.cometimplementation.Interfaces.UserListeners;
import com.example.cometimplementation.R;
import com.example.cometimplementation.adapter.OnlineUserAdapter;
import com.example.cometimplementation.adapter.RecyclerAdapter;
import com.example.cometimplementation.utilities.ApiCalls;

import java.util.ArrayList;
import java.util.List;


public class FriendListActivity extends AppCompatActivity implements FetchUserCallBack, UserListeners {
    private RecyclerView recyclerView;
    boolean isScrolling = false;
    List<User> userList=new ArrayList<>();
    OnlineUserAdapter onlineUserAdapter;
    UsersRequest usersRequest;
    private String listenerID = "FriendListActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_list);
        getSupportActionBar().setTitle("Friends");
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.addOnScrollListener(onScrollListener);
        usersRequest= new UsersRequest.UsersRequestBuilder()
                .setLimit(30)
                .friendsOnly(true)
                .build();
        setUsersRecycler();

    }
    private RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);

            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
            }
        }

        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            if (!recyclerView.canScrollVertically(1)) {
                isScrolling = true;
                fetchUsers();
            }


        }
    };

    private void fetchUsers() {
        ApiCalls.fetchCometChatUsers(this,usersRequest,this);

    }

    @Override
    protected void onStart() {
        super.onStart();
        fetchUsers();
    }

    @Override
    protected void onResume() {
        super.onResume();
        ApiCalls.getUserActiveStatus(this,listenerID,this);

    }

    private void setUsersRecycler() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        onlineUserAdapter = new OnlineUserAdapter(userList, this);
        recyclerView.setAdapter(onlineUserAdapter);
    }

    @Override
    public void onSuccess(List<User> list) {
        if (!isScrolling)
            userList.clear();
        else
            isScrolling = false;

        userList.addAll(list);
        onlineUserAdapter.notifyDataSetChanged();

    }

    @Override
    public void onSuccess(User user) {
        if(userList.contains(user)){
            int index=userList.indexOf(user);
            userList.remove(index);
            userList.add(index,user);
            onlineUserAdapter.notifyItemChanged(userList.indexOf(user));
        }
    }

    @Override
    public void onError(CometChatException e) {

    }
}