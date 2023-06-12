package com.example.cometimplementation.activities;

import static com.example.cometimplementation.utilities.SharedPrefData.getCommonUser;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.User;
import com.example.cometimplementation.Interfaces.FetchUserCallBack;
import com.example.cometimplementation.Interfaces.UserListeners;
import com.example.cometimplementation.R;
import com.example.cometimplementation.adapter.OnlineUserAdapter;
import com.example.cometimplementation.models.UserPojo;
import com.example.cometimplementation.utilities.ApiCalls;
import com.example.cometimplementation.utilities.SharedPrefData;

import java.util.ArrayList;
import java.util.List;


public class OnlineUsersActivity extends AppCompatActivity implements UserListeners, FetchUserCallBack {

    RecyclerView online_users;
    private String listenerID = "OnlineUsersActivity";
    List<User> userList=new ArrayList<>();
    List<String> uid_list=new ArrayList<>();
    OnlineUserAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_users);
        initViews();
        ApiCalls.getUsersById(this,uid_list,this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        CometChat.removeUserListener(listenerID);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ApiCalls.getUserActiveStatus(this,listenerID,this);

    }

    private void initViews() {
        getSupportActionBar().setTitle("Online Users");
        online_users=findViewById(R.id.online_users);
        setRecyclerView();
    }

    private void setRecyclerView() {
        uid_list.clear();
        online_users.setLayoutManager(new LinearLayoutManager(this));
        adapter=new OnlineUserAdapter(userList,this);
        online_users.setAdapter(adapter);
        if(getCommonUser(this)!=null && getCommonUser(this).size()>0){
            for(UserPojo list:SharedPrefData.getCommonUser(this)){
                uid_list.add(list.getNumber());
            }
        }

    }


    @Override
    public void onSuccess(User user) {

        if(userList.contains(user)){
            int index=userList.indexOf(user);
            userList.remove(index);
            userList.add(index,user);
            adapter.notifyItemChanged(userList.indexOf(user));
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onSuccess(List<User> list) {
        userList.addAll(list);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onError(CometChatException e) {

    }
}