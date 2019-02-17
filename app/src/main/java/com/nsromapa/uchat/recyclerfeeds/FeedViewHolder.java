package com.nsromapa.uchat.recyclerfeeds;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nsromapa.emoticompack.samsung.SamsungEmoticonProvider;
import com.nsromapa.say.emogifstickerkeyboard.widget.EmoticonEditText;
import com.nsromapa.say.emogifstickerkeyboard.widget.EmoticonTextView;
import com.nsromapa.uchat.R;

import de.hdodenhof.circleimageview.CircleImageView;

class FeedViewHolder extends RecyclerView.ViewHolder {
    public CircleImageView PostedUserFromPFP;
    public TextView PosterUserName, PostCreationTime;

    public ImageView PostImageVideo_ImageView;
    public ImageView post_VideoThumbnail_play;
    public EmoticonTextView PostTextpost_TextView;
    public EmoticonTextView PostCaption_TextView;

    public LinearLayout PostActionButtons_likeUnlike, PostActionButtons_hateUnhate;
    public ImageView PostActionButtons_likeUnlike_Image,PostActionButtons_hateUnhate_Image;
    public LinearLayout PostActionButtons_comment;
    public ImageButton PostActionButtons_delete;
    public TextView postTotal_likers, postTotal_haters, postTotal_commenters;

    public RecyclerView comments_RecyclerView;
    public LinearLayout create_New_Comment;
    public EmoticonEditText CreateComment_TextEdit;
    public Button Send_Comment_Btn;



    public FeedViewHolder(@NonNull View itemView) {
        super(itemView);

        PostedUserFromPFP = itemView.findViewById(R.id.PostedUserFromPFP);
        PosterUserName = itemView.findViewById(R.id.PosterUserName);
        PostCreationTime = itemView.findViewById(R.id.PostCreationTime);

        PostImageVideo_ImageView = itemView.findViewById(R.id.PostImageVideo_ImageView);
        post_VideoThumbnail_play = itemView.findViewById(R.id.post_VideoThumbnail_play);
        PostTextpost_TextView = itemView.findViewById(R.id.PostTextpost_TextView);
        PostCaption_TextView = itemView.findViewById(R.id.PostCaption_TextView);

        PostActionButtons_likeUnlike = itemView.findViewById(R.id.PostActionButtons_likeUnlike);
        PostActionButtons_likeUnlike_Image = itemView.findViewById(R.id.PostActionButtons_likeUnlike_Image);
        postTotal_likers = itemView.findViewById(R.id.postTotal_likers);

        PostActionButtons_hateUnhate = itemView.findViewById(R.id.PostActionButtons_hateUnhate);
        PostActionButtons_hateUnhate_Image = itemView.findViewById(R.id.PostActionButtons_hateUnhate_Image);
        postTotal_haters = itemView.findViewById(R.id.postTotal_haters);

        PostActionButtons_comment = itemView.findViewById(R.id.PostActionButtons_comment);
        postTotal_commenters = itemView.findViewById(R.id.postTotal_commenters);

        PostActionButtons_delete = itemView.findViewById(R.id.PostActionButtons_delete);


        PostTextpost_TextView.setEmoticonProvider(SamsungEmoticonProvider.create());
        PostCaption_TextView.setEmoticonProvider(SamsungEmoticonProvider.create());



        comments_RecyclerView = itemView.findViewById(R.id.comments_RecyclerView);
        create_New_Comment = itemView.findViewById(R.id.create_New_Comment);
        CreateComment_TextEdit = itemView.findViewById(R.id.CreateComment_TextEdit);
        Send_Comment_Btn = itemView.findViewById(R.id.Send_Comment_Btn);
    }
}
