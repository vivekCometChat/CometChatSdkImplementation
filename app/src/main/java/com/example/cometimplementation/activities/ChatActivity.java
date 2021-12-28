package com.example.cometimplementation.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.cometchat.pro.constants.CometChatConstants;
import com.cometchat.pro.core.Call;
import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.BaseMessage;
import com.cometchat.pro.models.CustomMessage;
import com.cometchat.pro.models.MediaMessage;
import com.cometchat.pro.models.TextMessage;
import com.cometchat.pro.models.TypingIndicator;
import com.cometchat.pro.models.User;
import com.example.cometimplementation.Interfaces.Listeners;
import com.example.cometimplementation.R;
import com.example.cometimplementation.adapter.ChatAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.single.PermissionListener;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener, Listeners{

    private static final String TAG = "check_call";
    private String receiverUid = "", receiverImg = "", receiverName = "";
    private CircleImageView profile_img;
    private TextView name,indicator;
    private EditText input_message;
    private ImageView gallery, image, call;
    private FloatingActionButton send;
    private RecyclerView recycler_chat;
    private ChatAdapter chatAdapter;
    private List<BaseMessage> messages = new ArrayList<>();
    private String listenerID = "ChatActivity.java";
    private Uri resultUri;
    private TypingIndicator typingIndicator;
    private String listenerId = "123456";

    long delay = 2000;
    long last_text_edit = 0;
    Handler handler = new Handler();
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
        indicator = findViewById(R.id.indicator);
        name = findViewById(R.id.name);
        call = findViewById(R.id.call);
        input_message = findViewById(R.id.input_message);
        gallery = findViewById(R.id.gallery);
        send = findViewById(R.id.send);
        recycler_chat = findViewById(R.id.recycler_chat);
        image = findViewById(R.id.image);
        typingIndicator = new TypingIndicator(receiverUid, CometChatConstants.RECEIVER_TYPE_USER);
        send.setOnClickListener(this);
        gallery.setOnClickListener(this);
        call.setOnClickListener(this);
        name.setText(receiverName);
        Picasso.get().load(receiverImg).into(profile_img);
        setChatRecyclerView();

        input_message.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                CometChat.startTyping(typingIndicator);
                handler.removeCallbacks(input_finish_checker);
            }
            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() > 0) {
                    last_text_edit = System.currentTimeMillis();
                }
                handler.postDelayed(input_finish_checker, delay);
            }
        });

    }

    private Runnable input_finish_checker = new Runnable() {
        public void run() {
            if (System.currentTimeMillis() > (last_text_edit + delay - 500)) {
                // TODO: do what you need here
                // ............
                // ............
                CometChat.endTyping(typingIndicator);

            }
        }
    };

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

        Dexter.withActivity(this).withPermissions(Manifest.permission.CAMERA,Manifest.permission.RECORD_AUDIO).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                Intent intent = new Intent(ChatActivity.this, CallingActivity.class);
                intent.putExtra("receiverUid", receiverUid);
                intent.putExtra("session_id", "");
                intent.putExtra("user_img", receiverImg);
                intent.putExtra("receiver_name", receiverName);
                startActivity(intent);
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {

            }
        }).check();




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
                recycler_chat.smoothScrollToPosition(messages.size()-1);

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
                CometChat.endTyping(typingIndicator);
                Log.d("check", "Message sent successfully: " + textMessage.toString());
                input_message.getText().clear();
                hideKeyBoard();
                messages.add(textMessage);
                chatAdapter.notifyDataSetChanged();
                recycler_chat.smoothScrollToPosition(messages.size()-1);

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
                recycler_chat.smoothScrollToPosition(messages.size()-1);
            }

            @Override
            public void onMediaMessageReceived(MediaMessage mediaMessage) {
                messages.add(mediaMessage);
                chatAdapter.notifyDataSetChanged();
                recycler_chat.smoothScrollToPosition(messages.size()-1);

                Log.d("check", "Media message received successfully: " + mediaMessage.toString());
            }

            @Override
            public void onCustomMessageReceived(CustomMessage customMessage) {
                Log.d("check", "Custom message received successfully: " + customMessage.toString());
            }
        });
//        ApiCalls.callInformation(this, this);

        CometChat.addMessageListener("Listener 1", new CometChat.MessageListener() {
            @Override
            public void onTypingStarted(TypingIndicator typingIndicator) {
                Log.d(TAG, " Typing Started : " + typingIndicator.toString());
                indicator.setVisibility(View.VISIBLE);
            }
            @Override
            public void onTypingEnded(TypingIndicator typingIndicator) {
                Log.d(TAG, " Typing Ended : " + typingIndicator.toString());
                indicator.setVisibility(View.GONE);
            }

        });

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