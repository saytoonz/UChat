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

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
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

    public FeedsAdapter(List<FeedsObjects> postLists, Context mContext) {
        this.postLists = postLists;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public FeedViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.recylerview_fragment_feeds_item,viewGroup,false);
        mRootRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        return new FeedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FeedViewHolder feedViewHolder, int position) {
        String currentUserID = mAuth.getCurrentUser().getUid();
        final FeedsObjects post = postLists.get(position);


        feedViewHolder.PostImageVideo_ImageView.setVisibility(View.GONE);
        feedViewHolder.PostTextpost_TextView.setVisibility(View.GONE);
        feedViewHolder.PostCaption_TextView.setVisibility(View.GONE);

        if (post.getType().equals("test_post")){
            feedViewHolder.PostTextpost_TextView.setVisibility(View.VISIBLE);

            String fontFamily="AlexBrush_Regular.ttf";
            if (!TextUtils.isEmpty(post.getStyle())){
                fontFamily = post.getStyle();
            }
            String background = "post_background_transparent";
            if (!TextUtils.isEmpty(post.getBackground())){
                background = post.getBackground();
            }
            float textSize = 16f;
            if (!TextUtils.isEmpty(post.getSize())){
                textSize= Float.parseFloat(post.getSize());
            }

            Typeface typeface = Typeface.createFromAsset(mContext.getAssets(),
                    "fonts/"+fontFamily);
            feedViewHolder.PostTextpost_TextView.setText(post.getText());
            feedViewHolder.PostTextpost_TextView.setTypeface(typeface);
            feedViewHolder.PostTextpost_TextView.setBackground(GetImage(mContext,background));
            feedViewHolder.PostTextpost_TextView.setTextSize(textSize);

        }else{
            feedViewHolder.PostImageVideo_ImageView.setVisibility(View.VISIBLE);
            Glide.with(mContext)
                    .asBitmap()
                    .load(post.getUrl())
                    .apply(new RequestOptions().placeholder(R.drawable.post_background_transparent))
                    .into(feedViewHolder.PostImageVideo_ImageView);
        }
    }

    @Override
    public int getItemCount() {
        return postLists.size();
    }

    public static Drawable GetImage(Context c, String ImageName) {
        return c.getResources().getDrawable(c.getResources().getIdentifier(ImageName, "drawable", c.getPackageName()));
    }
}
