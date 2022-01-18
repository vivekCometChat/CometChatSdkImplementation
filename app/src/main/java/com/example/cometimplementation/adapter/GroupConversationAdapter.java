package com.example.cometimplementation.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cometchat.pro.models.Conversation;
import com.cometchat.pro.models.Group;
import com.cometchat.pro.models.TextMessage;
import com.cometchat.pro.models.User;
import com.example.cometimplementation.R;
import com.example.cometimplementation.activities.ChatActivity;
import com.example.cometimplementation.utilities.SharedPrefData;
import com.example.cometimplementation.utilities.Utilities;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupConversationAdapter extends RecyclerView.Adapter<GroupConversationAdapter.myViewHolder> {

    List<Conversation> conversations;
    Context context;
    boolean isGroup;

    public GroupConversationAdapter(List<Conversation> conversations, Context context, boolean isGroup) {
        this.conversations = conversations;
        this.context = context;
        this.isGroup = isGroup;
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recent_chat_row, parent, false);
        return new GroupConversationAdapter.myViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolder holder, int position) {

        Conversation conversation = conversations.get(position);

        Group group = (Group) conversation.getLastMessage().getReceiver();
        try {
            holder.name.setText(group.getName());
            if (!group.getIcon().isEmpty())
                Picasso.get().load(group.getIcon()).error(R.drawable.user_deafult).into(holder.user_image);
            else
                holder.user_image.setImageDrawable(context.getResources().getDrawable(R.drawable.user_deafult));

            if (conversation.getUnreadMessageCount() > 0) {
                holder.unread_count.setVisibility(View.VISIBLE);
                holder.unread_count.setText(" " + conversation.getUnreadMessageCount() + " ");
            } else {
                holder.unread_count.setVisibility(View.GONE);
            }

            if (conversation.getLastMessage().getType().equals("text")) {
                holder.last_message.setVisibility(View.VISIBLE);
                holder.img_message.setVisibility(View.GONE);
                holder.calls.setVisibility(View.GONE);
                TextMessage textMessage = (TextMessage) conversation.getLastMessage();
                if (textMessage.getText() == null)
                    holder.last_message.setText("this message was deleted");
                else
                    holder.last_message.setText(textMessage.getText());

            } else if (conversation.getLastMessage().getType().equals("image")) {
                holder.last_message.setVisibility(View.GONE);
                holder.img_message.setVisibility(View.VISIBLE);
                holder.calls.setVisibility(View.GONE);

            } else if (conversation.getLastMessage().getType().equals("video")) {
                holder.calls.setVisibility(View.VISIBLE);
                holder.last_message.setVisibility(View.GONE);
                holder.img_message.setVisibility(View.GONE);
                if (conversation.getLastMessage().getRawMessage().getJSONObject("data").getString("action").equals("rejected")) {
                    holder.rejected.setVisibility(View.VISIBLE);
                    holder.accept_call.setVisibility(View.GONE);
                } else {
                    holder.rejected.setVisibility(View.GONE);
                    holder.accept_call.setVisibility(View.VISIBLE);
                }
                holder.call_status.setText(conversation.getLastMessage().getRawMessage().getJSONObject("data").getString("action"));


            }
            Log.d("check_type", "onBindViewHolder: " + conversation.getLastMessage().getType());
            holder.itemView.setOnClickListener(view -> {
                Intent i = new Intent(context, ChatActivity.class);
                i.putExtra("name", group.getName());
                i.putExtra("uid", group.getGuid());
                i.putExtra("img_url", group.getIcon());
                i.putExtra("isGroup", isGroup);
                holder.itemView.getContext().startActivity(i);
            });
            holder.time.setText(Utilities.convertMillisToTime(conversation.getLastMessage().getDeliveredAt()));

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
        }


    }

    @Override
    public int getItemCount() {
        return conversations.size();
    }

    public class myViewHolder extends RecyclerView.ViewHolder {

        CircleImageView user_image;
        ImageView img_message, rejected, accept_call;
        TextView name, last_message, time, unread_count, call_status;
        LinearLayout calls;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);

            user_image = itemView.findViewById(R.id.user_image);
            calls = itemView.findViewById(R.id.calls);
            img_message = itemView.findViewById(R.id.img_message);
            rejected = itemView.findViewById(R.id.rejected);
            accept_call = itemView.findViewById(R.id.accept_call);
            name = itemView.findViewById(R.id.name);
            call_status = itemView.findViewById(R.id.call_status);
            last_message = itemView.findViewById(R.id.last_message);
            time = itemView.findViewById(R.id.time);
            unread_count = itemView.findViewById(R.id.unread_count);
        }
    }
}
