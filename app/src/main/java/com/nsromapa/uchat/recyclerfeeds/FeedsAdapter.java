package com.nsromapa.uchat.recyclerfeeds;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
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
    private final static String TAG = "FeedsAdapter";
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
        feedViewHolder.post_VideoThumbnail_play.setVisibility(View.GONE);
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

        } else if (post.getType().equals("video") || post.getType().equals("image")){
            feedViewHolder.PostImageVideo_ImageView.setVisibility(View.VISIBLE);

            ///Show caption if file has....
            if (!TextUtils.isEmpty(post.getText())){
                feedViewHolder.PostCaption_TextView.setVisibility(View.VISIBLE);
                feedViewHolder.PostCaption_TextView.setText(post.getText());
            }
            //Show play icon on videos
            if (post.getType().equals("video")) {
                feedViewHolder.post_VideoThumbnail_play.setVisibility(View.VISIBLE);
            }else{
                feedViewHolder.post_VideoThumbnail_play.setVisibility(View.GONE);
            }


            Glide.with(mContext)
                    .asBitmap()
                    .load(post.getUrl())
                    .apply(new RequestOptions().placeholder(R.drawable.post_background_transparent))
                    .into(feedViewHolder.PostImageVideo_ImageView);



        }else{
            Log.d(TAG, "onBindViewHolder: Unkown Post type");

        }


        ////Show total likes and hates
        feedViewHolder.postTotal_likers.setText(String.valueOf(post.getLikers().size()));
        feedViewHolder.postTotal_haters.setText(String.valueOf(post.getHaters().size()));

        ////Show whether user has liked already and display the unlike image
        ///else display the like image
        if (post.getLikers().contains(currentUserID)) {
            feedViewHolder.PostActionButtons_likeUnlike_Image.setImageResource(R.drawable.ic_unlike2);
        } else {
            feedViewHolder.PostActionButtons_likeUnlike_Image.setImageResource(R.drawable.ic_like2);
        }


        ////Show whether user has hated already by display the unhate image
        ///else display the hate image
        if (post.getHaters().contains(currentUserID)) {
            feedViewHolder.PostActionButtons_hateUnhate_Image.setImageResource(R.drawable.ic_unhate2);
        } else {
            feedViewHolder.PostActionButtons_hateUnhate_Image.setImageResource(R.drawable.ic_hate2);
        }


        //When the like button is clicked
        feedViewHolder.PostActionButtons_likeUnlike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (post.getLikers().contains(currentUserID)) {
                    mRootRef.child("posts").child(post.getPostId())
                            .child("likers")
                            .child(currentUserID).removeValue();
                } else {
                    mRootRef.child("posts").child(post.getPostId())
                            .child("likers")
                            .child(currentUserID).setValue(currentUserID);
                }
            }
        });


        //When the hate button is clicked
        feedViewHolder.PostActionButtons_hateUnhate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (post.getHaters().contains(currentUserID)) {
                    mRootRef.child("posts").child(post.getPostId())
                            .child("haters")
                            .child(currentUserID).removeValue();
                } else {
                    mRootRef.child("posts").child(post.getPostId())
                            .child("haters")
                            .child(currentUserID).setValue(currentUserID);
                }
            }
        });



        //Show Delete button and add OnClickListener
        if (!post.getFrom().equals(currentUserID)) {
            feedViewHolder.PostActionButtons_delete.setVisibility(View.GONE);

        } else {
            feedViewHolder.PostActionButtons_delete.setVisibility(View.VISIBLE);
            feedViewHolder.PostActionButtons_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FirebaseDatabase.getInstance().getReference()
                            .child("posts")
                            .child(post.getPostId())
                            .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (!task.isSuccessful()) {
                                Toast.makeText(mContext, "Error: Post was not deleted...", Toast.LENGTH_SHORT).show();
                            } else {
//                                postLists.remove(position-1);
                                Toast.makeText(mContext, "Message deleted...\n It will disappear when you restart you app", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            });
        }
    }


    @Override
    public int getItemCount() {
        return postLists.size();

    }



    private static Drawable GetImage(Context c, String ImageName) {
        return c.getResources().getDrawable(c.getResources().getIdentifier(ImageName, "drawable", c.getPackageName()));
    }
}
