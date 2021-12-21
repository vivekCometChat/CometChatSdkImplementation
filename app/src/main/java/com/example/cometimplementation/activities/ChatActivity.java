package com.example.cometimplementation.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cometchat.pro.constants.CometChatConstants;
import com.cometchat.pro.core.Call;
import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.BaseMessage;
import com.cometchat.pro.models.CustomMessage;
import com.cometchat.pro.models.MediaMessage;
import com.cometchat.pro.models.TextMessage;
import com.cometchat.pro.models.User;
import com.example.cometimplementation.ApiCalls;
import com.example.cometimplementation.Interfaces.CallStatus;
import com.example.cometimplementation.Interfaces.Listeners;
import com.example.cometimplementation.R;
import com.example.cometimplementation.adapter.ChatAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener, Listeners , CallStatus {

    private static final String TAG = "check_call";
    private String receiverUid = "", receiverImg = "", receiverName = "";
    private CircleImageView profile_img;
    private TextView name;
    private EditText input_message;
    private ImageView gallery, image, call;
    private FloatingActionButton send;
    private RecyclerView recycler_chat;
    private ChatAdapter chatAdapter;
    private List<BaseMessage> messages = new ArrayList<>();
    private String listenerID = "ChatActivity.java";
    private Uri resultUri;

    private String listenerId = "123456";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        initView();


    }

    private void initView() {
        receiverUid = getIntent().getStringExtra("uid");
        receiverImg = getIntent().getStringExtra("img_url");
        receiverName = getIntent().getStringExtra("name");

        profile_img = findViewById(R.id.profile_img);
        name = findViewById(R.id.name);
        call = findViewById(R.id.call);
        input_message = findViewById(R.id.input_message);
        gallery = findViewById(R.id.gallery);
        send = findViewById(R.id.send);
        recycler_chat = findViewById(R.id.recycler_chat);
        image = findViewById(R.id.image);

        send.setOnClickListener(this);
        gallery.setOnClickListener(this);
        call.setOnClickListener(this);
        name.setText(receiverName);
        Picasso.get().load(receiverImg).into(profile_img);
        setChatRecyclerView();

    }

    private void setChatRecyclerView() {

        recycler_chat.setLayoutManager(new LinearLayoutManager(this));
        chatAdapter = new ChatAdapter(this, messages);
        recycler_chat.setAdapter(chatAdapter);

    }


    public void back(View view) {
        onBackPressed();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.send:
                sendMessage();
                break;
            case R.id.gallery:
                checkGalleryPermission();
                break;
            case R.id.call:
                initiateCall();


        }

    }

    private void initiateCall() {

        Intent intent = new Intent(this, CallingActivity.class);
        intent.putExtra("receiverUid", receiverUid);
        intent.putExtra("session_id","");
        startActivity(intent);


    }

    private void checkGalleryPermission() {
        Dexter.withContext(ChatActivity.this).withPermission(Manifest.permission.READ_EXTERNAL_STORAGE).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                Intent i = new Intent(Intent.ACTION_PICK);
                i.setType("image/*");
                startActivityForResult(Intent.createChooser(i, "select image file"), 1);
            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                permissionToken.continuePermissionRequest();
            }
        }).check();


    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            image.setImageURI(data.getData());
            image.setVisibility(View.VISIBLE);
            resultUri = data.getData();
