package com.nsromapa.uchat.recyclerviewstory;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nsromapa.uchat.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class StoryViewHolders extends RecyclerView.ViewHolder{
     TextView mEmail, number_of_stories, story_item_last_story_time;
     LinearLayout mLayout;
    CircleImageView profileImage;

    public StoryViewHolders(View itemView){
        super(itemView);
        mEmail = itemView.findViewById(R.id.email);
        mLayout = itemView.findViewById(R.id.layout);
        profileImage = itemView.findViewById(R.id.story_item_profileImageView);
        story_item_last_story_time = itemView.findViewById(R.id.story_item_last_story_time);
        number_of_stories = itemView.findViewById(R.id.number_of_stories);

    }

}
