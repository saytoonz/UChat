package com.nsromapa.uchat.recyclerviewreceiver;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nsromapa.uchat.R;
import com.squareup.picasso.Picasso;

import java.util.List;


public class ReceiverAdapater extends RecyclerView.Adapter<ReceiverViewHolder>{

    private List<ReceiverObject> usersList;
    private Context context;

    public ReceiverAdapater(List<ReceiverObject> usersList, Context context){
        this.usersList = usersList;
        this.context = context;
    }
    @Override
    public ReceiverViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_receiver_item, null);
        ReceiverViewHolder rcv = new ReceiverViewHolder(layoutView);
        return rcv;
    }

    @Override
    public void onBindViewHolder(final ReceiverViewHolder holder, int position) {

        if (usersList.get(position).getProfileImageUrl() == null || usersList.get(position).getProfileImageUrl().equals("default")) {
            holder.profileImageView.setImageResource(R.drawable.profile_image);
        } else {
            Picasso.get()
                    .load(usersList.get(position).getProfileImageUrl())
                    .placeholder(R.drawable.profile_image)
                    .into(holder.profileImageView);
        }

        holder.mName.setText(usersList.get(position).getName());
        holder.mIndex.setText(usersList.get(position).getIndex());
        holder.mReceive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean receiveState = !usersList.get(holder.getLayoutPosition()).getReceive();
                usersList.get(holder.getLayoutPosition()).setReceive(receiveState);
            }
        });

        holder.addRecieverFullLinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean receiveState = !usersList.get(holder.getLayoutPosition()).getReceive();
                usersList.get(holder.getLayoutPosition()).setReceive(receiveState);
                if (receiveState){
                    holder.mReceive.setChecked(true);
                }else {
                    holder.mReceive.setChecked(false);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.usersList.size();
    }
}
