package com.example.cometimplementation.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.core.UsersRequest;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.User;
import com.example.cometimplementation.ApiCalls;
import com.example.cometimplementation.R;
import com.example.cometimplementation.activities.CallingActivity;
import com.example.cometimplementation.activities.ContactsActivity;
import com.example.cometimplementation.activities.MainActivity;
import com.example.cometimplementation.adapter.RecyclerAdapter;
import com.example.cometimplementation.models.UserPojo;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;


public class ChattingUsersFragment extends Fragment {
    List<UserPojo> userPojos;
    List<UserPojo> recycler = new ArrayList<>();
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

    private void setUsersRecycler() {

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerAdapter = new RecyclerAdapter(getActivity(), recycler);
        recyclerView.setAdapter(recyclerAdapter);
    }

    private void initView(View view) {
        ApiCalls.cometChatInitialize(getActivity());
        recyclerView = view.findViewById(R.id.recyclerView);
        message = view.findViewById(R.id.message);
        show_contacts_btn = view.findViewById(R.id.show_contacts_btn);
        setUsersRecycler();

        new Thread(() -> checkPermissions()).start();

        show_contacts_btn.setOnClickListener(view1 -> {
            startActivity(new Intent(getActivity(), ContactsActivity.class));

        });
    }


    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_CONTACTS}, 100);

        } else {
            getContactList();
            fetchUsers();
        }

    }

    private void getContactList() {
        userPojos = new ArrayList<>();
        Uri uri = ContactsContract.Contacts.CONTENT_URI;
        String sort = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC";

        Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, sort);

        if (cursor.getCount() > 0) {

            while (cursor.moveToNext()) {
                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                Uri uriPhone = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
                String selection = ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " =?";
                Cursor phoneCursor = getActivity().getContentResolver().query(uriPhone, null, selection, new String[]{id}, null);
                if (phoneCursor.moveToNext()) {
                    String number = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    number = number.replaceAll("()-", "");
                    userPojos.add(new UserPojo(name, number));
//                    Log.d("see_contacts", "name: " + name + "\nnumber" + number);
                    phoneCursor.close();
                }
            }
            cursor.close();
        }

    }

    private void fetchUsers() {
        UsersRequest usersRequest = new UsersRequest.UsersRequestBuilder().setLimit(30).build();

        usersRequest.fetchNext(new CometChat.CallbackListener<List<User>>() {
            @Override
            public void onSuccess(List<User> list) {
                Log.d("check", "User list received: " + list.size());
                checkIfUserExistInPhoneBook(list);
            }

            @Override
            public void onError(CometChatException e) {
                message.setVisibility(View.VISIBLE);
                message.setText(e.getMessage());
                recyclerView.setVisibility(View.GONE);
                Log.d("check", "User list fetching failed with exception: " + e.getMessage());
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void checkIfUserExistInPhoneBook(List<User> list) {
        Log.d("check", "if its present" + list.toString());
        recycler.clear();
        if (userPojos.size() > 0) {
            recyclerView.setVisibility(View.VISIBLE);
            for (int i = 0; i < userPojos.size(); i++) {
                for (int j = 0; j < list.size(); j++) {
                    if (list.get(j).getUid().equals(userPojos.get(i).getNumber())) {
                        recycler.add(new UserPojo(userPojos.get(i).getName(), userPojos.get(i).getNumber(), list.get(j).getAvatar()));
                    }
                }

            }
            if (recycler.size() == 0) {
                recyclerView.setVisibility(View.GONE);
                message.setVisibility(View.VISIBLE);
                message.setText("No User Found");

            } else {
                recyclerAdapter.notifyDataSetChanged();
            }
        } else {
            Log.d("check", "\n\n" + "You Don't have Any Contact To Look For");
            recyclerView.setVisibility(View.GONE);

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            message.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            getContactList();
            fetchUsers();
        } else {
            message.setVisibility(View.VISIBLE);
            message.setText("Permission Denied");
            recyclerView.setVisibility(View.GONE);
            Toast.makeText(getActivity(), "Permission Denied", Toast.LENGTH_SHORT).show();
        }
    }

}