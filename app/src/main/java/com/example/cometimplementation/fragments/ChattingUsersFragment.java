package com.example.cometimplementation.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
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
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cometchat.pro.core.UsersRequest;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.Group;
import com.cometchat.pro.models.User;
import com.example.cometimplementation.Interfaces.FetchGroupListCallBack;
import com.example.cometimplementation.Interfaces.FetchUserCallBack;
import com.example.cometimplementation.Interfaces.UserListeners;
import com.example.cometimplementation.R;
import com.example.cometimplementation.activities.ContactsActivity;
import com.example.cometimplementation.activities.CreateGroupActivity;
import com.example.cometimplementation.adapter.RecyclerAdapter;
import com.example.cometimplementation.adapter.ShowGroupAdapter;
import com.example.cometimplementation.models.UserPojo;
import com.example.cometimplementation.utilities.ApiCalls;
import com.example.cometimplementation.utilities.SharedPrefData;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class ChattingUsersFragment extends Fragment implements FetchUserCallBack , UserListeners {
    private List<User> userPojoList_users = new ArrayList<>();
    private List<User> userPojoList_member = new ArrayList<>();
    private List<String> uid_list_users = new ArrayList<>();
    private List<UserPojo> temp_users=new ArrayList<>();
    private RecyclerView recyclerView;
    LinearLayout group_members, create;
    HorizontalScrollView scroll;
    private RecyclerAdapter recyclerAdapter;
    private TextView message;
    private FloatingActionButton show_contacts_btn;
    View fragment_view;
    UsersRequest usersRequest;
    boolean isScrolling = false;
    private String listenerID = "OnlineUsersActivity";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chatting_users, container, false);
        fragment_view = view;
        initView(view);

        return view;

    }

    @SuppressLint("NotifyDataSetChanged")
    private void setUsersRecycler() {

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerAdapter = new RecyclerAdapter(getContext(), userPojoList_users);
        recyclerView.setAdapter(recyclerAdapter);


    }

    @Override
    public void onStart() {
        super.onStart();
        userPojoList_users.clear();
        usersRequest = new UsersRequest.UsersRequestBuilder()
                .setLimit(30)
                .build();
        fetchUsers(false);
    }

    private void initView(View view) {
        recyclerView = view.findViewById(R.id.recyclerView);
        group_members = view.findViewById(R.id.group_members);
        scroll = view.findViewById(R.id.scroll_view);
        message = view.findViewById(R.id.message);
        create = view.findViewById(R.id.create);
        show_contacts_btn = view.findViewById(R.id.show_contacts_btn);
        recyclerView.addOnScrollListener(onScrollListener);

        setUsersRecycler();
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
        show_contacts_btn.setOnClickListener(view1 -> {
            startActivity(new Intent(getActivity(), ContactsActivity.class));
        });
        create.setOnClickListener(v -> {
            if (group_members.getChildCount() >= 2) {
                createGroup();
            } else {
                Snackbar snackbar = Snackbar.make(fragment_view, "Add atleast two member to create group", Snackbar.LENGTH_LONG);
                snackbar.show();
            }
        });
        scroll.setVisibility(View.GONE);
    }

    private void createGroup() {
        Intent i = new Intent(getContext(), CreateGroupActivity.class);
        i.putExtra("uid_list", (Serializable) uid_list_users);
        i.putExtra("users_details", (Serializable) temp_users);
        startActivity(i);
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
            recyclerAdapter.notifyDataSetChanged();
        }

    };

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
                fetchUsers(true);
            }


        }
    };

    private void fetchUsers(boolean b) {

        ApiCalls.fetchCometChatUsers(getContext(), usersRequest, this);

    }

    private void addMembers(int position) {
        if (!userPojoList_member.contains(userPojoList_users.get(position))) {
            View view = getLayoutInflater().inflate(R.layout.horizontal_users_row, null, false);
            ShapeableImageView profile = view.findViewById(R.id.image);
            ImageView cancel = view.findViewById(R.id.removeItem);
            TextView name = view.findViewById(R.id.name);
            name.setMinWidth(250);
            TextView uid = view.findViewById(R.id.uid);
            name.setText(userPojoList_users.get(position).getName());
            uid.setText(userPojoList_users.get(position).getUid());
            Picasso.get().load(userPojoList_users.get(position).getAvatar()).into(profile);
            cancel.setOnClickListener(v -> {
                removeMember(view, group_members.indexOfChild(view));
            });
            group_members.addView(view);
            userPojoList_member.add(userPojoList_users.get(position));
            uid_list_users.add(userPojoList_users.get(position).getUid());
            temp_users.add(new UserPojo(userPojoList_users.get(position).getName(),userPojoList_users.get(position).getUid(),userPojoList_users.get(position).getAvatar()));
            scroll.setVisibility(View.VISIBLE);
        } else {
            Snackbar snackbar = Snackbar.make(fragment_view, "member is already Added", Snackbar.LENGTH_LONG);
            snackbar.show();
        }
    }

    private void removeMember(View view, int i) {
        group_members.removeView(view);
        userPojoList_member.remove(i);
        uid_list_users.remove(i);
        temp_users.remove(i);
        if (group_members.getChildCount() < 1)
            scroll.setVisibility(View.GONE);


    }

    @Override
    public void onSuccess(List<User> list) {
        if (!isScrolling)
            userPojoList_users.clear();
        else
            isScrolling = false;

        userPojoList_users.addAll(list);
        recyclerAdapter.notifyDataSetChanged();

    }

    @Override
    public void onResume() {
        super.onResume();
        ApiCalls.getUserActiveStatus(getActivity(),listenerID,this);


    }

    @Override
    public void onSuccess(User user) {
        if(userPojoList_users.contains(user)){
            int index=userPojoList_users.indexOf(user);
            userPojoList_users.remove(index);
            userPojoList_users.add(index,user);
            recyclerAdapter.notifyItemChanged(userPojoList_users.indexOf(user));
//            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onError(CometChatException e) {

    }
}