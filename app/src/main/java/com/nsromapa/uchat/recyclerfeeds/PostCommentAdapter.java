package com.nsromapa.uchat.recyclerfeeds;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.nsromapa.say.emogifstickerkeyboard.widget.EmoticonTextView;
import com.nsromapa.uchat.R;
import com.nsromapa.uchat.utils.FormatterUtil;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostCommentAdapter extends RecyclerView.Adapter<PostCommentAdapter.CommentsViewHolder> {

    private Context mContext;
    private ArrayList<PostCommentObjects> commentsList;

    public PostCommentAdapter(Context mContext, ArrayList<PostCommentObjects> commentsList) {
        this.mContext = mContext;
        this.commentsList = commentsList;
    }

    @NonNull
    @Override
    public PostCommentAdapter.CommentsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.recylerview_postcomment_item, viewGroup,false);
        return new PostCommentAdapter.CommentsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostCommentAdapter.CommentsViewHolder commentsViewHolder, int position) {
        final PostCommentObjects comment = commentsList.get(position);

        commentsViewHolder.Post_commenter_name.setText(comment.getSenderName());
        commentsViewHolder.vPost_comment.setText(comment.getComment());
        commentsViewHolder.vPost_commet_time
                .setText(FormatterUtil.getRelativeTimeSpanString(mContext,Long.parseLong(comment.getCommentId())));

        Glide.with(mContext)
                .asBitmap()
                .apply(new RequestOptions().placeholder(R.drawable.profile_image))
                .load(comment.getSenderImage())
                .into(commentsViewHolder.vPost_commenter_profileImage);
    }

    @Override
    public int getItemCount() {
        return commentsList.size();
    }


    class CommentsViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView vPost_commenter_profileImage;
        private EmoticonTextView Post_commenter_name;
        private EmoticonTextView vPost_comment;
        private TextView vPost_commet_time;

        CommentsViewHolder(@NonNull View itemView) {
            super(itemView);

            vPost_commenter_profileImage = itemView.findViewById(R.id.vPost_commenter_profileImage);
            Post_commenter_name = itemView.findViewById(R.id.Post_commenter_name);
            vPost_comment = itemView.findViewById(R.id.vPost_comment);
            vPost_commet_time = itemView.findViewById(R.id.vPost_commet_time);
        }
    }
}
