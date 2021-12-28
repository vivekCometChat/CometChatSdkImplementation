package com.example.cometimplementation.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cometchat.pro.models.Conversation;
import com.example.cometimplementation.R;
import com.example.cometimplementation.utilities.SharedPrefData;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.myViewHolder> {
    List<Conversation> conversations;
    Context context;

    public ConversationAdapter(List<Conversation> conversations, Context context) {
        this.conversations = conversations;
        this.context = context;
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recent_chat_row, parent, false);
        return new ConversationAdapter.myViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolder holder, int position) {

        Conversation conversation = conversations.get(position);
        try {
            holder.name.setText(conversation.getLastMessage().getSender().getName());
            holder.last_message.setText(conversation.getLastMessage().getSender().getUid());
            if (conversation.getUnreadMessageCount() > 0) {
                holder.unread_count.setVisibility(View.VISIBLE);
                holder.unread_count.setText(" " + conversation.getUnreadMessageCount() + " ");
            }
            if (!conversation.getLastMessage().getSender().getAvatar().isEmpty()) {
                Picasso.get().load(conversation.getLastMessage().getSender().getAvatar()).error(R.drawable.user_deafult).into(holder.user_image);
            } else {
                holder.user_image.setImageDrawable(context.getResources().getDrawable(R.drawable.user_deafult));
            }
            if (conversation.getLastMessage().getType().equals("text")) {
                holder.last_message.setText(conversation.getLastMessage().getRawMessage().getJSONObject("data").getString("text"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return conversations.size();
    }

    public class myViewHolder extends RecyclerView.ViewHolder {

        CircleImageView user_image;
        TextView name, last_message, time, unread_count;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);

            user_image = itemView.findViewById(R.id.user_image);
            name = itemView.findViewById(R.id.name);
            last_message = itemView.findViewById(R.id.last_message);
            time = itemView.findViewById(R.id.time);
            unread_count = itemView.findViewById(R.id.unread_count);

        }
    }
}
