package com.nsromapa.uchat.recyclerviewfollow;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.nsromapa.uchat.R;

import de.hdodenhof.circleimageview.CircleImageView;


class FollowViewHolders extends RecyclerView.ViewHolder{
    TextView mName,mIndex;
    ImageButton mFollow;
    CircleImageView profileImageView;

    FollowViewHolders(View itemView){
        super(itemView);
        mName = itemView.findViewById(R.id.follower_item_name);
        mIndex = itemView.findViewById(R.id.follower_item_index);
        mFollow = itemView.findViewById(R.id.follower_item_follow);
        profileImageView = itemView.findViewById(R.id.follower_item_profileImageView);

    }


}
