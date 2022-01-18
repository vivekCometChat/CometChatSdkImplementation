package com.example.cometimplementation.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.cometchat.pro.constants.CometChatConstants;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.Group;
import com.cometchat.pro.models.GroupMember;
import com.example.cometimplementation.Interfaces.OnGroupCreateCallBackListener;
import com.example.cometimplementation.Interfaces.OnMembersAddedCallBack;
import com.example.cometimplementation.R;
import com.example.cometimplementation.models.UserPojo;
import com.example.cometimplementation.utilities.ApiCalls;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import de.measite.minidns.record.A;

public class CreateGroupActivity extends AppCompatActivity implements View.OnClickListener, OnGroupCreateCallBackListener, OnMembersAddedCallBack {

    //    private LinearLayout group_members, edit_name, edit_status;
    private LinearLayout group_members,group_type_selection;
    private RadioGroup radioGroup;
    private TextInputLayout password_input;
    private String update_category = "";
    private String group_type = "";
    private String description="Lets talk Together!",GUID = "", icon_url = "https://t4.ftcdn.net/jpg/03/03/24/45/360_F_303244508_cEc4mqtJPmjMX3A20UJOiU5RT1taJV2m.jpg";
    private EditText input_group_name;
    private ImageView arrow_down, arrow_up;
    TextView create_group;
    List<String> uid_list = new ArrayList<>();
    List<UserPojo> userPojoList = new ArrayList<>();
    List<GroupMember> members = new ArrayList<>();
    View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);
        initViews();
        radioGroup.setOnCheckedChangeListener((radioGroup, i) -> {
            RadioButton radioButton = findViewById(i);
            if (radioButton.getText().equals("Public")) {
                password_input.setVisibility(View.GONE);
                group_type = CometChatConstants.GROUP_TYPE_PUBLIC;
            } else if (radioButton.getText().equals("Private")) {
                password_input.setVisibility(View.GONE);
                group_type = CometChatConstants.GROUP_TYPE_PRIVATE;
            } else if (radioButton.getText().equals("Password")) {
                password_input.setVisibility(View.VISIBLE);
                group_type = CometChatConstants.GROUP_TYPE_PASSWORD;

            }
        });
        showAddedMembers();

    }

    private void showAddedMembers() {
        for (int i = 0; i < userPojoList.size(); i++) {
            members.add(new GroupMember(uid_list.get(i), CometChatConstants.SCOPE_MODERATOR));
            addMembers(i);
        }
    }

    private void addMembers(int position) {
        View view = getLayoutInflater().inflate(R.layout.horizontal_users_row, null, false);
        ShapeableImageView profile = view.findViewById(R.id.image);
        ImageView cancel = view.findViewById(R.id.removeItem);
        cancel.setVisibility(View.GONE);
        TextView name = view.findViewById(R.id.name);
        name.setMinWidth(250);
        TextView uid = view.findViewById(R.id.uid);
        name.setText(userPojoList.get(position).getName());
        uid.setText(userPojoList.get(position).getNumber());
        Picasso.get().load(userPojoList.get(position).getImg_url()).into(profile);
        group_members.addView(view);
    }

    private void initViews() {
        uid_list = (ArrayList<String>) getIntent().getSerializableExtra("uid_list");
        userPojoList = (ArrayList<UserPojo>) getIntent().getSerializableExtra("users_details");
        group_members = findViewById(R.id.group_members);
        group_type_selection = findViewById(R.id.group_type_selection);
        create_group = findViewById(R.id.create_group);
        input_group_name = findViewById(R.id.input_group_name);
        arrow_down = findViewById(R.id.arrow_down);
        arrow_up = findViewById(R.id.arrow_up);

        radioGroup = findViewById(R.id.radioGroup);
        password_input = findViewById(R.id.password_input);

        create_group.setOnClickListener(this);
        arrow_down.setOnClickListener(this);
        arrow_up.setOnClickListener(this);
        password_input.setVisibility(View.GONE);
        Date d = new Date();
        GUID = String.valueOf(d.getTime());
        view = getWindow().getDecorView().findViewById(android.R.id.content);

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.create_group:
                createGroup();
                break;
            case R.id.arrow_down:
                arrow_down.setVisibility(View.GONE);
                arrow_up.setVisibility(View.VISIBLE);
                group_type_selection.setVisibility(View.VISIBLE);
                break;
            case R.id.arrow_up:
                arrow_down.setVisibility(View.VISIBLE);
                arrow_up.setVisibility(View.GONE);
                group_type_selection.setVisibility(View.GONE);
                break;
        }
    }

    private void createGroup() {
        if (!input_group_name.getText().toString().trim().isEmpty() && !group_type.isEmpty() && !GUID.isEmpty()) {
            if (group_type.equals(CometChatConstants.GROUP_TYPE_PASSWORD)) {
                if (!password_input.getEditText().getText().toString().trim().isEmpty()) {
                    ApiCalls.createGroup(this, GUID, input_group_name.getText().toString().trim(), group_type, password_input.getEditText().getText().toString().trim(), icon_url, description, this);
                } else {
                    password_input.getEditText().setError("Enter password");
                }
            } else {
                if (group_type.equals(CometChatConstants.GROUP_TYPE_PUBLIC)) {

                    ApiCalls.createGroup(this, GUID, input_group_name.getText().toString().trim(), group_type, "", icon_url, description, this);

                } else if (group_type.equals(CometChatConstants.GROUP_TYPE_PRIVATE)) {

                    ApiCalls.createGroup(this, GUID, input_group_name.getText().toString().trim(), group_type, "", icon_url, description, this);

                }
            }
        } else {
            if (input_group_name.getText().toString().isEmpty()) {
                showMessageOnSnackBar(view, "Name field Can't be Empty!");
            } else if (group_type.isEmpty()) {

                showMessageOnSnackBar(view, "Please select group type!");
            } else if (GUID.isEmpty()) {
                showMessageOnSnackBar(view, "Failed to create group id!");
            }
        }


    }

    private void showMessageOnSnackBar(View view, String message) {
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
        snackbar.show();
    }


    @Override
    public void onGroupCreateSuccess(Group group) {

        showMessageOnSnackBar(view, group.getOwner() + " have Created Group Successfully");
        ApiCalls.addMembersInGroup(this, group.getGuid(), members, this);

    }

    @Override
    public void onMembersAddedSuccess(HashMap<String, String> successMap) {
        Log.e("onMembersAddedSuccess", "onMembersAddedSuccess: " + successMap.toString());
        showMessageOnSnackBar(view, "Group Created Successfully !");
        finish();
    }

    @Override
    public void onError(CometChatException e) {
        e.printStackTrace();
    }

    public void back(View view) {
        onBackPressed();
    }

    public void cancel(View view) {

        onBackPressed();
    }
}