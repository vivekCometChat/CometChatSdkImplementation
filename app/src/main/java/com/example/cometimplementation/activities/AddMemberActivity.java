package com.example.cometimplementation.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cometchat.pro.constants.CometChatConstants;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.Conversation;
import com.cometchat.pro.models.GroupMember;
import com.cometchat.pro.models.User;
import com.example.cometimplementation.Interfaces.OnMembersAddedCallBack;
import com.example.cometimplementation.R;
import com.example.cometimplementation.adapter.AddNewMembersAdapter;
import com.example.cometimplementation.models.UserPojo;
import com.example.cometimplementation.utilities.ApiCalls;
import com.example.cometimplementation.utilities.SharedPrefData;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class AddMemberActivity extends AppCompatActivity implements OnMembersAddedCallBack {
    List<String> added_users = new ArrayList<>();
    List<UserPojo> userPojoList = new ArrayList<>();
    private List<UserPojo> userPojoMembers = new ArrayList<>();
    String GUID="";
    private RecyclerView recyclerView;
    LinearLayout group_members;
    AddNewMembersAdapter adapter;
    List<GroupMember> groupMembers=new ArrayList<>();
    View view;
    HorizontalScrollView scroll_view;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_member);
        view = getWindow().getDecorView().findViewById(android.R.id.content);
        initView();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_member_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.add_member) {
            callAddMemberApi();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void callAddMemberApi() {
        for(int i=0;i<userPojoMembers.size();i++){
            groupMembers.add(new GroupMember(userPojoMembers.get(i).getNumber(), CometChatConstants.SCOPE_MODERATOR));
        }
        if(groupMembers.size()>0) {
            ApiCalls.addMembersInGroup(this, GUID, groupMembers, this);
        }else {
            showMessage(view,"No one is added");
        }

    }

    private void initView() {
        added_users = (ArrayList<String>) getIntent().getSerializableExtra("added_users");
        GUID=getIntent().getStringExtra("GUID");
        recyclerView = findViewById(R.id.recyclerView);
        scroll_view = findViewById(R.id.scroll_view);
        group_members = findViewById(R.id.group_members);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
        setRecyclerView();
        filterData();

    }
    ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            return false;
        }
        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
            int position = viewHolder.getAdapterPosition();
            addMembers(position);
            adapter.notifyDataSetChanged();


        }

    };

    private void addMembers(int position) {
        if (!userPojoMembers.contains(userPojoList.get(position))) {
            View view = getLayoutInflater().inflate(R.layout.horizontal_users_row, null, false);
            ShapeableImageView profile = view.findViewById(R.id.image);
            ImageView cancel = view.findViewById(R.id.removeItem);
            TextView name = view.findViewById(R.id.name);
            name.setMinWidth(250);
            TextView uid = view.findViewById(R.id.uid);
            name.setText(userPojoList.get(position).getName());
            uid.setText(userPojoList.get(position).getNumber());
            Picasso.get().load(userPojoList.get(position).getImg_url()).into(profile);
            cancel.setOnClickListener(v -> {
                removeMember(view, group_members.indexOfChild(view));
            });
            group_members.addView(view);
            userPojoMembers.add(userPojoList.get(position));
            scroll_view.setVisibility(View.VISIBLE);
        } else {
            showMessage(view,"member is already Added");

        }
    }
    private void removeMember(View view, int i) {
        group_members.removeView(view);
        userPojoMembers.remove(i);
        if (group_members.getChildCount() < 1)
            scroll_view.setVisibility(View.GONE);
    }
    private void setRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AddNewMembersAdapter(this, userPojoList);
        recyclerView.setAdapter(adapter);
    }

    private void filterData() {
        if (SharedPrefData.getCommonUser(this) != null && SharedPrefData.getCommonUser(this).size() > 0) {

            List<UserPojo> userPojos = SharedPrefData.getCommonUser(this);
            for (int i = 0; i < userPojos.size(); i++) {
                for (int j = 0; j < added_users.size(); j++) {
                    if (added_users.get(j).equals(userPojos.get(i).getNumber())) {
                        userPojos.remove(i);
                    }
                }

            }
            userPojoList.addAll(userPojos);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onMembersAddedSuccess(HashMap<String, String> successMap) {
        finish();
    }

    @Override
    public void onError(CometChatException e) {
        e.printStackTrace();
    }

    private void showMessage(View view,String message){
        Snackbar snackbar=Snackbar.make(view,message, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

}