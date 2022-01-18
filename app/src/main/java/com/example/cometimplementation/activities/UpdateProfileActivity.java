package com.example.cometimplementation.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cometchat.pro.constants.CometChatConstants;
import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.User;
import com.example.cometimplementation.Interfaces.UserListeners;
import com.example.cometimplementation.R;
import com.example.cometimplementation.utilities.ApiCalls;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputLayout;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class UpdateProfileActivity extends AppCompatActivity implements UserListeners {
    User user;
    String update_category = "";
    ShapeableImageView user_avatar;
    TextView name, status_message;
    ImageView status;
    LinearLayout change_name, status_message_lay, edit_status_lay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);
        initViews();
        change_name.setOnClickListener(view -> {
            update_category = "name";
            showDialogBox("Enter your name");
        });
        status_message_lay.setOnClickListener(view -> {
            update_category = "status";
            showDialogBox("Enter your Status");
        });

        edit_status_lay.setOnClickListener(view -> {
            update_category = "status";
            showDialogBox("Enter Online status");
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
                User usr = new User();
                usr.setName(input.getEditText().getText().toString().trim());
                ApiCalls.updateUserDetails(usr, this, this);
            } else if (update_category.equals("status") && !update_category.isEmpty()) {
                User usr = new User();
                usr.setStatusMessage(input.getEditText().getText().toString().trim());
                ApiCalls.updateUserDetails(usr, this, this);
            } else if (update_category.equals("avatar") && !update_category.isEmpty()) {
                User usr = new User();
                usr.setAvatar(input.getEditText().getText().toString().trim());
                ApiCalls.updateUserDetails(usr, this, this);
            } else if (update_category.equals("status") && !update_category.isEmpty()) {
                User usr = new User();
                usr.setStatus(input.getEditText().getText().toString().trim());
                ApiCalls.updateUserDetails(usr, this, this);

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

        Picasso.get().load(usr.getAvatar()).into(user_avatar);
        name.setText(usr.getName());
        if (user.getStatus().equals(CometChatConstants.USER_STATUS_ONLINE))
            status.setColorFilter(ContextCompat.getColor(this, R.color.online_green), android.graphics.PorterDuff.Mode.MULTIPLY);
        else
            status.setColorFilter(ContextCompat.getColor(this, R.color.red), android.graphics.PorterDuff.Mode.MULTIPLY);

        status_message.setText(user.getStatus());

    }

    private void initViews() {
        user = CometChat.getLoggedInUser();
        user_avatar = findViewById(R.id.user_avatar);
        name = findViewById(R.id.name);
        change_name = findViewById(R.id.change_name);
        status_message_lay = findViewById(R.id.status_message_lay);
        edit_status_lay = findViewById(R.id.edit_status_lay);
        status = findViewById(R.id.status);
        status_message = findViewById(R.id.status_message);


    }


    @Override
    public void onSuccess(User user) {
        Log.d("see_object", "onSuccess: " + user.toString());
        setData(user);
    }

    @Override
    public void onError(CometChatException e) {
        Log.d("see_object", "onError: " + e.getMessage());
        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        e.printStackTrace();

    }

    public void back(View view) {
        onBackPressed();
    }
}