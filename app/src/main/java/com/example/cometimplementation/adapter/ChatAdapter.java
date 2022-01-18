package com.example.cometimplementation.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
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
import com.example.cometimplementation.Interfaces.SelectedMessageCallBack;
import com.example.cometimplementation.R;
import com.example.cometimplementation.utilities.SharedPrefData;
import com.example.cometimplementation.utilities.Utilities;
import com.google.android.material.imageview.ShapeableImageView;
import com.squareup.picasso.Picasso;

import java.util.List;


public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.myViewHolder> {
    Context context;
    List<BaseMessage> chatMessageModels;
    boolean isGroup;
    int raw_position = -1;
    SelectedMessageCallBack messageCallBack;

    public ChatAdapter(Context context, List<BaseMessage> chatMessageModels, boolean isGroup, SelectedMessageCallBack messageCallBack) {
        this.context = context;
        this.chatMessageModels = chatMessageModels;
        this.isGroup = isGroup;
        this.messageCallBack = messageCallBack;
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
        boolean sender = false;
        if (chatMessageModels.get(position).getSender().getUid().equals(SharedPrefData.getUserId(context))) {
            sender = true;
            BaseMessage message = chatMessageModels.get(position);
            senderSideSettings(holder, message);
        } else {
            sender = false;
            BaseMessage message = chatMessageModels.get(position);
            receiverSideSettings(holder, message);
        }

        if (chatMessageModels.get(position) instanceof TextMessage) {
            TextMessage textMessage = (TextMessage) chatMessageModels.get(position);
            setTextMessages(textMessage, holder, sender);
            Log.e("text_message_receive", "onBindViewHolder: " + textMessage.toString());
        } else if (chatMessageModels.get(position) instanceof MediaMessage) {
            MediaMessage mediaMessage = (MediaMessage) chatMessageModels.get(position);
            setMediaMessage(mediaMessage, holder, sender);
        } else if (chatMessageModels.get(position) instanceof Action) {
            holder.message_holder.setVisibility(View.GONE);
        }

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                raw_position = position;
                if (chatMessageModels.get(position).getSender().getUid().equals(SharedPrefData.getUserId(context)) && isGroup) {

                    if (chatMessageModels.get(position) instanceof TextMessage) {
                        TextMessage textMessage = (TextMessage) chatMessageModels.get(position);
                        messageCallBack.onSelectedMessage(chatMessageModels.get(raw_position).getId(), textMessage.getText(), true);

                    } else if (chatMessageModels.get(position) instanceof MediaMessage) {
                        MediaMessage mediaMessage = (MediaMessage) chatMessageModels.get(position);
                        messageCallBack.onSelectedMessage(chatMessageModels.get(raw_position).getId(), mediaMessage.getAttachment().getFileUrl(), true);
                    }
                    notifyDataSetChanged();
                    return true;
                } else
                    return false;
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                raw_position = -1;
                messageCallBack.onSelectedMessage(-1, "", false);
                holder.message_container.setBackgroundColor(Color.parseColor("#F9F9F9"));

            }
        });
        if (raw_position == position) {
            holder.message_container.setBackgroundColor(Color.parseColor("#c5c5c7"));
            Toast.makeText(context, "long_pressed", Toast.LENGTH_SHORT).show();
        } else {
            holder.message_container.setBackgroundColor(Color.parseColor("#F9F9F9"));
        }
    }

    private void setMediaMessage(MediaMessage mediaMessage, myViewHolder holder, boolean isSender) {

        holder.text_there_side.setVisibility(View.GONE);
        holder.text_my_side.setVisibility(View.GONE);
        if(isSender){

            holder.image_my_side.setVisibility(View.VISIBLE);
            holder.image_there_side.setVisibility(View.GONE);
            Picasso.get().load(mediaMessage.getAttachment().getFileUrl()).into( holder.image_my_side);
            holder.time_sender.setText(Utilities.convertMillisToTime(mediaMessage.getSentAt() * 1000));


        }else{
            holder.image_my_side.setVisibility(View.GONE);
            holder.image_there_side.setVisibility(View.VISIBLE);
            Picasso.get().load(mediaMessage.getAttachment().getFileUrl()).into( holder.image_there_side);
            holder.time_receiver.setText(Utilities.convertMillisToTime(mediaMessage.getSentAt() * 1000));

        }

    }

    private void setTextMessages(TextMessage textMessage, myViewHolder holder, boolean isSender) {

        holder.image_my_side.setVisibility(View.GONE);
        holder.image_there_side.setVisibility(View.GONE);

        if (isSender) {
            if (textMessage.getText() == null) {
                holder.text_my_side.setText("this message was deleted");
            } else {
                holder.text_my_side.setText(textMessage.getText());
            }

            holder.time_sender.setText(Utilities.convertMillisToTime(textMessage.getSentAt() * 1000));


        } else {
            if (textMessage.getText() == null) {
                holder.text_there_side.setText("this message was deleted");
            } else {
                holder.text_there_side.setText(textMessage.getText());
            }
            holder.time_receiver.setText(Utilities.convertMillisToTime(textMessage.getSentAt() * 1000));

        }


    }

    @SuppressLint("ResourceAsColor")
    private void receiverSideSettings(myViewHolder holder, BaseMessage message) {
//        holder.message_container.setGravity(Gravity.START);
//        holder.message_holder.setBackground(ContextCompat.getDrawable(context, R.drawable.receivers_background));
//        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//        params.setMargins(10, 10, 100, 10);
//        holder.message_holder.setLayoutParams(params);
//        holder.delivered.setVisibility(View.GONE);
//        holder.seen.setVisibility(View.GONE);

        // new
        holder.my_side.setVisibility(View.GONE);
        holder.there_side.setVisibility(View.VISIBLE);
        Picasso.get().load(message.getSender().getAvatar()).into(holder.receiver_img);
        if (message instanceof TextMessage) {
            TextMessage textMessage = (TextMessage) message;
            setTextMessages(textMessage, holder, false);
            Log.e("text_message_receive", "onBindViewHolder: " + textMessage.toString());
        } else if (message instanceof MediaMessage) {
            MediaMessage mediaMessage = (MediaMessage) message;
            setMediaMessage(mediaMessage, holder, false);
        }


    }

    private void senderSideSettings(myViewHolder holder, BaseMessage message) {
//        holder.message_container.setGravity(Gravity.END);
//        holder.message_holder.setBackground(ContextCompat.getDrawable(context, R.drawable.sender_background));
//        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//        params.setMargins(100, 10, 10, 10);
//        holder.message_holder.setLayoutParams(params);
//        holder.delivered.setVisibility(View.VISIBLE);

        //new
        holder.my_side.setVisibility(View.VISIBLE);
        holder.there_side.setVisibility(View.GONE);

        if (message instanceof TextMessage) {
            TextMessage textMessage = (TextMessage) message;
            setTextMessages(textMessage, holder, true);
            Log.e("text_message_receive", "onBindViewHolder: " + textMessage.toString());
        } else if (message instanceof MediaMessage) {
            MediaMessage mediaMessage = (MediaMessage) message;
            setMediaMessage(mediaMessage, holder, true);
        }


    }

    @Override
    public int getItemCount() {
        return chatMessageModels.size();
    }

    public class myViewHolder extends RecyclerView.ViewHolder {
        TextView message, time;
        LinearLayout message_container, message_holder;
        ImageView image;
        ImageView delivered, seen;

        //new changes
        LinearLayout my_side, there_side;
        TextView text_my_side, text_there_side, time_receiver, time_sender;
        ImageView image_there_side, image_my_side;
        ShapeableImageView receiver_img;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.message);
            message_holder = itemView.findViewById(R.id.message_holder);
            time = itemView.findViewById(R.id.time);
            delivered = itemView.findViewById(R.id.delivered);
            seen = itemView.findViewById(R.id.seen);
            message_container = itemView.findViewById(R.id.message_container);
            image = itemView.findViewById(R.id.image);


            //new
            receiver_img = itemView.findViewById(R.id.receiver_img);
            image_there_side = itemView.findViewById(R.id.image_there_side);
            image_my_side = itemView.findViewById(R.id.image_my_side);
            text_my_side = itemView.findViewById(R.id.text_my_side);
            text_there_side = itemView.findViewById(R.id.text_there_side);
            time_receiver = itemView.findViewById(R.id.time_receiver);
            time_sender = itemView.findViewById(R.id.time_sender);
            my_side = itemView.findViewById(R.id.my_side);
            there_side = itemView.findViewById(R.id.there_side);


        }
    }
}
