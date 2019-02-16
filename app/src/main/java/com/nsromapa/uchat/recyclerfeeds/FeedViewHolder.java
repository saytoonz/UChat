package com.nsromapa.uchat.recyclerfeeds;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nsromapa.say.emogifstickerkeyboard.widget.EmoticonTextView;
import com.nsromapa.uchat.R;

import de.hdodenhof.circleimageview.CircleImageView;

class FeedViewHolder extends RecyclerView.ViewHolder {
    public CircleImageView PostedUserFromPFP;
    public TextView PosterUserName, PostCreationTime;

    public ImageView PostImageVideo_ImageView;
    public EmoticonTextView PostTextpost_TextView;
    public EmoticonTextView PostCaption_TextView;

    public LinearLayout PostActionButtons_likeUnlike, PostActionButtons_hate;
    public LinearLayout PostActionButtons_comment, PostActionButtons_delete;
    public TextView postTotal_likers, postTotal_haters, postTotal_commenters;

    public FeedViewHolder(@NonNull View itemView) {
        super(itemView);

        PostedUserFromPFP = itemView.findViewById(R.id.PostedUserFromPFP);
        PosterUserName = itemView.findViewById(R.id.PosterUserName);
        PostCreationTime = itemView.findViewById(R.id.PostCreationTime);

        PostImageVideo_ImageView = itemView.findViewById(R.id.PostImageVideo_ImageView);
        PostTextpost_TextView = itemView.findViewById(R.id.PostTextpost_TextView);
        PostCaption_TextView = itemView.findViewById(R.id.PostCaption_TextView);

        PostActionButtons_likeUnlike = itemView.findViewById(R.id.PostActionButtons_likeUnlike);
        postTotal_likers = itemView.findViewById(R.id.postTotal_likers);
        PostActionButtons_hate = itemView.findViewById(R.id.PostActionButtons_hate);
        postTotal_haters = itemView.findViewById(R.id.postTotal_haters);
        PostActionButtons_comment = itemView.findViewById(R.id.PostActionButtons_comment);
        postTotal_commenters = itemView.findViewById(R.id.postTotal_commenters);
        PostActionButtons_delete = itemView.findViewById(R.id.PostActionButtons_delete);
        PostActionButtons_comment = itemView.findViewById(R.id.PostActionButtons_comment);
        PostActionButtons_comment = itemView.findViewById(R.id.PostActionButtons_comment);

    }
}
