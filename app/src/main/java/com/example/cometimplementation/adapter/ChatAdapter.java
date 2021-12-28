package com.example.cometimplementation.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.cometchat.pro.models.BaseMessage;
import com.cometchat.pro.models.MediaMessage;
import com.cometchat.pro.models.TextMessage;
import com.example.cometimplementation.R;
import com.example.cometimplementation.utilities.SharedPrefData;
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
    public void onBindViewHolder(@NonNull myViewHolder holder, int position) {
        if (!chatMessageModels.get(position).getReceiverUid().equals(SharedPrefData.getUserId(context))) {
            holder.message_container.setGravity(Gravity.END);
            holder.message.setBackground(ContextCompat.getDrawable(context, R.drawable.sender_background));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(60, 10, 10, 10);
            holder.message.setLayoutParams(params);
            params.setMargins(100, 10, 10, 10);
            holder.image.setLayoutParams(params);

        } else {
            holder.message_container.setGravity(Gravity.START);
            holder.message.setBackground(ContextCompat.getDrawable(context, R.drawable.receivers_background));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(10, 10, 60, 10);
            holder.message.setLayoutParams(params);
            params.setMargins(10, 10, 100, 10);
            holder.image.setLayoutParams(params);

        }
        if (chatMessageModels.get(position) instanceof TextMessage) {
            TextMessage textMessage = (TextMessage) chatMessageModels.get(position);
            holder.message.setText(textMessage.getText());
            holder.image.setVisibility(View.GONE);
        } else if (chatMessageModels.get(position) instanceof MediaMessage) {
            MediaMessage textMessage = (MediaMessage) chatMessageModels.get(position);
            holder.message.setText(textMessage.getAttachment().getFileUrl());
            holder.message.setVisibility(View.GONE);
            holder.image.setVisibility(View.VISIBLE);
            Picasso.get().load(textMessage.getAttachment().getFileUrl()).into(holder.image);
        }
    }

    @Override
    public int getItemCount() {
        return chatMessageModels.size();
    }

    public class myViewHolder extends RecyclerView.ViewHolder {
        TextView message;
        LinearLayout message_container;
        ImageView image;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.message);
            message_container = itemView.findViewById(R.id.message_container);
            image = itemView.findViewById(R.id.image);
        }
    }
}
