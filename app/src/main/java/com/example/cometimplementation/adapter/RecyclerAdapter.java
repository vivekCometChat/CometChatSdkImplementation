package com.example.cometimplementation.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.cometchat.pro.constants.CometChatConstants;
import com.cometchat.pro.models.User;
import com.example.cometimplementation.R;
import com.example.cometimplementation.activities.ChatActivity;
import com.example.cometimplementation.models.UserPojo;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.myViewHolder> {
    Context context;
    List<User> userPojoList;

    public RecyclerAdapter(Context context, List<User> userPojoList) {
        this.context = context;
        this.userPojoList = userPojoList;
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_lay, parent, false);

        return new RecyclerAdapter.myViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolder holder, int position) {

        holder.status.setVisibility(View.VISIBLE);
        holder.name.setText(userPojoList.get(position).getName());
        holder.number.setText(userPojoList.get(position).getStatus());
        Log.e("check_status", "onBindViewHolder: "+ userPojoList.get(position).getStatus());
        Picasso.get().load(userPojoList.get(position).getAvatar()).error(R.drawable.ic_launcher_background).into(holder.circular_img);

        if (userPojoList.get(position).getStatus().equals(CometChatConstants.USER_STATUS_ONLINE)) {
            holder.status.setColorFilter(ContextCompat.getColor(context, R.color.online_green), android.graphics.PorterDuff.Mode.MULTIPLY);
        } else if (userPojoList.get(position).getStatus().equals(CometChatConstants.USER_STATUS_OFFLINE)) {
            holder.status.setColorFilter(ContextCompat.getColor(context, R.color.red), android.graphics.PorterDuff.Mode.MULTIPLY);
        }

        holder.chat.setOnClickListener(v -> {
            Intent i = new Intent(context, ChatActivity.class);
            i.putExtra("name", userPojoList.get(position).getName());
            i.putExtra("uid", userPojoList.get(position).getUid());
            i.putExtra("img_url", userPojoList.get(position).getAvatar());
            i.putExtra("isGroup", false);
            holder.itemView.getContext().startActivity(i);
        });

    }

    @Override
    public int getItemCount() {
        return userPojoList.size();
    }

    public class myViewHolder extends RecyclerView.ViewHolder {

        TextView name, number;
        CircleImageView circular_img;
        ImageView chat, status;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            number = itemView.findViewById(R.id.number);
            circular_img = itemView.findViewById(R.id.circular_img);
            chat = itemView.findViewById(R.id.chat);
            status = itemView.findViewById(R.id.status);

        }
    }
}
