package com.example.cometimplementation.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.User;
import com.example.cometimplementation.Interfaces.UserListeners;
import com.example.cometimplementation.R;
import com.example.cometimplementation.utilities.ApiCalls;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class UpdateProfileActivity extends AppCompatActivity implements UserListeners {
    User user;
    CircleImageView profile_pic;
    FloatingActionButton choose_image;
    TextView user_name, about, Phone;
    String update_category = "";
    LinearLayout edit_name, edit_status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);
        initViews();
        edit_name.setOnClickListener(view -> {
            update_category = "name";
            showDialogBox("Enter your name");
        });
        edit_status.setOnClickListener(view -> {
            update_category = "status";
            showDialogBox("Enter your Status");
        });

        choose_image.setOnClickListener(view -> {
            update_category="avatar";
            showDialogBox("Enter Link for Avatar");
        });

    }

    private void showDialogBox(String category) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottom_dialog_sheet);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        TextView hint = dialog.findViewById(R.id.input_hint);
        hint.setText(category);
        TextInputLayout input = dialog.findViewById(R.id.input);
        TextView cancel = dialog.findViewById(R.id.cancel);
        TextView save = dialog.findViewById(R.id.save);

        cancel.setOnClickListener(view -> {
            dialog.dismiss();
        });

        save.setOnClickListener(view -> {
            if (update_category.equals("name") && !update_category.isEmpty()) {
                User usr=new User();
                usr.setName(input.getEditText().getText().toString().trim());
                ApiCalls.updateUserDetails(usr,this,this);
            } else if (update_category.equals("status") && !update_category.isEmpty()) {
                User usr=new User();
                usr.setStatusMessage(input.getEditText().getText().toString().trim());
                ApiCalls.updateUserDetails(usr,this,this);
            }else if(update_category.equals("avatar") && !update_category.isEmpty()){
                User usr=new User();
                usr.setAvatar(input.getEditText().getText().toString().trim());
                ApiCalls.updateUserDetails(usr,this,this);
            }
            dialog.dismiss();
        });

        dialog.show();
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);

    }


    @Override
    protected void onStart() {
        setData(user);
        super.onStart();
    }

    private void setData(User usr) {
        Picasso.get().load(usr.getAvatar()).into(profile_pic);
        user_name.setText(usr.getName());
        about.setText(usr.getStatusMessage() == null ? "Hey There I am using CometChat" : usr.getStatusMessage());
        Phone.setText("+91 " + usr.getUid());

    }

    private void initViews() {
        user = CometChat.getLoggedInUser();
        edit_status = findViewById(R.id.edit_status);
        edit_name = findViewById(R.id.edit_name);

        profile_pic = findViewById(R.id.profile_pic);
        user_name = findViewById(R.id.user_name);
        choose_image = findViewById(R.id.choose_image);
        about = findViewById(R.id.about);
        Phone = findViewById(R.id.Phone);
        getSupportActionBar().setTitle("Profile");
    }


    @Override
    public void onSuccess(User user) {
        Log.d("see_object", "onSuccess: "+user.toString());
        setData(user);
    }

    @Override
    public void onError(CometChatException e) {
        Log.d("see_object", "onError: "+e.getMessage());
        Toast.makeText(this,e.getMessage(),Toast.LENGTH_SHORT).show();
        e.printStackTrace();

    }
}