package com.nsromapa.uchat.recyclerviewreceiver;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nsromapa.uchat.R;

import de.hdodenhof.circleimageview.CircleImageView;


class ReceiverViewHolder extends RecyclerView.ViewHolder{
    TextView mName;
    TextView mIndex;
    CheckBox mReceive;
    CircleImageView profileImageView;
    LinearLayout addRecieverFullLinear;

    ReceiverViewHolder(View itemView){
        super(itemView);

        addRecieverFullLinear = itemView.findViewById(R.id.addRecieverFullLinear);
        mName = itemView.findViewById(R.id.receiver_item_name);
        mIndex = itemView.findViewById(R.id.receiver_item_index);
        mReceive = itemView.findViewById(R.id.receiver_item_receive);
        profileImageView = itemView.findViewById(R.id.receiver_item_profileImageView);


    }


}
