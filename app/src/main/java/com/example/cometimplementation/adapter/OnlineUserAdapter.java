package com.example.cometimplementation.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cometchat.pro.constants.CometChatConstants;
import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.models.User;
import com.example.cometimplementation.R;
import com.example.cometimplementation.activities.ChatActivity;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class OnlineUserAdapter extends RecyclerView.Adapter<OnlineUserAdapter.myViewHolder> {

    List<User> userList;
    Context context;

    public OnlineUserAdapter(List<User> userList, Context context) {
        this.userList = userList;
        this.context = context;
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.online_offline_users_row, parent, false);
        return new OnlineUserAdapter.myViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolder holder, int position) {

        User user = userList.get(position);
        holder.name.setText(user.getName());
        holder.user_status.setText(user.getStatusMessage() == null ? "Hey There I am using CometChat" : user.getStatusMessage());
        Picasso.get().load(user.getAvatar()).error(R.drawable.ic_launcher_background).into(holder.user_image);

        if (user.getStatus().equals(CometChatConstants.USER_STATUS_ONLINE)) {
            holder.online_mark.setVisibility(View.VISIBLE);
            holder.offline_mark.setVisibility(View.GONE);
        } else {
            holder.online_mark.setVisibility(View.GONE);
            holder.offline_mark.setVisibility(View.VISIBLE);
        }

        holder.itemView.setOnClickListener(view -> {
            Intent i = new Intent(context, ChatActivity.class);
            i.putExtra("name", user.getName());
            i.putExtra("uid", user.getUid());
            i.putExtra("img_url", user.getAvatar());
            holder.itemView.getContext().startActivity(i);
        });

    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class myViewHolder extends RecyclerView.ViewHolder {

        CircleImageView user_image, offline_mark;
        ImageView online_mark;
        TextView name, user_status;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);

            user_image = itemView.findViewById(R.id.user_image);
            offline_mark = itemView.findViewById(R.id.offline_mark);
            online_mark = itemView.findViewById(R.id.online_mark);
            name = itemView.findViewById(R.id.name);
            user_status = itemView.findViewById(R.id.user_status);

        }
    }
}
