package com.example.cometimplementation.fragments;

import android.Manifest;
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

import com.cometchat.pro.core.Call;
import com.cometchat.pro.models.User;
import com.example.cometimplementation.Interfaces.Listeners;
import com.example.cometimplementation.R;
import com.example.cometimplementation.adapter.ContactRecycler;
import com.example.cometimplementation.models.UserPojo;

import java.util.ArrayList;
import java.util.List;


public class ContactFragment extends Fragment implements Listeners {
    private RecyclerView contact_recycler;
    private TextView message;

    ContactRecycler contactRecycler;
    List<UserPojo> userPojos = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_contact, container, false);
        initViews(view);
        setContactRecycler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                checkPermissions();
            }
        }).start();
        return view;

    }

    private void initViews(View view) {

        contact_recycler = view.findViewById(R.id.contact_recycler);
        message = view.findViewById(R.id.message);
        message.setVisibility(View.GONE);


    }

    private void setContactRecycler() {
        contact_recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        contactRecycler = new ContactRecycler(this, getContext(), userPojos);
        contact_recycler.setAdapter(contactRecycler);


    }

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_CONTACTS}, 100);
        } else {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    getContactList();

                }
            });
        }

    }
    private void getContactList() {
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
                    Log.d("see_contacts", "name: " + name + "\nnumber" + number);
                    phoneCursor.close();

                }
            }
            cursor.close();
            Log.d("see_contacts", "total count" + userPojos.size());
            contactRecycler.notifyDataSetChanged();
        }

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            message.setVisibility(View.GONE);
            contact_recycler.setVisibility(View.VISIBLE);
            getContactList();
        } else {
            message.setVisibility(View.VISIBLE);
            message.setText("Permission Denied");
            contact_recycler.setVisibility(View.GONE);
            Toast.makeText(getActivity(), "Permission Denied", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void addedNewUser(User user) {

    }

    @Override
    public void receiveCall(Call call) {

    }

    @Override
    public void acceptedOutGoingCall(Call call) {

    }

    @Override
    public void rejectedOutGoingCall(Call call) {

    }

    @Override
    public void canceledOutGoingCall(Call call) {

    }
}