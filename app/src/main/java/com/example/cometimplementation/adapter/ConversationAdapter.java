package com.example.cometimplementation.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cometchat.pro.models.Conversation;
import com.example.cometimplementation.R;
import com.example.cometimplementation.activities.ChatActivity;
import com.example.cometimplementation.utilities.SharedPrefData;
import com.example.cometimplementation.utilities.Utilities;
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

            if (!conversation.getLastMessage().getSender().getUid().equals(SharedPrefData.getUserId(context))) {
                holder.name.setText(conversation.getLastMessage().getSender().getName());
                if (!conversation.getLastMessage().getSender().getAvatar().isEmpty())
                    Picasso.get().load(conversation.getLastMessage().getSender().getAvatar()).error(R.drawable.user_deafult).into(holder.user_image);
                else
                    holder.user_image.setImageDrawable(context.getResources().getDrawable(R.drawable.user_deafult));
            } else {
                holder.name.setText(conversation.getLastMessage().getRawMessage().getJSONObject("data").getJSONObject("entities").getJSONObject("receiver").getJSONObject("entity").getString("name"));
                if (!conversation.getLastMessage().getSender().getAvatar().isEmpty())
                    Picasso.get().load(conversation.getLastMessage().getRawMessage().getJSONObject("data").getJSONObject("entities").getJSONObject("receiver").getJSONObject("entity").getString("avatar")).error(R.drawable.user_deafult).into(holder.user_image);
                else
                    holder.user_image.setImageDrawable(context.getResources().getDrawable(R.drawable.user_deafult));
            }
            if (conversation.getUnreadMessageCount() > 0) {
                holder.unread_count.setVisibility(View.VISIBLE);
                holder.unread_count.setText(" " + conversation.getUnreadMessageCount() + " ");
            }else{
                holder.unread_count.setVisibility(View.GONE);
            }
            if (conversation.getLastMessage().getType().equals("text")) {
                holder.last_message.setVisibility(View.VISIBLE);
                holder.last_message.setText(conversation.getLastMessage().getRawMessage().getJSONObject("data").getString("text"));
            }else if(conversation.getLastMessage().getType().equals("image")){
                holder.img_message.setVisibility(View.VISIBLE);
            }
            Log.d("check_type", "onBindViewHolder: "+conversation.getLastMessage().getType());
            holder.itemView.setOnClickListener(v -> {
                try {
                    Intent i = new Intent(context, ChatActivity.class);
                    if (!conversation.getLastMessage().getSender().getUid().equals(SharedPrefData.getUserId(context))) {
                        i.putExtra("name", conversation.getLastMessage().getSender().getName());
                        i.putExtra("uid", conversation.getLastMessage().getSender().getUid());
                        i.putExtra("img_url", conversation.getLastMessage().getSender().getAvatar());
                    } else {
                        i.putExtra("name", conversation.getLastMessage().getRawMessage().getJSONObject("data").getJSONObject("entities").getJSONObject("receiver").getJSONObject("entity").getString("name"));
                        i.putExtra("uid", conversation.getLastMessage().getRawMessage().getJSONObject("data").getJSONObject("entities").getJSONObject("receiver").getJSONObject("entity").getString("uid"));
                        i.putExtra("img_url", conversation.getLastMessage().getRawMessage().getJSONObject("data").getJSONObject("entities").getJSONObject("receiver").getJSONObject("entity").getString("avatar"));
                    }
                    holder.itemView.getContext().startActivity(i);
                } catch (Exception e) {
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

            holder.time.setText(Utilities.convertMillisToTime(conversation.getLastMessage().getDeliveredAt()));
        } catch (Exception e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public int getItemCount() {
        return conversations.size();
    }

    public class myViewHolder extends RecyclerView.ViewHolder {

        CircleImageView user_image;
        ImageView img_message;
        TextView name, last_message, time, unread_count;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);

            user_image = itemView.findViewById(R.id.user_image);
            img_message = itemView.findViewById(R.id.img_message);
            name = itemView.findViewById(R.id.name);
            last_message = itemView.findViewById(R.id.last_message);
            time = itemView.findViewById(R.id.time);
            unread_count = itemView.findViewById(R.id.unread_count);

        }
    }
}
