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

import com.cometchat.pro.models.Group;
import com.example.cometimplementation.R;
import com.example.cometimplementation.activities.ChatActivity;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ShowGroupAdapter extends RecyclerView.Adapter<ShowGroupAdapter.myViewHolder> {
    Context context;
    List<Group> groupList;

    public ShowGroupAdapter(Context context, List<Group> groupList) {
        this.context = context;
        this.groupList = groupList;
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_lay, parent, false);
        return new ShowGroupAdapter.myViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolder holder, int position) {

        holder.name.setText(groupList.get(position).getName());
        holder.number.setText("Users : "+groupList.get(position).getMembersCount());
        Log.d("see_image", "onBindViewHolder: " + groupList.get(position).getIcon());
        Picasso.get().load(groupList.get(position).getIcon()).error(R.drawable.ic_launcher_background).into(holder.circular_img);
        holder.chat.setOnClickListener(v -> {
            Intent i = new Intent(context, ChatActivity.class);
            i.putExtra("name", groupList.get(position).getName());
            i.putExtra("uid", groupList.get(position).getGuid());
            i.putExtra("img_url", groupList.get(position).getIcon());
            i.putExtra("isGroup",true);

            holder.itemView.getContext().startActivity(i);
        });
    }

    @Override
    public int getItemCount() {
        return groupList.size();
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
