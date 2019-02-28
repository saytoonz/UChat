package com.nsromapa.uchat.recyclerfeeds;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

public class PostCommentAdapter extends RecyclerView.Adapter<PostCommentAdapter.CommentsViewHolder> {

    @NonNull
    @Override
    public PostCommentAdapter.CommentsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull PostCommentAdapter.CommentsViewHolder commentsViewHolder, int i) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }


    public class CommentsViewHolder extends RecyclerView.ViewHolder {
        public CommentsViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