//            startCrop(CropImage.getPickImageResultUri(this, data));
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                image.setImageURI(result.getUri());
                image.setVisibility(View.VISIBLE);
                resultUri = result.getUri();
                Log.d("image_uri", "nextActivity: " + resultUri);

            }
        }
    }


    private void startCrop(Uri imageUri) {
        CropImage.activity(imageUri).setGuidelines(CropImageView.Guidelines.ON).setAspectRatio(1, 1).setMultiTouchEnabled(true).start(this);
    }

    private void sendMessage() {


        if (!input_message.getText().toString().trim().isEmpty()) {
            sendingTextMessage(input_message.getText().toString().trim());
        } else {
            if (resultUri == null) {
                input_message.setError("Please write some Message");
                input_message.requestFocus();
            }
        }
        if (resultUri != null) {
            Log.d("see_result_uri", "sendMessage: " + resultUri);
            sendImageMessage(getPath(resultUri));

        }

    }

    private void sendImageMessage(String image_uri) {
        MediaMessage mediaMessage = new MediaMessage(receiverUid, new File(image_uri), CometChatConstants.MESSAGE_TYPE_IMAGE, CometChatConstants.RECEIVER_TYPE_USER);

        CometChat.sendMediaMessage(mediaMessage, new CometChat.CallbackListener<MediaMessage>() {
            @Override
            public void onSuccess(MediaMessage mediaMessage) {
                Log.d("check", "Media message sent successfully: " + mediaMessage.toString());
                resultUri = null;
                image.setVisibility(View.GONE);
                messages.add(mediaMessage);
                chatAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(CometChatException e) {
                Log.d("check", "Media message sending failed with exception: " + e.getMessage());
            }
        });
    }

    private void sendingTextMessage(String message) {
        TextMessage textMessage = new TextMessage(receiverUid, message, CometChatConstants.RECEIVER_TYPE_USER);

        CometChat.sendMessage(textMessage, new CometChat.CallbackListener<TextMessage>() {
            @Override
            public void onSuccess(TextMessage textMessage) {
                Log.d("check", "Message sent successfully: " + textMessage.toString());
                input_message.getText().clear();
                hideKeyBoard();
                messages.add(textMessage);
                chatAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(CometChatException e) {
                Log.d("check", "Message sending failed with exception: " + e.getMessage());

            }
        });

    }


    @Override
    protected void onResume() {
        super.onResume();

        CometChat.addMessageListener(listenerID, new CometChat.MessageListener() {
            @Override
            public void onTextMessageReceived(TextMessage textMessage) {
                Log.d("check", "Text message received successfully: " + textMessage.toString());
                messages.add(textMessage);
                chatAdapter.notifyDataSetChanged();
            }

            @Override
            public void onMediaMessageReceived(MediaMessage mediaMessage) {
                messages.add(mediaMessage);
                chatAdapter.notifyDataSetChanged();
                Log.d("check", "Media message received successfully: " + mediaMessage.toString());
            }

            @Override
            public void onCustomMessageReceived(CustomMessage customMessage) {
                Log.d("check", "Custom message received successfully: " + customMessage.toString());
            }
        });

        ApiCalls.callInformation(this,this);

    }

    @Override
    protected void onPause() {
        super.onPause();
        CometChat.removeMessageListener(listenerID);
        CometChat.removeMessageListener(listenerId);


    }

    public void hideKeyBoard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


    public String getPath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        startManagingCursor(cursor);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    @Override
    public void addedNewUser(User user) {

    }

    @Override
    public void receiveCall(Call call) {

        ShowCallingAlertDialog(call);


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

    private void ShowCallingAlertDialog(Call call) {

        Dialog dialog=new Dialog(this);
        View view=getLayoutInflater().inflate(R.layout.calling_request_alert_layout,null,false);
        dialog.setContentView(view);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        CircleImageView circleImageView=dialog.findViewById(R.id.image_view);

        FloatingActionButton reject=dialog.findViewById(R.id.reject);
        FloatingActionButton accept=dialog.findViewById(R.id.accept);
        Picasso.get().load(call.getSender().getAvatar()).into(circleImageView);
        accept.setOnClickListener(v -> {
           ApiCalls.acceptCall(this,this,call);
           dialog.dismiss();
        });

        reject.setOnClickListener(v->{
            ApiCalls.rejectCall(this,this,call);
            dialog.dismiss();

        });
        dialog.show();

    }

    @Override
    public void reject(Call call) {
        Toast.makeText(ChatActivity.this,call.getSender().toString()+ " call has been rejected",Toast.LENGTH_LONG).show();
    }

    @Override
    public void accept(Call call) {
        Intent intent = new Intent(this, CallingActivity.class);
        intent.putExtra("receiverUid", "");
        intent.putExtra("session_id",call.getSessionId());
        startActivity(intent);

    }
}