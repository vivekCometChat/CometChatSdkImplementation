package com.example.cometimplementation.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cometchat.pro.core.BlockedUsersRequest;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.TextMessage;
import com.cometchat.pro.models.User;
import com.example.cometimplementation.Interfaces.BlockUnBlockUserCallBackListener;
import com.example.cometimplementation.Interfaces.FetchUserCallBack;
import com.example.cometimplementation.R;
import com.example.cometimplementation.adapter.BlockUserAdapter;
import com.example.cometimplementation.utilities.ApiCalls;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import de.measite.minidns.record.A;

public class BlockedUserActivity extends AppCompatActivity implements FetchUserCallBack, BlockUnBlockUserCallBackListener {
    RecyclerView blocked_users;
    BlockUserAdapter blockUserAdapter;
    List<User> userList = new ArrayList<>();
    TextView message;
    LinearLayoutManager linearLayoutManager;
    int pos = -1;
    BlockedUsersRequest blockedUsersRequest;
    boolean isScrolling=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blocked_user);
        initViews();

    }

    private void initViews() {
        getSupportActionBar().setTitle("Blocked Users");
        blocked_users = findViewById(R.id.blocked_users);
        linearLayoutManager = new LinearLayoutManager(this);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(blocked_users);
        message = findViewById(R.id.message);
        setRecyclerView();

    }

    private RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);

        }
        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            if (!recyclerView.canScrollVertically(1)) {
                isScrolling=false;
                getBlockedUsers(blockedUsersRequest);
            }


        }
    };
    ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            Toast.makeText(BlockedUserActivity.this, "on Move", Toast.LENGTH_SHORT).show();
            return false;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
            int position = viewHolder.getAdapterPosition();
            unBlockUser(position);
        }

    };

    private void unBlockUser(int position) {
        pos = position;
        ApiCalls.unBlockUsers(this, Arrays.asList(userList.get(position).getUid()), this);
    }


    private void setRecyclerView() {

        blocked_users.setLayoutManager(linearLayoutManager);
        blockUserAdapter = new BlockUserAdapter(userList, this);
        blocked_users.setAdapter(blockUserAdapter);

    }

    @Override
    protected void onStart() {
        super.onStart();
        blockedUsersRequest = new BlockedUsersRequest.BlockedUsersRequestBuilder().setLimit(10)
                .setDirection(BlockedUsersRequest.DIRECTION_BLOCKED_BY_ME)
                .build();
        getBlockedUsers(blockedUsersRequest);
    }
    private void getBlockedUsers(BlockedUsersRequest blockedUsersRequest ){
        ApiCalls.getBlockedUsersByMe(this, this,blockedUsersRequest);

    }

    @Override
    public void onSuccess(List<User> list) {
        if(!isScrolling){
            userList.clear();
        }else{
            isScrolling=false;
        }

        if (list.size() < 1) {
            blocked_users.setVisibility(View.GONE);
            message.setVisibility(View.VISIBLE);
            message.setText("You have not Blocked anyone yet!");
        } else {
            blocked_users.setVisibility(View.VISIBLE);
            message.setVisibility(View.GONE);
            userList.clear();
            userList.addAll(list);
            blockUserAdapter.notifyDataSetChanged();
        }

    }

    @Override
    public void onError(CometChatException e) {

    }

    @Override
    public void onUserBlockUnBlockSuccess(HashMap<String, String> resultMap) {

        if (pos != -1) {
            userList.remove(pos);
            blockUserAdapter.notifyItemRemoved(pos);
            pos = -1;
            toUpdateUi();
        }else{
            blockUserAdapter.notifyDataSetChanged();
        }

    }

    @Override
    public void onUserBlockUnBlockError(CometChatException e) {
        blockUserAdapter.notifyDataSetChanged();

    }

    private void toUpdateUi(){
        if (userList.size() < 1) {
            blocked_users.setVisibility(View.GONE);
            message.setVisibility(View.VISIBLE);
            message.setText("You have not Blocked anyone yet!");
        } else {
            blocked_users.setVisibility(View.VISIBLE);
            message.setVisibility(View.GONE);

        }

    }

}