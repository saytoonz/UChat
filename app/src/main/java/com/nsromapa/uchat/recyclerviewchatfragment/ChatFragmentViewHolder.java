package com.nsromapa.uchat.recyclerviewchatfragment;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nsromapa.uchat.R;

import de.hdodenhof.circleimageview.CircleImageView;


public class ChatFragmentViewHolder extends RecyclerView.ViewHolder{
    public  TextView mName;
    public  TextView mState;
    public  CircleImageView profileImageView;
    public  LinearLayout fullLayout;

    public  ChatFragmentViewHolder(View itemView){
        super(itemView);

        fullLayout = itemView.findViewById(R.id.fragment_chat_item_full_layout);
        mName = itemView.findViewById(R.id.fragment_chat_item_name);
        mState = itemView.findViewById(R.id.fragment_chat_item_state);
        profileImageView = itemView.findViewById(R.id.fragment_chat_item_profileImageView);


    }


}
