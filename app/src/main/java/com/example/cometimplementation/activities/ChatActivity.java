package com.example.cometimplementation.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cometchat.pro.constants.CometChatConstants;
import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.core.MessagesRequest;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.BaseMessage;
import com.cometchat.pro.models.CustomMessage;
import com.cometchat.pro.models.MediaMessage;
import com.cometchat.pro.models.MessageReceipt;
import com.cometchat.pro.models.TextMessage;
import com.cometchat.pro.models.TypingIndicator;
import com.cometchat.pro.models.User;
import com.example.cometimplementation.Interfaces.BlockUnBlockUserCallBackListener;
import com.example.cometimplementation.Interfaces.CallBackMessageListener;
import com.example.cometimplementation.Interfaces.MessageListiners;
import com.example.cometimplementation.Interfaces.SelectedMessageCallBack;
import com.example.cometimplementation.R;
import com.example.cometimplementation.adapter.ChatAdapter;
import com.example.cometimplementation.utilities.ApiCalls;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.imageview.ShapeableImageView;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener, MessageListiners, CallBackMessageListener, BlockUnBlockUserCallBackListener, SelectedMessageCallBack {

    private static final String TAG = "check_call";
    private LinearLayout linear_lay;
    private String receiverUid = "", receiverImg = "", receiverName = "", selected_message = "";
    private ShapeableImageView profile_img;
    private TextView name, indicator;
    private EditText input_message;
    private ImageView gallery, audio_call, video_call, more, message_info, attachments;
    private FloatingActionButton send;
    private RecyclerView recycler_chat;
    private ChatAdapter chatAdapter;
    private List<BaseMessage> messages = new ArrayList<>();
    private String listenerID = "ChatActivity.java";
    private Uri resultUri;
    private TypingIndicator typingIndicator;
    private BottomSheetDialog bottomSheetDialog;
    long delay = 2000;
    long last_text_edit = 0;
    Handler handler = new Handler();

    MessagesRequest messagesRequest;
    LinearLayoutManager linearLayoutManager;
    boolean isScrolling = false;

    int reply_message_id = 0;
    boolean isReply = false;
    boolean isGroup;
    boolean isAudio = false;
    int message_id = -1;
    View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        setContentView(R.layout.activity_chat);
        view = getWindow().getDecorView().findViewById(android.R.id.content);

        initView();

        recycler_chat.addOnScrollListener(onScrollListener);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recycler_chat);


    }

    private void initView() {
        receiverUid = getIntent().getStringExtra("uid");
        receiverImg = getIntent().getStringExtra("img_url");
        receiverName = getIntent().getStringExtra("name");
        isGroup = getIntent().getBooleanExtra("isGroup", false);

        linearLayoutManager = new LinearLayoutManager(this);

        profile_img = findViewById(R.id.profile_img);
        more = findViewById(R.id.more);
        message_info = findViewById(R.id.message_info);
        attachments = findViewById(R.id.attachments);
        linear_lay = findViewById(R.id.linear_lay);
        indicator = findViewById(R.id.indicator);
        name = findViewById(R.id.name);
        audio_call = findViewById(R.id.audio_call);
        video_call = findViewById(R.id.video_call);
        input_message = findViewById(R.id.input_message);
        gallery = findViewById(R.id.gallery);
        send = findViewById(R.id.send);
        recycler_chat = findViewById(R.id.recycler_chat);
        send.setOnClickListener(this);
        gallery.setOnClickListener(this);
        video_call.setOnClickListener(this);
        audio_call.setOnClickListener(this);
        linear_lay.setOnClickListener(this);
        message_info.setOnClickListener(this);
        attachments.setOnClickListener(this);
        name.setText(receiverName);
        Picasso.get().load(receiverImg).into(profile_img);

        if (isGroup) {
            more.setVisibility(View.VISIBLE);
            typingIndicator = new TypingIndicator(receiverUid, CometChatConstants.RECEIVER_TYPE_GROUP);
            messagesRequest = new MessagesRequest.MessagesRequestBuilder()
                    .setLimit(50)
                    .setGUID(receiverUid)
                    .build();
        } else {
            more.setVisibility(View.VISIBLE);
            typingIndicator = new TypingIndicator(receiverUid, CometChatConstants.RECEIVER_TYPE_USER);
            messagesRequest = new MessagesRequest.MessagesRequestBuilder()
                    .setLimit(50)
                    .setUID(receiverUid)
                    .build();
        }
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

    ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            Toast.makeText(ChatActivity.this, "on Move", Toast.LENGTH_SHORT).show();
            return false;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
            int position = viewHolder.getAdapterPosition();

            if (swipeDir == 8) {
                chatAdapter.notifyDataSetChanged();
                isReply = true;
                reply_message_id = messages.get(position).getId();
                if (messages.get(position) instanceof TextMessage) {
                    TextMessage textMessage = (TextMessage) messages.get(position);
                    input_message.clearComposingText();
                    input_message.setText("reply To :\n" + textMessage.getText() + "\n\nYour Reply here :\n");
                }
            } else if (swipeDir == 4) {
                deleteMessage(messages.get(position).getId());
                Toast.makeText(ChatActivity.this, "on Deleted" + messages.get(position).getId(), Toast.LENGTH_SHORT).show();

            }

        }

    };

    private void deleteMessage(int id) {
        ApiCalls.deleteMessage(this, id, this);

    }

    private RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);

            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                isScrolling = true;
            }


        }

        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            if (!recyclerView.canScrollVertically(-1)) {
                fetchData(true);
            }


        }
    };

    private Runnable input_finish_checker = new Runnable() {
        public void run() {
            if (System.currentTimeMillis() > (last_text_edit + delay - 500)) {
                CometChat.endTyping(typingIndicator);

            }
        }
    };

    private void setChatRecyclerView() {

        recycler_chat.setLayoutManager(linearLayoutManager);
        chatAdapter = new ChatAdapter(this, messages, isGroup, this);
        recycler_chat.setAdapter(chatAdapter);
        fetchData(false);

    }

    private void fetchData(boolean isScroll) {
        ApiCalls.fetchPreviousMessages(this, messagesRequest, isScroll, this);

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
//                checkGalleryPermission();
                break;
            case R.id.audio_call:
                isAudio = true;
                initiateCall();
                break;
            case R.id.video_call:
                isAudio = false;
                initiateCall();
                break;
            case R.id.attachments:
                openAttachmentMenu();
//                isAudio=false;
//                initiateCall();
                break;
            case R.id.linear_lay:
//                Intent i = new Intent(this, UserDetailActivity.class);
                Intent i = new Intent(this, UserDetailActivity.class);
                i.putExtra("uid", receiverUid);
                i.putExtra("isGroup", isGroup);
                startActivity(i);
                break;
            case R.id.input_message:
                if (messages.size() > 0)
                    linearLayoutManager.scrollToPosition(messages.size() - 1);
                break;
            case R.id.message_info:
                if (message_id != -1) {
                    Intent intent = new Intent(this, MessageInfoActivity.class);
                    intent.putExtra("uid", receiverUid);
                    intent.putExtra("isGroup", isGroup);
                    intent.putExtra("messageId", message_id);
                    intent.putExtra("message", selected_message);
                    startActivity(intent);
                }
                break;

        }

    }

    private void openAttachmentMenu() {
        bottomSheetDialog = new BottomSheetDialog(this, R.style.CustomBottomSheetDialog);
        View view = LayoutInflater.from(this).inflate(R.layout.attachment_layout, (LinearLayout) findViewById(R.id.sheet));
        bottomSheetDialog.setContentView(view);

        LinearLayout camera = view.findViewById(R.id.camera);
        LinearLayout photo = view.findViewById(R.id.photo);
        LinearLayout video = view.findViewById(R.id.video);
        LinearLayout broadcast = view.findViewById(R.id.broadcast);
        LinearLayout stickers = view.findViewById(R.id.stickers);
        LinearLayout more = view.findViewById(R.id.more);

        camera.setOnClickListener(view1 -> {
            bottomSheetDialog.dismiss();

        });
        photo.setOnClickListener(view1 -> {
            checkGalleryPermission();
            bottomSheetDialog.dismiss();


        });
        video.setOnClickListener(view1 -> {
            bottomSheetDialog.dismiss();

        });
        broadcast.setOnClickListener(view1 -> {
            bottomSheetDialog.dismiss();

        });
        stickers.setOnClickListener(view1 -> {
            bottomSheetDialog.dismiss();

        });
        more.setOnClickListener(view1 -> {
            bottomSheetDialog.dismiss();

        });

        bottomSheetDialog.show();

    }

    private void initiateCall() {

        Dexter.withActivity(this).withPermissions(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                Intent intent = new Intent(ChatActivity.this, CallingActivity.class);
                intent.putExtra("receiverUid", receiverUid);
                intent.putExtra("session_id", "");
                intent.putExtra("user_img", receiverImg);
                intent.putExtra("receiver_name", receiverName);
                intent.putExtra("isGroup", isGroup);
                intent.putExtra("isAudio", isAudio);
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
//            image.setImageURI(data.getData());
//            image.setVisibility(View.VISIBLE);
            resultUri = data.getData();
            if (resultUri != null)
                ApiCalls.sendMediaMessage(this, reply_message_id, isReply, "", receiverUid, this, "media", getPath(resultUri), isGroup);

//            startCrop(CropImage.getPickImageResultUri(this, data));
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

//                image.setImageURI(result.getUri());
//                image.setVisibility(View.VISIBLE);
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
            ApiCalls.sendTextMessage(this, reply_message_id, isReply, input_message.getText().toString().trim(), receiverUid, this, "text", isGroup);
        } else {
            if (resultUri == null) {
                input_message.setError("Please write some Message");
                input_message.requestFocus();
            }
        }
        if (resultUri != null) {
            Log.d("see_result_uri", "sendMessage: " + resultUri);
            ApiCalls.sendMediaMessage(this, reply_message_id, isReply, "", receiverUid, this, "media", getPath(resultUri), isGroup);
        }

    }


    @Override
    protected void onResume() {
        super.onResume();

        CometChat.addMessageListener(listenerID, new CometChat.MessageListener() {
            @Override
            public void onTextMessageReceived(TextMessage textMessage) {
                Log.d("check", "Text message received successfully: " + textMessage.toString());
                if (!isGroup) {
                    CometChat.markAsRead(textMessage.getId(), textMessage.getSender().getUid(), CometChatConstants.RECEIVER_TYPE_USER);
                } else {
                    CometChat.markAsRead(textMessage.getId(), textMessage.getReceiverUid(), CometChatConstants.RECEIVER_TYPE_GROUP);
                }
                messages.add(textMessage);
                chatAdapter.notifyItemInserted(messages.size() - 1);
                linearLayoutManager.scrollToPosition(messages.size() - 1);
            }

            @Override
            public void onMediaMessageReceived(MediaMessage mediaMessage) {
                if (!isGroup) {
                    CometChat.markAsRead(mediaMessage.getId(), mediaMessage.getSender().getUid(), CometChatConstants.RECEIVER_TYPE_USER);
                } else {
                    CometChat.markAsRead(mediaMessage.getId(), mediaMessage.getReceiverUid(), CometChatConstants.RECEIVER_TYPE_GROUP);
                }

                messages.add(mediaMessage);
                chatAdapter.notifyItemInserted(messages.size() - 1);
                linearLayoutManager.scrollToPosition(messages.size() - 1);
                Log.d("check", "Media message received successfully: " + mediaMessage.toString());
            }

            @Override
            public void onCustomMessageReceived(CustomMessage customMessage) {
                Log.d("check", "Custom message received successfully: " + customMessage.toString());
            }

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

            @Override
            public void onMessageDeleted(BaseMessage message) {
                updateViewForMessageDeleted(message);
                Log.d(TAG, "Message Edited");
            }

            @Override
            public void onMessagesDelivered(MessageReceipt messageReceipt) {
                super.onMessagesDelivered(messageReceipt);

                Log.e("message_read", "onMessagesDelivered: " + messageReceipt.toString() + "\n" + messages.contains(messageReceipt));

            }

            @Override
            public void onMessagesRead(MessageReceipt messageReceipt) {
                super.onMessagesRead(messageReceipt);
                Log.e("message_read", "onMessagesRead: " + messageReceipt.toString());
            }
        });


    }


    @Override
    protected void onPause() {
        super.onPause();
        CometChat.removeMessageListener(listenerID);


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
    public void onMessageReceivedSuccess(List<BaseMessage> list, boolean isScrolling) {

        if (!isScrolling) {
            for (BaseMessage message : list) {
                if (!isGroup)
                    CometChat.markAsRead(message.getId(), message.getSender().getUid(), CometChatConstants.RECEIVER_TYPE_USER);
                else
                    CometChat.markAsRead(message.getId(), message.getReceiverUid(), CometChatConstants.RECEIVER_TYPE_GROUP);

                if (message instanceof TextMessage) {
                    messages.add(message);
                    Log.d("", "Text message received successfully: " + ((TextMessage) message).toString());
                } else if (message instanceof MediaMessage) {
                    messages.add(message);
                    Log.d("", "Media message received successfully: " + ((MediaMessage) message).toString());
                }
            }
            chatAdapter.notifyDataSetChanged();
            if (messages.size() > 0)
                linearLayoutManager.scrollToPosition(messages.size() - 1);
        } else {
            for (BaseMessage message : list) {
                if (!isGroup)
                    CometChat.markAsRead(message.getId(), message.getSender().getUid(), CometChatConstants.RECEIVER_TYPE_USER);
                else
                    CometChat.markAsRead(message.getId(), message.getReceiverUid(), CometChatConstants.RECEIVER_TYPE_GROUP);

                if (message instanceof TextMessage) {
                    messages.add(0, message);
                    Log.d("", "Text message received successfully: " + ((TextMessage) message).toString());
                } else if (message instanceof MediaMessage) {
                    messages.add(0, message);
                    Log.d("", "Media message received successfully: " + ((MediaMessage) message).toString());
                }
            }
            chatAdapter.notifyDataSetChanged();
        }

    }

    @Override
    public void onMessageReceivedError(CometChatException e) {

    }

    @Override
    public void onMessageSuccess(BaseMessage baseMessage) {
        if (baseMessage instanceof TextMessage) {
            reply_message_id = 0;
            isReply = false;
            TextMessage textMessage = (TextMessage) baseMessage;
            CometChat.endTyping(typingIndicator);
            Log.d("check", "Message sent successfully: " + textMessage.toString() + textMessage.getDeletedAt());
            input_message.getText().clear();
            messages.add(textMessage);
            chatAdapter.notifyDataSetChanged();
            linearLayoutManager.scrollToPosition(messages.size() - 1);
        } else if (baseMessage instanceof MediaMessage) {
            reply_message_id = 0;
            isReply = false;
            MediaMessage mediaMessage = (MediaMessage) baseMessage;
            Log.d("check", "Media message sent successfully: " + mediaMessage.toString());
            resultUri = null;
//            image.setVisibility(View.GONE);
            messages.add(mediaMessage);
            chatAdapter.notifyDataSetChanged();
            linearLayoutManager.scrollToPosition(messages.size() - 1);
        }

    }

    @Override
    public void onMessageFailure(CometChatException e) {
        chatAdapter.notifyDataSetChanged();
        Log.e("get_deleted_error", "onMessageFailure: " + e.getMessage());
    }

    @Override
    public void onMessageDeleted(BaseMessage baseMessage) {
        updateViewForMessageDeleted(baseMessage);

    }

    private void updateViewForMessageDeleted(BaseMessage baseMessage) {
        if (baseMessage instanceof TextMessage) {
            TextMessage textMessage = (TextMessage) baseMessage;

            if (baseMessage.getDeletedAt() != 0) {
                int pos = messages.indexOf(textMessage);
                Log.d("get_deleted_error", "onMessageSuccess: " + pos);
                messages.add(pos, textMessage);
                chatAdapter.notifyItemChanged(pos);


            }
        } else if (baseMessage instanceof MediaMessage) {
            MediaMessage mediaMessage = (MediaMessage) baseMessage;
            if (baseMessage.getDeletedAt() != 0) {
                int pos = messages.indexOf(mediaMessage);
                Log.d("get_deleted_error", "onMessageSuccess: " + pos);
                messages.add(pos, mediaMessage);
                chatAdapter.notifyItemChanged(pos);

            }
        }
        chatAdapter.notifyDataSetChanged();

    }

    @SuppressLint("RestrictedApi")
    public void moreOption(View view) {

        Intent i = new Intent(this, MoreOptionActivity.class);
        i.putExtra("uid", receiverUid);
        i.putExtra("isGroup", isGroup);
        i.putExtra("name", receiverName);
        startActivity(i);


//        @SuppressLint("RestrictedApi") MenuBuilder menuBuilder = new MenuBuilder(this);
//        MenuInflater inflater = new MenuInflater(this);
//        inflater.inflate(R.menu.pop_up_menu, menuBuilder);
//        MenuPopupHelper menuPopupHelper = new MenuPopupHelper(this, menuBuilder, view);
//        menuPopupHelper.setForceShowIcon(true);
//        menuBuilder.setCallback(new MenuBuilder.Callback() {
//            @Override
//            public boolean onMenuItemSelected(@NonNull MenuBuilder menu, @NonNull MenuItem item) {
//                switch (item.getItemId()) {
//                    case R.id.block:
//                        blockUser();
//                        return true;
//                    case R.id.report:
//                        Toast.makeText(ChatActivity.this, "coming soon", Toast.LENGTH_SHORT).show();
//                        return true;
//                    default:
//                        return false;
//                }
//            }
//
//            @Override
//            public void onMenuModeChange(@NonNull MenuBuilder menu) {
//
//            }
//        });
//
//        menuPopupHelper.setGravity(Gravity.END);
//        menuPopupHelper.show();

    }

    private void blockUser() {

        ApiCalls.blockUser(this, Arrays.asList(receiverUid), this);

    }

    @Override
    public void onUserBlockUnBlockSuccess(HashMap<String, String> resultMap) {

        finish();
        Toast.makeText(this, "User Blocked Successfully", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onUserBlockUnBlockError(CometChatException e) {
        e.printStackTrace();
    }

    @Override
    public void onSelectedMessage(int messageId, String message, boolean isVisible) {

        if (isVisible) {
            message_info.setVisibility(View.VISIBLE);
        } else {
            message_info.setVisibility(View.GONE);
        }

        message_id = messageId;
        selected_message = message;
    }
}