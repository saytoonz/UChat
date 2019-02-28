package com.nsromapa.uchat;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.nsromapa.say.LikeButton;
import com.nsromapa.say.OnLikeListener;
import com.nsromapa.say.emogifstickerkeyboard.widget.EmoticonTextView;
import com.nsromapa.uchat.utils.FormatterUtil;

import java.util.ArrayList;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class ViewPostActivity extends AppCompatActivity {
    private final static String TAG = "ViewPostActivity";

    private Toolbar toolbar;
    private String postId;
    private String postType;
    private String posterName;
    private String posterImage;
    private String postText, postBackground;
    private String postStyle, postSize;
    private String postUrl;
    private String postfrom;
    private String hateState;
    private String likeState;

    private EmoticonTextView posterNameView;
    private TextView postTimeView;
    private CircleImageView posterImageView;

    private ImageView vPostImageVideo_ImageView;
    private ImageView vpost_VideoThumbnail_play;
    private EmoticonTextView vPostTextpost_TextView;
    private EmoticonTextView vPostCaption_TextView;

    private LikeButton vPostActionButtons_likeUnlike;
    private LikeButton vPostActionButtons_hateUnhate;
    private TextView vpostTotal_likers;
    private TextView vpostTotal_haters;
    private ImageButton vPostActionButtons_delete;

    private TextView total_comments;

    private String currentUserID;
    private DatabaseReference mRootRef;
    ArrayList<String> likers = new ArrayList<>();
    ArrayList<String> haters = new ArrayList<>();
    ArrayList<String> commentsArray = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_post);

        currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mRootRef = FirebaseDatabase.getInstance().getReference();


        toolbar = findViewById(R.id.view_post_app_bar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View actionBarView = inflater.inflate(R.layout.custom_view_post_bar, null);
        actionBar.setCustomView(actionBarView);

        posterNameView = findViewById(R.id.custom_bar_poster_name);
        postTimeView = findViewById(R.id.custom_bar_post_time);
        posterImageView = findViewById(R.id.custom_bar_poster_image_view);

        vPostImageVideo_ImageView = findViewById(R.id.vPostImageVideo_ImageView);
        vpost_VideoThumbnail_play = findViewById(R.id.vpost_VideoThumbnail_play);
        vPostTextpost_TextView = findViewById(R.id.vPostTextpost_TextView);
        vPostCaption_TextView = findViewById(R.id.vPostCaption_TextView);

        vPostActionButtons_likeUnlike = findViewById(R.id.vPostActionButtons_likeUnlike);
        vPostActionButtons_hateUnhate = findViewById(R.id.vPostActionButtons_hateUnhate);
        vpostTotal_likers = findViewById(R.id.vpostTotal_likers);
        vpostTotal_haters = findViewById(R.id.vpostTotal_haters);
        vPostActionButtons_delete = findViewById(R.id.vPostActionButtons_delete);

        total_comments = findViewById(R.id.total_comments);


        if (getIntent() != null) {
            postId = getIntent().getStringExtra("postId");
            postType = getIntent().getStringExtra("postType");
            posterName = getIntent().getStringExtra("posterName");
            posterImage = getIntent().getStringExtra("posterImage");
            postText = getIntent().getStringExtra("postText");
            postStyle = getIntent().getStringExtra("postStyle");
            postBackground = getIntent().getStringExtra("postBackground");
            postSize = getIntent().getStringExtra("postSize");
            postUrl = getIntent().getStringExtra("postUrl");
            postfrom = getIntent().getStringExtra("postfrom");
            hateState = getIntent().getStringExtra("hateState");
            likeState = getIntent().getStringExtra("likeState");

            posterNameView.setText(posterName);
            postTimeView.setText(FormatterUtil.getRelativeTimeSpanStringShort(this, Long.parseLong(postId)));
            Glide.with(this)
                    .asBitmap()
                    .apply(new RequestOptions().placeholder(R.drawable.profile_image))
                    .load(posterImage)
                    .into(posterImageView);


            vPostImageVideo_ImageView.setVisibility(View.GONE);
            vpost_VideoThumbnail_play.setVisibility(View.GONE);
            vPostTextpost_TextView.setVisibility(View.GONE);
            vPostCaption_TextView.setVisibility(View.GONE);

            if (postType.equals("test_post")) {

                vPostTextpost_TextView.setVisibility(View.VISIBLE);

                String fontFamily = "AlexBrush_Regular.ttf";
                if (!TextUtils.isEmpty(postStyle)) {
                    fontFamily = postStyle;
                }
                String background = "post_background_transparent";
                if (!TextUtils.isEmpty(postBackground)) {
                    background = postBackground;
                }
                float textSize = 16f;
                if (!TextUtils.isEmpty(postSize)) {
                    textSize = Float.parseFloat(postSize);
                }

                Typeface typeface = Typeface.createFromAsset(this.getAssets(),
                        "fonts/" + fontFamily);
                vPostTextpost_TextView.setText(postText);
                vPostTextpost_TextView.setTypeface(typeface);
                vPostTextpost_TextView.setBackground(GetImage(this, background));
                vPostTextpost_TextView.setTextSize(textSize);
                vPostTextpost_TextView.setEmoticonSize(((int) textSize) + 12);


            } else if (postType.equals("video") || postType.equals("image")) {
                vPostImageVideo_ImageView.setVisibility(View.VISIBLE);

                ///Show caption if file has....
                if (!TextUtils.isEmpty(postText)) {
                    vPostCaption_TextView.setVisibility(View.VISIBLE);
                    vPostCaption_TextView.setText(postText);
                }

                //Show play icon on videos
                if (postType.equals("video")) {
                    vpost_VideoThumbnail_play.setVisibility(View.VISIBLE);
                } else {
                    vpost_VideoThumbnail_play.setVisibility(View.GONE);
                }


                vPostImageVideo_ImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(ViewPostActivity.this, "toggle play", Toast.LENGTH_SHORT).show();
                    }
                });

                Glide.with(this)
                        .asBitmap()
                        .load(postUrl)
                        .apply(new RequestOptions()
                                .placeholder(R.drawable.post_background_transparent))
                        .into(vPostImageVideo_ImageView);
            }


            if (likeState.equals("liked")) {
                vPostActionButtons_likeUnlike.setLiked(true);
            } else {
                vPostActionButtons_likeUnlike.setLiked(false);
            }
            if (hateState.equals("hated")) {
                vPostActionButtons_hateUnhate.setLiked(true);
            } else {
                vPostActionButtons_hateUnhate.setLiked(false);
            }
            vPostActionButtons_likeUnlike.setOnLikeListener(new OnLikeListener() {
                @Override
                public void liked(LikeButton likeButton) {
                    mRootRef.child("posts").child(postId)
                            .child("likers").child(currentUserID).setValue(currentUserID);
                }

                @Override
                public void unLiked(LikeButton likeButton) {
                    mRootRef.child("posts").child(postId)
                            .child("likers").child(currentUserID).removeValue();

                }
            });


            vPostActionButtons_hateUnhate.setOnLikeListener(new OnLikeListener() {
                @Override
                public void liked(LikeButton likeButton) {
                    mRootRef.child("posts").child(postId)
                            .child("haters").child(currentUserID).setValue(currentUserID);
                }

                @Override
                public void unLiked(LikeButton likeButton) {
                    mRootRef.child("posts").child(postId)
                            .child("haters").child(currentUserID).removeValue();
                }
            });


            if (postfrom.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                vPostActionButtons_delete.setVisibility(View.VISIBLE);
                vPostActionButtons_delete.setEnabled(true);

                vPostActionButtons_delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deletePost(postId);
                    }
                });
            } else {
                vPostActionButtons_delete.setVisibility(View.GONE);
                vPostActionButtons_delete.setEnabled(false);
            }


            mRootRef.child("posts").child(postId)
                    .child("haters").addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    String likerId = Objects.requireNonNull(dataSnapshot.getValue()).toString();
                    HatesAddCounts(likerId);
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    String likerId = Objects.requireNonNull(dataSnapshot.getValue()).toString();
                    HatesRemoveCounts(likerId);
                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                    String likerId = Objects.requireNonNull(dataSnapshot.getValue()).toString();
                    HatesRemoveCounts(likerId);
                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    String likerId = Objects.requireNonNull(dataSnapshot.getValue()).toString();
                    HatesRemoveCounts(likerId);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });

            mRootRef.child("posts").child(postId)
                    .child("likers").addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    String likerId = Objects.requireNonNull(dataSnapshot.getValue()).toString();
                    likesAddCounts(likerId);
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    vpostTotal_likers.setText(String.valueOf(dataSnapshot.getChildrenCount()));
                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                    String likerId = Objects.requireNonNull(dataSnapshot.getValue()).toString();
                    likesRemoveCounts(likerId);
                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    String likerId = Objects.requireNonNull(dataSnapshot.getValue()).toString();
                    likesRemoveCounts(likerId);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });

            mRootRef.child("posts").child(postId)
                    .child("comments").addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    String commentId = Objects.requireNonNull(dataSnapshot.child("commentId").getValue()).toString();
                    commentCounter(commentId,  "add");
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    String commentId = Objects.requireNonNull(dataSnapshot.child("commentId").getValue()).toString();
                    commentCounter(commentId,"remove") ;               }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                    String commentId = Objects.requireNonNull(dataSnapshot.child("commentId").getValue()).toString();
                    commentCounter(commentId,"remove") ;               }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    String commentId = Objects.requireNonNull(dataSnapshot.child("commentId").getValue()).toString();
                    commentCounter(commentId,"remove");
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


        } else {
            finish();
        }
    }

    private void likesAddCounts(String likerId) {
        if (likers.contains(likerId)) {
            Log.d(TAG, "likesAddCounts: LIKED alerady");
        } else {
            vpostTotal_likers.setText(String.valueOf(likers.size()+1));
            likers.add(likerId);
        }
    }

    private void likesRemoveCounts(String likerId) {
        if (likers.contains(likerId)) {
            vpostTotal_likers.setText(String.valueOf(likers.size()-1));
            likers.remove(likerId);
        } else {
            Log.d(TAG, "likesRemoveCounts: Unknown like to remove");
        }
    }

    private void HatesAddCounts(String haterId) {
        if (haters.contains(haterId)) {
            Log.d(TAG, "HatesAddCounts: LIKED alerady");
        } else {
            vpostTotal_haters.setText(String.valueOf(likers.size()+1));
            likers.add(haterId);
        }
    }

    private void HatesRemoveCounts(String haterId) {
        if (haters.contains(haterId)) {
            vpostTotal_haters.setText(String.valueOf(likers.size()-1));
            likers.remove(haterId);
        } else {
            Log.d(TAG, "HatesRemoveCounts: Unknown like to remove");
        }
    }

    private void commentCounter(String commentId,String addORremove){
        if (commentsArray.contains(commentId)&& addORremove.equals("remove")){
            String  commentString = " Comments";
            if ((commentsArray.size()-1)<2){
                commentString = " Comment";
            }
            String setCmt = String.valueOf(commentsArray.size()-1) + commentString;
            total_comments.setText(setCmt);
            commentsArray.remove(commentId);

        }else if (!commentsArray.contains(commentId) && addORremove.equals("add")){
            String  commentString = " Comments";
            if ((commentsArray.size()+1)<2){
                commentString = " Comment";
            }
            String setCmt = String.valueOf(commentsArray.size()+1) + commentString;
            total_comments.setText(setCmt);
            commentsArray.add(commentId);

        }else{
            Log.d(TAG, "commentCounter: no action on comments....");
        }
    }



    private void deletePost(String postId) {
        Toast.makeText(this, "post to be deleted...." + postId, Toast.LENGTH_SHORT).show();
    }



    private static Drawable GetImage(Context c, String ImageName) {
        return c.getResources().getDrawable(c.getResources().getIdentifier(ImageName, "drawable", c.getPackageName()));
    }
}
