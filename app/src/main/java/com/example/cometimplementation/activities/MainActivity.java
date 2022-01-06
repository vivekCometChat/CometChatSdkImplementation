package com.example.cometimplementation.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
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
import com.example.cometimplementation.utilities.SharedPrefData;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private String listenerId = "123456";
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        user = CometChat.getLoggedInUser();
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav);
        bottomNavigationView.setSelectedItemId(R.id.chat);
        bottomNavigationView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener);
//        getSupportActionBar().setTitle("Hello "+ SharedPrefData.getUserName(MainActivity.this)+"!");

    }

    @Override
    protected void onStart() {
        super.onStart();
        user = CometChat.getLoggedInUser();
        getSupportActionBar().setTitle("Hello " + user.getName() + "!");
        setFragment(new RecentChatFragment());
    }

    BottomNavigationView.OnNavigationItemSelectedListener onNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            switch (item.getItemId()) {
                case R.id.chat:
                    getSupportActionBar().setTitle("Hello " + user.getName() + "!");
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
    protected void onPause() {
        super.onPause();
        CometChat.removeMessageListener(listenerId);

    }

    private void setFragment(Fragment fragment) {

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.container_for_fragments, fragment);
        fragmentTransaction.commit();

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.profile) {
            startActivity(new Intent(MainActivity.this, ProfileActivity.class));
//            Toast.makeText(MainActivity.this,"profile",Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }
}