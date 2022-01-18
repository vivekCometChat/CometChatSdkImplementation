package com.example.cometimplementation.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.models.User;
import com.example.cometimplementation.R;
import com.example.cometimplementation.fragments.ChattingUsersFragment;
import com.example.cometimplementation.fragments.GroupFragment;
import com.example.cometimplementation.fragments.RecentChatFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private String listenerId = "123456";
    User user;
    private ImageView more,create;
    private TextView current_window_name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        user = CometChat.getLoggedInUser();
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav);
        current_window_name= findViewById(R.id.current_window_name);
        more= findViewById(R.id.more);
        create= findViewById(R.id.create);
        bottomNavigationView.setSelectedItemId(R.id.chat);
        bottomNavigationView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener);

        setFragment(new RecentChatFragment());
        current_window_name.setText("Recent");

        more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,UpdateProfileActivity.class));
            }
        });
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,ContactsActivity.class));
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        user = CometChat.getLoggedInUser();

    }

    BottomNavigationView.OnNavigationItemSelectedListener onNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            switch (item.getItemId()) {
                case R.id.chat:
//                    getSupportActionBar().setTitle("Hello " + user.getName() + "!");
                    current_window_name.setText("Recent");
                    setFragment(new RecentChatFragment());
                    break;
                case R.id.users:
                    current_window_name.setText("Contacts");
//                    getSupportActionBar().setTitle("Users");
                    setFragment(new ChattingUsersFragment());
                    break;
                case R.id.group:
                    current_window_name.setText("Group");
//                    getSupportActionBar().setTitle("Group");
                    setFragment(new GroupFragment());
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