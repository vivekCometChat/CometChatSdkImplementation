package com.example.cometimplementation.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.cometchat.pro.constants.CometChatConstants;
import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.BaseMessage;
import com.cometchat.pro.models.CustomMessage;
import com.cometchat.pro.models.MediaMessage;
import com.cometchat.pro.models.MessageReceipt;
import com.cometchat.pro.models.TextMessage;
import com.cometchat.pro.models.TypingIndicator;
import com.example.cometimplementation.Interfaces.MessageReadByListCallBack;
import com.example.cometimplementation.R;
import com.example.cometimplementation.adapter.ReadByUsersAdapter;
import com.example.cometimplementation.utilities.ApiCalls;
import com.example.cometimplementation.utilities.SharedPrefData;

import java.util.ArrayList;
import java.util.List;

public class MessageInfoActivity extends AppCompatActivity implements MessageReadByListCallBack {
    TextView selected_message;
    RecyclerView recyclerView;
    ReadByUsersAdapter adapter;
    List<MessageReceipt> messageReceipts_list = new ArrayList<>();
    int message_id;
    boolean isGroup;
    String uid_or_guid,message;
    private String listenerID = "MessageInfoActivity.java";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_info);

        message_id = getIntent().getIntExtra("messageId", -1);
        isGroup = getIntent().getBooleanExtra("isGroup", false);
        uid_or_guid = getIntent().getStringExtra("uid");
        message = getIntent().getStringExtra("message");

        selected_message = findViewById(R.id.message);
        selected_message.setText("   "+message+"   ");
        recyclerView = findViewById(R.id.recycler);
        setRecyclerView();
       getReadByUsers();

    }

    private void setRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ReadByUsersAdapter(this, messageReceipts_list);
        recyclerView.setAdapter(adapter);
    }

    private void getReadByUsers(){
        ApiCalls.getMessageReadByUsers(this, message_id, this);

    }
    @Override
    protected void onResume() {
        super.onResume();
        CometChat.addMessageListener(listenerID, new CometChat.MessageListener() {
            @Override
            public void onMessagesRead(MessageReceipt messageReceipt) {
                super.onMessagesRead(messageReceipt);
                getReadByUsers();
            }
        });


    }

    @Override
    protected void onPause() {
        super.onPause();
        CometChat.removeMessageListener(listenerID);
    }

    @Override
    public void onMessageReadBySuccess(List<MessageReceipt> messageReceipts) {
        messageReceipts_list.clear();
        messageReceipts_list.addAll(messageReceipts);
        adapter.notifyDataSetChanged();

    }

    @Override
    public void onError(CometChatException e) {

    }
}