package com.nsromapa.uchat.recyclerviewchatfragment;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.nsromapa.uchat.ChatActivity;
import com.nsromapa.uchat.R;
import com.squareup.picasso.Picasso;

import java.util.List;


public class ChatFragmentAdapater extends RecyclerView.Adapter<ChatFragmentViewHolder>{

    private List<ChatFragmentObject> usersList;
    private Context context;

    public ChatFragmentAdapater(List<ChatFragmentObject> usersList, Context context){
        this.usersList = usersList;
        this.context = context;
    }


    @Override
    public ChatFragmentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recylerview_fragment_chat_item, null);
        ChatFragmentViewHolder rcv = new ChatFragmentViewHolder(layoutView);
        return rcv;
    }

    @Override
    public void onBindViewHolder(final ChatFragmentViewHolder holder, final int position) {

        if (usersList.get(position).getProfileImageUrl() == null || usersList.get(position).getProfileImageUrl().equals("default")) {
            holder.profileImageView.setImageResource(R.drawable.profile_image);
        } else {
            Picasso.get()
                    .load(usersList.get(position).getProfileImageUrl())
                    .placeholder(R.drawable.profile_image)
                    .into(holder.profileImageView);
        }

        holder.mName.setText(usersList.get(position).getName());
        holder.mState.setText(usersList.get(position).getState());


        holder.fullLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,ChatActivity.class);
                intent.putExtra("receiver_user_id",usersList.get(position).getUid());
                intent.putExtra("receiver_user_name",usersList.get(position).getName());
                intent.putExtra("receiver_user_image_url",usersList.get(position).getProfileImageUrl());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.usersList.size();
    }
}
