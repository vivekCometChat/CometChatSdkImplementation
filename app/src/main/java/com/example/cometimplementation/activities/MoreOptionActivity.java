package com.example.cometimplementation.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.Group;
import com.example.cometimplementation.Interfaces.BlockUnBlockUserCallBackListener;
import com.example.cometimplementation.Interfaces.DeleteConversationCallBack;
import com.example.cometimplementation.Interfaces.FetchGroupCallBack;
import com.example.cometimplementation.Interfaces.SuccessMessageCallBack;
import com.example.cometimplementation.R;
import com.example.cometimplementation.utilities.ApiCalls;
import com.example.cometimplementation.utilities.SharedPrefData;
import com.example.cometimplementation.utilities.Utilities;
import com.google.android.material.snackbar.Snackbar;

import java.util.Arrays;
import java.util.HashMap;

public class MoreOptionActivity extends AppCompatActivity implements View.OnClickListener, BlockUnBlockUserCallBackListener, DeleteConversationCallBack, SuccessMessageCallBack, FetchGroupCallBack {
    String id = "", name = "";
    boolean isGroup;
    LinearLayout view_profile, clear_conversation, report, block, leave_group, delete_group;
    View view;
    TextView toolbar_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_option);
        initViews();
    }

    private void initViews() {
        view = getWindow().getDecorView().findViewById(android.R.id.content);
        id = getIntent().getStringExtra("uid");
        name = getIntent().getStringExtra("name");
        isGroup = getIntent().getBooleanExtra("isGroup", false);
        view_profile = findViewById(R.id.view_profile);
        clear_conversation = findViewById(R.id.clear_conversation);
        report = findViewById(R.id.report);
        block = findViewById(R.id.block);
        toolbar_name = findViewById(R.id.toolbar_name);
        leave_group = findViewById(R.id.leave_group);
        delete_group = findViewById(R.id.delete_group);
        hideUnHide();
        block.setOnClickListener(this);
        report.setOnClickListener(this);
        clear_conversation.setOnClickListener(this);
        view_profile.setOnClickListener(this);
        toolbar_name.setText(name);
    }

    public void back(View view) {
        onBackPressed();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.view_profile:
                Intent i = new Intent(this, UserDatailScreen.class);
                i.putExtra("uid", id);
                i.putExtra("isGroup", isGroup);
                startActivity(i);
                break;
            case R.id.clear_conversation:
                clearConversation();
                break;
            case R.id.report:
                Utilities.showSnackBarMessage(view, "this Feature is coming soon!");
                break;
            case R.id.block:
                blockUser();
                break;
            case R.id.leave_group:
                leaveGroup();
                break;
            case R.id.delete_group:
                deleteGroup();
                break;

        }

    }
    private void hideUnHide(){
        if(!isGroup){
            leave_group.setVisibility(View.GONE);
            delete_group.setVisibility(View.GONE);
            block.setVisibility(View.VISIBLE);
        }else{
            block.setVisibility(View.GONE);
            ApiCalls.getGroupDetails(this,id,this);
        }
    }

    private void leaveGroup() {
        Snackbar snackbar = Snackbar.make(view, "Are you sure you want to Leave " + name, Snackbar.LENGTH_LONG);
        snackbar.setDuration(4000);
        snackbar.setAction("Leave", view -> {
            ApiCalls.leaveGroup(this, id, this);

        });
        snackbar.show();
    }

    private void deleteGroup() {

        Snackbar snackbar = Snackbar.make(view, "Are you sure you want to Delete " + name, Snackbar.LENGTH_LONG);
        snackbar.setDuration(4000);
        snackbar.setAction("Delete", view -> {
            ApiCalls.deleteGroup(this, id, this);

        });
        snackbar.show();

    }

    private void blockUser() {
        ApiCalls.blockUser(this, Arrays.asList(id), this);
    }

    private void clearConversation() {

        Snackbar snackbar = Snackbar.make(view, "Are you sure you want to Delete Conversation with " + name, Snackbar.LENGTH_LONG);
        snackbar.setDuration(4000);
        snackbar.setAction("Delete", view -> {
            if (!isGroup)
                ApiCalls.deleteConversationWithUser(this, id, this, 0);
            else if (isGroup)
                ApiCalls.deleteConversationWithGroup(this, id, this, 0);

        });
        snackbar.show();
    }


    @Override
    public void onUserBlockUnBlockSuccess(HashMap<String, String> resultMap) {
        Utilities.showSnackBarMessage(view, resultMap.get(id));
    }

    @Override
    public void onUserBlockUnBlockError(CometChatException e) {

    }

    @Override
    public void onConversationDeleteSuccess(String s, int position) {
        Utilities.showSnackBarMessage(view, s);
        finishAffinity();
        startActivity(new Intent(this, MainActivity.class));
    }

    @Override
    public void onConversationDeleteError(CometChatException e, int position) {

    }

    @Override
    public void onActionSuccess(String successMessage) {
        if ("Group left successfully.".equalsIgnoreCase(successMessage)) {
            startActivity(new Intent(this, MainActivity.class));
            Utilities.showSnackBarMessage(view, successMessage);
            finishAffinity();
        } else if ("Group deleted successfully.".equalsIgnoreCase(successMessage)) {
            startActivity(new Intent(this, MainActivity.class));
            Utilities.showSnackBarMessage(view, successMessage);
            finishAffinity();
        }
    }

    @Override
    public void onGroupFetchedSuccess(Group group) {
        if(group.getOwner().equals(SharedPrefData.getUserId(this))){
            delete_group.setVisibility(View.VISIBLE);
            leave_group.setVisibility(View.GONE);
        }else{
            delete_group.setVisibility(View.GONE);
            leave_group.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onError(CometChatException e) {

    }
}