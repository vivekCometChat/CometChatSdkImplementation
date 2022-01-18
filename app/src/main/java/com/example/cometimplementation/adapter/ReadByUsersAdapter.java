package com.example.cometimplementation.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cometchat.pro.models.MessageReceipt;
import com.example.cometimplementation.R;
import com.example.cometimplementation.utilities.Utilities;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ReadByUsersAdapter extends RecyclerView.Adapter<ReadByUsersAdapter.myViewHolder> {
    Context context;
    List<MessageReceipt> messageReceipts;

    public ReadByUsersAdapter(Context context, List<MessageReceipt> messageReceipts) {
        this.context = context;
        this.messageReceipts = messageReceipts;
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_lay, parent, false);
        return new ReadByUsersAdapter.myViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolder holder, int position) {

        holder.chat.setVisibility(View.GONE);
        Picasso.get().load(messageReceipts.get(position).getSender().getAvatar()).into(holder.circular_img);
        holder.name.setText(messageReceipts.get(position).getSender().getName());
        Log.e("read_at_message_reading", "onBindViewHolder: " + messageReceipts.get(position).getReadAt());
        holder.read_at.setText("Read at  " + Utilities.convertMillisToTime(messageReceipts.get(position).getReadAt() * 1000));


    }

    @Override
    public int getItemCount() {
        return messageReceipts.size();
    }

    public class myViewHolder extends RecyclerView.ViewHolder {
        CircleImageView circular_img;
        TextView name, read_at;
        ImageView chat;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            circular_img = itemView.findViewById(R.id.circular_img);
            name = itemView.findViewById(R.id.name);
            chat = itemView.findViewById(R.id.chat);
            read_at = itemView.findViewById(R.id.number);

        }
    }
}
