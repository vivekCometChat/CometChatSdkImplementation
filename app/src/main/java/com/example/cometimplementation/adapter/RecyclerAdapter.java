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
import androidx.recyclerview.widget.RecyclerView;

import com.example.cometimplementation.R;
import com.example.cometimplementation.activities.ChatActivity;
import com.example.cometimplementation.models.UserPojo;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.myViewHolder> {
    Context context;
    List<UserPojo> userPojoList;

    public RecyclerAdapter(Context context, List<UserPojo> userPojoList) {
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

        holder.name.setText(userPojoList.get(position).getName());
        holder.number.setText(userPojoList.get(position).getNumber());
        Log.d("see_image", "onBindViewHolder: " + userPojoList.get(position).getImg_url());
        Picasso.get().load(userPojoList.get(position).getImg_url()).error(R.drawable.ic_launcher_background).into(holder.circular_img);
        holder.chat.setOnClickListener(v -> {
            Intent i = new Intent(context, ChatActivity.class);
            i.putExtra("name", userPojoList.get(position).getName());
            i.putExtra("uid", userPojoList.get(position).getNumber());
            i.putExtra("img_url", userPojoList.get(position).getImg_url());
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
        ImageView chat;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            number = itemView.findViewById(R.id.number);
            circular_img = itemView.findViewById(R.id.circular_img);
            chat = itemView.findViewById(R.id.chat);

        }
    }
}
