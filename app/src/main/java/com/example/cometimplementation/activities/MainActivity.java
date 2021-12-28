package com.example.cometimplementation.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.cometchat.pro.core.Call;
import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.models.User;
import com.example.cometimplementation.Interfaces.Listeners;
import com.example.cometimplementation.R;
import com.example.cometimplementation.fragments.ChattingUsersFragment;
import com.example.cometimplementation.fragments.RecentChatFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity implements Listeners {

    private String listenerId = "123456";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav);
        bottomNavigationView.setSelectedItemId(R.id.chat);
        bottomNavigationView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener);
        setFragment(new RecentChatFragment());
        getSupportActionBar().setTitle("Chat");

    }


    BottomNavigationView.OnNavigationItemSelectedListener onNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            switch (item.getItemId()) {
                case R.id.chat:
                    getSupportActionBar().setTitle("Chat");
                    setFragment(new RecentChatFragment());
                    break;
                case R.id.users:
                    getSupportActionBar().setTitle("Users");
                    setFragment(new ChattingUsersFragment());
                    break;
                case R.id.group:
                    getSupportActionBar().setTitle("Group");
                    break;
                case R.id.calls:
                    getSupportActionBar().setTitle("Call Logs");
                    break;
            }
            return true;
        }
    };


    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void addedNewUser(User user) {
//        fetchUsers();
    }

    @Override
    public void receiveCall(Call call) {
//        ShowCallingAlertDialog(call);

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


    @Override
    protected void onPause() {
        super.onPause();
        CometChat.removeMessageListener(listenerId);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 100 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            setFragment(new RecentChatFragment());
            getSupportActionBar().setTitle("Chat");

        } else {
            Toast.makeText(MainActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
        }
    }

    private void setFragment(Fragment fragment) {

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.container_for_fragments, fragment);
        fragmentTransaction.commit();

    }
}