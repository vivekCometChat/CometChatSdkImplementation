package com.example.cometimplementation.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.cometchat.pro.constants.CometChatConstants;
import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.core.GroupMembersRequest;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.Group;
import com.cometchat.pro.models.GroupMember;
import com.cometchat.pro.models.User;
import com.example.cometimplementation.Interfaces.BlockUnBlockUserCallBackListener;
import com.example.cometimplementation.Interfaces.FetchGroupCallBack;
import com.example.cometimplementation.Interfaces.FetchGroupMemberList;
import com.example.cometimplementation.Interfaces.SuccessMessageCallBack;
import com.example.cometimplementation.Interfaces.UserListeners;
import com.example.cometimplementation.R;
import com.example.cometimplementation.adapter.MemberListAdapter;
import com.example.cometimplementation.utilities.ApiCalls;
import com.example.cometimplementation.utilities.SharedPrefData;
import com.example.cometimplementation.utilities.Utilities;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserDetailActivity extends AppCompatActivity implements AppBarLayout.OnOffsetChangedListener, UserListeners, View.OnClickListener, BlockUnBlockUserCallBackListener, FetchGroupCallBack, FetchGroupMemberList, SuccessMessageCallBack {
    Toolbar toolbar;
    LinearLayout collapsing_layout, add_members;
    TextView status, user_name, user_number, active_status, toolbar_name, block_text, unblock_text, report_text, delete_text, leave_text;
    FloatingActionButton chat;
    LinearLayout block, report, unblock, leave_group, delete_group;
    ShapeableImageView user_avatar, toolbar_profile;
    CardView block_report_card, group_delete_leave_card;
    ImageView edit;
    String uid = "";
    private String listenerID = "UserDetailActivity";
    boolean isBlocked = false, isGroup;
    RecyclerView group_member;
    GroupMembersRequest groupMembersRequest;
    List<GroupMember> memberList = new ArrayList<>();
    boolean isScrolled = false;
    MemberListAdapter memberListAdapter;
    List<String> added_users = new ArrayList<>();
    View view;
    int i = -1;
    String group_type = "";
    Group groupChashed;
    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);
        initViews();


    }

    private void initViews() {
        view = getWindow().getDecorView().findViewById(android.R.id.content);
        uid = getIntent().getStringExtra("uid");
        isGroup = getIntent().getBooleanExtra("isGroup", false);
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appBarLayout);
        appBarLayout.addOnOffsetChangedListener(this);
        toolbar = findViewById(R.id.toolbar);
        add_members = findViewById(R.id.add_members);
        block_text = findViewById(R.id.block_text);
        edit = findViewById(R.id.edit);
        dialog = new Dialog(this);


        block_report_card = findViewById(R.id.block_report_card);
        delete_text = findViewById(R.id.delete_text);
        leave_text = findViewById(R.id.leave_text);
        group_delete_leave_card = findViewById(R.id.group_delete_leave_card);
        group_member = findViewById(R.id.group_member);
        delete_group = findViewById(R.id.delete_group);
        leave_group = findViewById(R.id.leave_group);
        unblock_text = findViewById(R.id.unblock_text);
        report_text = findViewById(R.id.report_text);
        toolbar_name = findViewById(R.id.toolbar_name);
        toolbar_profile = findViewById(R.id.toolbar_profile);
        collapsing_layout = findViewById(R.id.collapsing_layout);
        user_name = findViewById(R.id.user_name);
        user_number = findViewById(R.id.user_number);
        status = findViewById(R.id.status);
        active_status = findViewById(R.id.active_status);
        chat = findViewById(R.id.chat);
        block = findViewById(R.id.block);
        report = findViewById(R.id.report);
        unblock = findViewById(R.id.unblock);
        user_avatar = findViewById(R.id.user_avatar);

        chat.setOnClickListener(this);
        block.setOnClickListener(this);
        unblock.setOnClickListener(this);
        leave_group.setOnClickListener(this);
        delete_group.setOnClickListener(this);
        add_members.setOnClickListener(this);
        edit.setOnClickListener(this);
        add_members.setVisibility(View.GONE);
        toolbar.setTitle("Vivek");
        setSupportActionBar(toolbar);


        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        if (!uid.isEmpty() && !uid.equals("null")) {
            if (!isGroup) {
                group_member.setVisibility(View.GONE);
                edit.setVisibility(View.GONE);
                group_delete_leave_card.setVisibility(View.GONE);
                block_report_card.setVisibility(View.VISIBLE);
                active_status.setVisibility(View.VISIBLE);
                ApiCalls.getUserDetailsById(this, uid, this);
            } else {
                setMembersRecycler();
                group_member.setVisibility(View.VISIBLE);
                block_report_card.setVisibility(View.GONE);
                active_status.setVisibility(View.GONE);
                edit.setVisibility(View.GONE);
                group_delete_leave_card.setVisibility(View.VISIBLE);
                ApiCalls.getGroupDetails(this, uid, this);
            }
        }

    }


    ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
            int position = viewHolder.getAdapterPosition();
            i = position;
            removeMember(position);
            memberListAdapter.notifyDataSetChanged();

        }

    };

    private void removeMember(int position) {

        Snackbar snackbar = Snackbar.make(view, "Are you sure you want to kick " + memberList.get(position).getName(), Snackbar.LENGTH_LONG);
        snackbar.setDuration(5000);
        snackbar.setAction("Kick", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeMemberApi(memberList.get(position).getUid());
            }
        });
        snackbar.show();

    }

    private void removeMemberApi(String uid_member) {

        ApiCalls.removeGroupMember(this, uid_member, uid, this);

    }


    private void setMembersRecycler() {
        group_member.setLayoutManager(new LinearLayoutManager(this));
        memberListAdapter = new MemberListAdapter(this, memberList);
        group_member.setAdapter(memberListAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        Log.d("check_offset", "onOffsetChanged: " + verticalOffset + "  " + appBarLayout.getTotalScrollRange());
        if (verticalOffset == 0) {
            toolbar.setVisibility(View.GONE);
            collapsing_layout.setVisibility(View.VISIBLE);
            // Fully expanded
        } else {
            if (verticalOffset < (150 - appBarLayout.getTotalScrollRange()) || verticalOffset == -appBarLayout.getTotalScrollRange()) {
                toolbar.setVisibility(View.VISIBLE);
                collapsing_layout.setVisibility(View.GONE);
            } else {
                toolbar.setVisibility(View.GONE);
                collapsing_layout.setVisibility(View.VISIBLE);
            }
            // Not fully expanded or collapsed
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (isGroup) {
            groupMembersRequest = new GroupMembersRequest.GroupMembersRequestBuilder(uid).setLimit(30).build();
            ApiCalls.getGroupMembersList(this, groupMembersRequest, this);
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        CometChat.removeUserListener(listenerID);

    }

    @Override
    protected void onResume() {
        super.onResume();
        ApiCalls.getUserActiveStatus(this, listenerID, this);

    }

    @Override
    public void onSuccess(User user) {
        setUserData(user);

    }

    @Override
    public void onGroupFetchedSuccess(Group group) {
        groupChashed = group;
        group_type = "";
        setGroupData(group);
    }

    private void setGroupData(Group group) {
        Log.e("setGroupData", "setGroupData: " + group.getOwner());
        Picasso.get().load(group.getIcon()).into(user_avatar);
        Picasso.get().load(group.getIcon()).into(toolbar_profile);
        user_name.setText(group.getName());
        toolbar_name.setText(group.getName());
        user_number.setText(group.getMembersCount() + " participants");
        status.setText(group.getDescription() == null ? "Hey there! I am using CometChat" : group.getDescription());
        report_text.setText("Report " + group.getName());
        leave_text.setText("Leave "+group.getName());
        delete_text.setText("Delete "+group.getName());
        if (group.getOwner().equals(SharedPrefData.getUserId(this))) {
            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
            itemTouchHelper.attachToRecyclerView(group_member);
            add_members.setVisibility(View.VISIBLE);
            edit.setVisibility(View.VISIBLE);
            delete_group.setVisibility(View.VISIBLE);
            leave_group.setVisibility(View.GONE);
        } else {
            add_members.setVisibility(View.GONE);
            edit.setVisibility(View.GONE);
            delete_group.setVisibility(View.GONE);
            leave_group.setVisibility(View.VISIBLE);
        }
        dialog.dismiss();

    }

    private void setUserData(User user) {

        Picasso.get().load(user.getAvatar()).into(user_avatar);
        Picasso.get().load(user.getAvatar()).into(toolbar_profile);
        user_name.setText(user.getName());
        toolbar_name.setText(user.getName());
        user_number.setText("+91 " + user.getUid());
        status.setText(user.getStatusMessage() == null ? "Hey there! I am using CometChat" : user.getStatusMessage());
        block_text.setText("Block " + user.getName());
        unblock_text.setText("Unblock " + user.getName());
        report_text.setText("Report " + user.getName());
        if (user.getStatus().equals(CometChatConstants.USER_STATUS_ONLINE))
            active_status.setText("online");
        else
            active_status.setText("last seen at " + Utilities.convertMillisToTime(user.getLastActiveAt()));

        if (user.isBlockedByMe()) {
            block.setVisibility(View.GONE);
            unblock.setVisibility(View.VISIBLE);
            chat.setVisibility(View.GONE);
            isBlocked = true;
        } else {
            block.setVisibility(View.VISIBLE);
            unblock.setVisibility(View.GONE);
            chat.setVisibility(View.VISIBLE);
            isBlocked = false;
        }

    }


    @Override
    public void onGroupMemberListSuccess(List<GroupMember> list) {
        if (!isScrolled) {
            added_users.clear();
            memberList.clear();
            memberList.addAll(list);
            memberListAdapter.notifyDataSetChanged();

            for (int i = 0; i < list.size(); i++) {
                added_users.add(list.get(i).getUid());
            }
        }
    }

    @Override
    public void onActionSuccess(String successMessage) {
        if ("Group left successfully.".equalsIgnoreCase(successMessage)) {
            startActivity(new Intent(this, MainActivity.class));
            showMessageOnSnackBar(view, successMessage);
            finishAffinity();
        } else if ("Group deleted successfully.".equalsIgnoreCase(successMessage)) {
            startActivity(new Intent(this, MainActivity.class));
            showMessageOnSnackBar(view, successMessage);
            finishAffinity();
        } else {
            if (i != -1) {
                memberList.remove(i);
                memberListAdapter.notifyItemRemoved(i);
                i = -1;
            }
        }
        Log.e("show_success_message", "onActionSuccess: "+successMessage);
    }

    @Override
    public void onError(CometChatException e) {
        e.printStackTrace();
        Log.e("show_success_message", "onActionSuccess: " + e.getMessage());

        memberListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.chat:
                onBackPressed();
                break;
            case R.id.edit:
                editGroupInfo();
                break;
            case R.id.block:
                blockUser();
                break;
            case R.id.unblock:
                unBlockUser();
                break;
            case R.id.delete_group:
                Snackbar snackbar = Snackbar.make(view, "Are you sure you want to delete " + groupChashed.getName(), Snackbar.LENGTH_LONG);
                snackbar.setDuration(5000);
                snackbar.setAction("Delete", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        deleteGroup();

                    }
                });
                snackbar.show();
                break;
            case R.id.leave_group:
                Snackbar snackbar2 = Snackbar.make(view, "Are you sure you want to Leave " + groupChashed.getName(), Snackbar.LENGTH_LONG);
                snackbar2.setDuration(5000);
                snackbar2.setAction("Leave", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        leaveGroup();
                    }
                });
                snackbar2.show();
                break;
            case R.id.add_members:
                Intent i = new Intent(this, AddMemberActivity.class);
                i.putExtra("added_users", (Serializable) added_users);
                i.putExtra("GUID", uid);
                startActivity(i);
                break;
        }

    }

    private void leaveGroup() {
        ApiCalls.leaveGroup(this, uid, this);
    }

    private void deleteGroup() {
        ApiCalls.deleteGroup(this, uid, this);
    }

    private void editGroupInfo() {
        dialog.setContentView(R.layout.update_group_dialog_layout);
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        CircleImageView imageView = dialog.findViewById(R.id.profile_pic);
        EditText user_name = dialog.findViewById(R.id.user_name);
        EditText about = dialog.findViewById(R.id.about);
        FloatingActionButton editAvatar = dialog.findViewById(R.id.choose_image);
        LinearLayout editGroupName = dialog.findViewById(R.id.edit_name);
        LinearLayout editDescription = dialog.findViewById(R.id.edit_status);
        Button update_group = dialog.findViewById(R.id.update_group);
        RadioGroup radioGroup = dialog.findViewById(R.id.radioGroup);
        TextInputLayout password_input = dialog.findViewById(R.id.password_input);
        user_name.setText(groupChashed.getName());
        about.setText(groupChashed.getDescription());

        update_group.setOnClickListener(v -> {
            if (!user_name.getText().toString().trim().isEmpty() && !group_type.isEmpty() && !uid.isEmpty()) {
                if (group_type.equals(CometChatConstants.GROUP_TYPE_PASSWORD)) {
                    if (!password_input.getEditText().getText().toString().trim().isEmpty()) {

                        ApiCalls.updateGroupInfo(this, uid, user_name.getText().toString().trim(), group_type, password_input.getEditText().getText().toString().trim(), this);
                    } else {
                        password_input.getEditText().setError("Enter password");
                    }
                } else {
                    if (group_type.equals(CometChatConstants.GROUP_TYPE_PUBLIC)) {

                        ApiCalls.updateGroupInfo(this, uid, user_name.getText().toString().trim(), group_type, "", this);

                    } else if (group_type.equals(CometChatConstants.GROUP_TYPE_PRIVATE)) {
                        ApiCalls.updateGroupInfo(this, uid, user_name.getText().toString().trim(), group_type, "", this);

                    }
                }
            } else {
                if (user_name.getText().toString().isEmpty()) {
                    showMessageOnSnackBar(view, "Name field Can't be Empty!");
                } else if (group_type.isEmpty()) {

                    showMessageOnSnackBar(view, "Please select group type!");
                } else if (uid.isEmpty()) {
                    showMessageOnSnackBar(view, "Failed to create group id!");
                }
            }


        });

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton radioButton = radioGroup.findViewById(i);
                if (radioButton.getText().equals("Public")) {
                    password_input.setVisibility(View.GONE);
                    group_type = CometChatConstants.GROUP_TYPE_PUBLIC;
                } else if (radioButton.getText().equals("Private")) {
                    password_input.setVisibility(View.GONE);
                    group_type = CometChatConstants.GROUP_TYPE_PRIVATE;
                } else if (radioButton.getText().equals("Password")) {
                    password_input.setVisibility(View.VISIBLE);
                    group_type = CometChatConstants.GROUP_TYPE_PASSWORD;
                }
            }
        });
        dialog.show();

    }

    private void showMessageOnSnackBar(View view, String message) {
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    private void blockUser() {
        ApiCalls.blockUser(this, Arrays.asList(uid), this);
    }

    private void unBlockUser() {
        ApiCalls.unBlockUsers(this, Arrays.asList(uid), this);
    }

    @Override
    public void onUserBlockUnBlockSuccess(HashMap<String, String> resultMap) {
        if (isBlocked) {
            block.setVisibility(View.VISIBLE);
            unblock.setVisibility(View.GONE);
            isBlocked = false; //unblocked
        } else {
            block.setVisibility(View.GONE);
            unblock.setVisibility(View.VISIBLE);
            isBlocked = true; //blocked
        }
        Log.e("block_status", "onUserBlockUnBlockSuccess: " + resultMap);

    }

    @Override
    public void onUserBlockUnBlockError(CometChatException e) {
        Log.e("block_status", "onUserBlockUnBlockError: " + e.getMessage());

    }
}