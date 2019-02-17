package com.nsromapa.uchat.recyclerfeeds;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.nsromapa.uchat.R;

import java.util.List;

public class FeedsAdapter extends RecyclerView.Adapter<FeedViewHolder> {
    private FirebaseAuth mAuth;
    private DatabaseReference mRootRef;

    List<FeedsObjects> postLists;
    private Context mContext;
    private String currentUserID;

    public FeedsAdapter(List<FeedsObjects> postLists, Context mContext) {
        this.postLists = postLists;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public FeedViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.recylerview_fragment_feeds_item, viewGroup, false);
        mRootRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        return new FeedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FeedViewHolder feedViewHolder, final int position) {

        final FeedsObjects post = postLists.get(position);

        feedViewHolder.PostImageVideo_ImageView.setVisibility(View.GONE);
        feedViewHolder.PostTextpost_TextView.setVisibility(View.GONE);
        feedViewHolder.PostCaption_TextView.setVisibility(View.GONE);
        feedViewHolder.PostActionButtons_delete.setVisibility(View.GONE);


        feedViewHolder.PostCreationTime.setText(post.getDate() + " - " + post.getTime());


        if (post.getType().equals("test_post")) {
            feedViewHolder.PostTextpost_TextView.setVisibility(View.VISIBLE);

            String fontFamily = "AlexBrush_Regular.ttf";
            if (!TextUtils.isEmpty(post.getStyle())) {
                fontFamily = post.getStyle();
            }
            String background = "post_background_transparent";
            if (!TextUtils.isEmpty(post.getBackground())) {
                background = post.getBackground();
            }
            float textSize = 16f;
            if (!TextUtils.isEmpty(post.getSize())) {
                textSize = Float.parseFloat(post.getSize());
            }

            Typeface typeface = Typeface.createFromAsset(mContext.getAssets(),
                    "fonts/" + fontFamily);
            feedViewHolder.PostTextpost_TextView.setText(post.getText());
            feedViewHolder.PostTextpost_TextView.setTypeface(typeface);
            feedViewHolder.PostTextpost_TextView.setBackground(GetImage(mContext, background));
            feedViewHolder.PostTextpost_TextView.setTextSize(textSize);

        } else {
            feedViewHolder.PostImageVideo_ImageView.setVisibility(View.VISIBLE);
            Glide.with(mContext)
                    .asBitmap()
                    .load(post.getUrl())
                    .apply(new RequestOptions().placeholder(R.drawable.post_background_transparent))
                    .into(feedViewHolder.PostImageVideo_ImageView);
        }


        feedViewHolder.postTotal_likers.setText(String.valueOf(post.getLikers().size()));
        feedViewHolder.postTotal_haters.setText(String.valueOf(post.getHaters().size()));

        if (post.getLikers().contains(currentUserID)) {
            feedViewHolder.PostActionButtons_likeUnlike_Image.setImageResource(R.drawable.ic_unlike2);
        } else {
            feedViewHolder.PostActionButtons_likeUnlike_Image.setImageResource(R.drawable.ic_like2);
        }
        feedViewHolder.PostActionButtons_likeUnlike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                like_UnlikePostFeed(post.getPostId(), post.getLikers(), position);
            }
        });


        if (post.getFrom().equals(currentUserID)) {
            feedViewHolder.PostActionButtons_delete.setVisibility(View.VISIBLE);
            feedViewHolder.PostActionButtons_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deletePostFeed(post.getPostId(), position);
                }
            });

        } else {
            feedViewHolder.PostActionButtons_delete.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return postLists.size();

    }


    private void like_UnlikePostFeed(String postId, List<String> likers, int position) {

        int totalLikes = likers.size();

        if (likers.contains(currentUserID)) {
            likers.remove(currentUserID);

            mRootRef.child("posts").child(postId)
                    .child("likers")
                    .child(currentUserID).removeValue();


        } else {
            likers.add(currentUserID);

            mRootRef.child("posts").child(postId)
                    .child("likers")
                    .child(currentUserID).setValue(currentUserID);
        }
    }


    private void deletePostFeed(String postId, final int position) {
        FirebaseDatabase.getInstance().getReference()
                .child("posts")
                .child(postId)
                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (!task.isSuccessful()) {
                    Toast.makeText(mContext, "Error: Post was not deleted...", Toast.LENGTH_SHORT).show();
                } else {
                    postLists.remove(position);
                }
            }
        });

        this.notifyDataSetChanged();
    }


    public static Drawable GetImage(Context c, String ImageName) {
        return c.getResources().getDrawable(c.getResources().getIdentifier(ImageName, "drawable", c.getPackageName()));
    }
}
