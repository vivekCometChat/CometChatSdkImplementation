package com.example.cometimplementation.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.cometimplementation.R;
import com.example.cometimplementation.activities.ContactsActivity;
import com.example.cometimplementation.adapter.RecyclerAdapter;
import com.example.cometimplementation.models.UserPojo;
import com.example.cometimplementation.utilities.SharedPrefData;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;


public class ChattingUsersFragment extends Fragment {
    List<UserPojo> userPojoList = new ArrayList<>();
    private RecyclerView recyclerView;
    RecyclerAdapter recyclerAdapter;
    TextView message;
    FloatingActionButton show_contacts_btn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chatting_users, container, false);

        initView(view);

        return view;

    }

    @SuppressLint("NotifyDataSetChanged")
    private void setUsersRecycler() {

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerAdapter = new RecyclerAdapter(getActivity(), userPojoList);
        recyclerView.setAdapter(recyclerAdapter);

        if(SharedPrefData.getCommonUser(getContext())!=null){
            userPojoList.addAll(SharedPrefData.getCommonUser(getContext()));
            recyclerAdapter.notifyDataSetChanged();
        }else{
            recyclerView.setVisibility(View.GONE);
            message.setVisibility(View.VISIBLE);
            message.setText("No User Found");
        }
    }

    private void initView(View view) {
        recyclerView = view.findViewById(R.id.recyclerView);
        message = view.findViewById(R.id.message);
        show_contacts_btn = view.findViewById(R.id.show_contacts_btn);
        setUsersRecycler();

        show_contacts_btn.setOnClickListener(view1 -> {
            startActivity(new Intent(getActivity(), ContactsActivity.class));

        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if(SharedPrefData.getCommonUser(getContext())!=null){
            userPojoList.clear();
            userPojoList.addAll(SharedPrefData.getCommonUser(getContext()));
            recyclerAdapter.notifyDataSetChanged();
        }else{
            recyclerView.setVisibility(View.GONE);
            message.setVisibility(View.VISIBLE);
            message.setText("No User Found");
        }

    }
}