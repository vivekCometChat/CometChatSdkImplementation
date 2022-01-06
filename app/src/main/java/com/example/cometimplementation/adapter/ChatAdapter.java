package com.example.cometimplementation.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.cometchat.pro.constants.CometChatConstants;
import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.models.Action;
import com.cometchat.pro.models.BaseMessage;
import com.cometchat.pro.models.MediaMessage;
import com.cometchat.pro.models.TextMessage;
import com.example.cometimplementation.R;
import com.example.cometimplementation.utilities.SharedPrefData;
import com.example.cometimplementation.utilities.Utilities;
import com.squareup.picasso.Picasso;

import java.util.List;


public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.myViewHolder> {
    Context context;
    List<BaseMessage> chatMessageModels;

    public ChatAdapter(Context context, List<BaseMessage> chatMessageModels) {
        this.context = context;
        this.chatMessageModels = chatMessageModels;
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_layout, parent, false);

        return new myViewHolder(view);
    }

    @SuppressLint("RtlHardcoded")
    @Override
    public void onBindViewHolder(@NonNull myViewHolder holder, @SuppressLint("RecyclerView") int position) {
        if (!chatMessageModels.get(position).getReceiverUid().equals(SharedPrefData.getUserId(context)))
            senderSideSettings(holder);
        else
            receiverSideSettings(holder);


        if (chatMessageModels.get(position) instanceof TextMessage) {
            TextMessage textMessage = (TextMessage) chatMessageModels.get(position);
            setTextMessages(textMessage, holder);
        } else if (chatMessageModels.get(position) instanceof MediaMessage) {
            MediaMessage mediaMessage = (MediaMessage) chatMessageModels.get(position);
            setMediaMessage(mediaMessage, holder);

        } else if(chatMessageModels.get(position) instanceof Action){
//            Action action= (Action) chatMessageModels.get(position);
//            TextMessage textMessage= (TextMessage) action.getActionOn();
//            setTextMessages(textMessage,holder);
            holder.message_holder.setVisibility(View.GONE);
//            BaseMessage baseMessage = chatMessageModels.get(position);
//            Log.d("check_qwer", "onBindViewHolder: " + baseMessage.getDeletedAt());

        }

//        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View view) {
//                Toast.makeText(context, chatMessageModels.get(position).getId() + "", Toast.LENGTH_SHORT).show();
//
//                return true;
//            }
//        });
    }

    private void setMediaMessage(MediaMessage mediaMessage, myViewHolder holder) {
        CometChat.markAsRead(mediaMessage.getId(), mediaMessage.getSender().getUid(), CometChatConstants.RECEIVER_TYPE_USER);
        holder.message.setText(mediaMessage.getAttachment().getFileUrl());
        holder.message.setVisibility(View.GONE);
        holder.image.setVisibility(View.VISIBLE);
        Picasso.get().load(mediaMessage.getAttachment().getFileUrl()).into(holder.image);
    }

    private void setTextMessages(TextMessage textMessage, myViewHolder holder) {
        CometChat.markAsRead(textMessage.getId(), textMessage.getSender().getUid(), CometChatConstants.RECEIVER_TYPE_USER);
        if (textMessage.getText() == null)
            holder.message.setText("this message was deleted");
        else
            holder.message.setText(textMessage.getText());
        holder.time.setText(Utilities.convertMillisToTime(textMessage.getDeliveredAt()));
        holder.image.setVisibility(View.GONE);
    }

    @SuppressLint("ResourceAsColor")
    private void receiverSideSettings(myViewHolder holder) {
        holder.message_container.setGravity(Gravity.START);
        holder.message_holder.setBackground(ContextCompat.getDrawable(context, R.drawable.receivers_background));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(10, 10, 100, 10);
        holder.message_holder.setLayoutParams(params);
    }

    private void senderSideSettings(myViewHolder holder) {
        holder.message_container.setGravity(Gravity.END);
        holder.message_holder.setBackground(ContextCompat.getDrawable(context, R.drawable.sender_background));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(100, 10, 10, 10);
        holder.message_holder.setLayoutParams(params);
    }

    @Override
    public int getItemCount() {
        return chatMessageModels.size();
    }

    public class myViewHolder extends RecyclerView.ViewHolder {
        TextView message, time;
        LinearLayout message_container, message_holder;
        ImageView image;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.message);
            message_holder = itemView.findViewById(R.id.message_holder);
            time = itemView.findViewById(R.id.time);
            message_container = itemView.findViewById(R.id.message_container);
            image = itemView.findViewById(R.id.image);
        }
    }
}
