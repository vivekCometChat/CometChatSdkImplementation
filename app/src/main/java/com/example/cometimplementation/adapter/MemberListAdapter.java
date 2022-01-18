package com.example.cometimplementation.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cometchat.pro.models.GroupMember;
import com.example.cometimplementation.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MemberListAdapter extends RecyclerView.Adapter<MemberListAdapter.myViewHolder> {
    Context context;
    List<GroupMember> groupMembers;

    public MemberListAdapter(Context context, List<GroupMember> groupMembers) {
        this.context = context;
        this.groupMembers = groupMembers;
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_lay,parent,false);
        return new MemberListAdapter.myViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolder holder, int position) {
        holder.chat.setVisibility(View.GONE);
        GroupMember member=groupMembers.get(position);
        Picasso.get().load(member.getAvatar()).into(holder.circular_img);
        holder.name.setText(member.getName());
        holder.status.setText(member.getScope());

    }

    @Override
    public int getItemCount() {
        return groupMembers.size();
    }

    public class myViewHolder extends RecyclerView.ViewHolder {
        TextView name, status;
        CircleImageView circular_img;
        ImageView chat;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            status = itemView.findViewById(R.id.number);
            circular_img = itemView.findViewById(R.id.circular_img);
            chat = itemView.findViewById(R.id.chat);
        }
    }
}
