package com.example.cometimplementation.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cometchat.pro.models.User;
import com.example.cometimplementation.ApiCalls;
import com.example.cometimplementation.AppConfig;
import com.example.cometimplementation.Listeners;
import com.example.cometimplementation.R;
import com.example.cometimplementation.models.UserPojo;

import java.util.List;

public class ContactRecycler extends RecyclerView.Adapter<ContactRecycler.myViewHolder> {
    Context context;
    List<UserPojo> list;
    Listeners listeners;
    public ContactRecycler(Listeners listeners,Context context, List<UserPojo> list) {
        this.context = context;
        this.list = list;
        this.listeners=listeners;
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_recycler_lay, parent, false);
        return new ContactRecycler.myViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolder holder, int position) {

        holder.name.setText(list.get(position).getName());
        holder.number.setText(list.get(position).getNumber());
        holder.itemView.setOnClickListener(v -> {

            User user=new User();
            user.setUid(list.get(position).getNumber());
            user.setName(list.get(position).getName());
            user.setAvatar(AppConfig.AVATAR);

            ApiCalls.createUser(user,context,listeners);

        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class myViewHolder extends RecyclerView.ViewHolder {
        TextView name, number;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            number = itemView.findViewById(R.id.number);


        }
    }
}
