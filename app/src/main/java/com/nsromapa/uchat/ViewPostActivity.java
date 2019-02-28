package com.nsromapa.uchat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nsromapa.say.LikeButton;
import com.nsromapa.say.OnLikeListener;
import com.nsromapa.say.emogifstickerkeyboard.widget.EmoticonEditText;
import com.nsromapa.say.emogifstickerkeyboard.widget.EmoticonTextView;
import com.nsromapa.uchat.recyclerfeeds.PostCommentAdapter;
import com.nsromapa.uchat.recyclerfeeds.PostCommentObjects;
import com.nsromapa.uchat.utils.FormatterUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
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
    private EmoticonEditText vCreateComment_TextEdit;
    private Button vSend_Comment_Btn;

    private RecyclerView vPost_recycler;
    public RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    ArrayList<PostCommentObjects> postCommentsList = new ArrayList<>();

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
        vCreateComment_TextEdit = findViewById(R.id.vCreateComment_TextEdit);
        vSend_Comment_Btn = findViewById(R.id.vSend_Comment_Btn);

        vPost_recycler = findViewById(R.id.vPost_recycler);
        vPost_recycler.setHasFixedSize(true);
        vPost_recycler.setNestedScrollingEnabled(true);
        mLayoutManager = new LinearLayoutManager(this);
        ((LinearLayoutManager) mLayoutManager).setReverseLayout(true);
        ((LinearLayoutManager) mLayoutManager).setStackFromEnd(true);
        vPost_recycler.setLayoutManager(mLayoutManager);
        mAdapter = new PostCommentAdapter(this,postCommentsList);
        vPost_recycler.setAdapter(mAdapter);



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
                    final String commentId = Objects.requireNonNull(dataSnapshot.child("commentId").getValue()).toString();
                    final String sender = Objects.requireNonNull(dataSnapshot.child("sender").getValue()).toString();
                    final String date = Objects.requireNonNull(dataSnapshot.child("date").getValue()).toString();
                    final String time = Objects.requireNonNull(dataSnapshot.child("time").getValue()).toString();
                    final String comment = Objects.requireNonNull(dataSnapshot.child("comment").getValue()).toString();

                    commentCounter(commentId, "add");

                    mRootRef.child("users").child(sender)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    String senderImage = Objects.requireNonNull(dataSnapshot.child("profileImageUrl").getValue()).toString();
                                    String senderName = Objects.requireNonNull(dataSnapshot.child("name").getValue()).toString();

                                    postCommentsList.add(new PostCommentObjects(commentId,senderImage,senderName,date,time,comment));
                                    mAdapter.notifyDataSetChanged();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });



                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    String commentId = Objects.requireNonNull(dataSnapshot.child("commentId").getValue()).toString();
                    commentCounter(commentId, "remove");
                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                    String commentId = Objects.requireNonNull(dataSnapshot.child("commentId").getValue()).toString();
                    commentCounter(commentId, "remove");
                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    String commentId = Objects.requireNonNull(dataSnapshot.child("commentId").getValue()).toString();
                    commentCounter(commentId, "remove");
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });



            newPostStuff();

        } else {
            finish();
        }
    }


    private void newPostStuff() {

        ///Hide/Show post button if EditText is is/not empty
        vCreateComment_TextEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (s.length() < 1) {
                    vSend_Comment_Btn.setVisibility(View.GONE);
                } else {
                    vSend_Comment_Btn.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() < 1) {
                    vSend_Comment_Btn.setVisibility(View.GONE);
                } else {
                    vSend_Comment_Btn.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() < 1) {
                    vSend_Comment_Btn.setVisibility(View.GONE);
                } else {
                    vSend_Comment_Btn.setVisibility(View.VISIBLE);
                }
            }
        });


        vSend_Comment_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String comment = Objects.requireNonNull(vCreateComment_TextEdit.getText()).toString();
                vCreateComment_TextEdit.setText("");

                if (TextUtils.isEmpty(comment)) {
                    Toast.makeText(ViewPostActivity.this, "Can't create empty comment", Toast.LENGTH_SHORT).show();
                    v.setVisibility(View.GONE);
                } else {
                    Toast.makeText(ViewPostActivity.this, "Posting Comment...", Toast.LENGTH_SHORT).show();


                    String commentId = String.valueOf(System.currentTimeMillis());

                    DatabaseReference commentRef = FirebaseDatabase.getInstance().getReference()
                            .child("posts").child(postId).child("comments").child(commentId);

                    Calendar calendarFordate = Calendar.getInstance();
                    @SuppressLint("SimpleDateFormat")
                    SimpleDateFormat currentDateFormat = new SimpleDateFormat("MMM dd, yyyy");
                    String _date = currentDateFormat.format(calendarFordate.getTime());

                    Calendar calendarForTime = Calendar.getInstance();
                    @SuppressLint("SimpleDateFormat")
                    SimpleDateFormat currentTimeFormat = new SimpleDateFormat("hh:mm a");
                    String _time = currentTimeFormat.format(calendarForTime.getTime());

                    HashMap<String, Object> commentMap = new HashMap<>();
                    commentMap.put("commentId", commentId);
                    commentMap.put("sender", currentUserID);
                    commentMap.put("date", _date);
                    commentMap.put("time", _time);
                    commentMap.put("comment", comment);

                    commentRef.updateChildren(commentMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                mRootRef.child("posts").child(postId)
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                ////Increase comment counter for the post and insert into db...
                                                int commentCounter;
                                                if (dataSnapshot.child("commentCounter").getValue() != null)
                                                    commentCounter = Integer.parseInt(Objects.requireNonNull(dataSnapshot.child("commentCounter").getValue()).toString());
                                                else
                                                    commentCounter = 0;

                                                commentCounter++;
                                                dataSnapshot.getRef().child("commentCounter").setValue(String.valueOf(commentCounter));


                                                ///Check  if 1st comment is already given.....
                                                String comment1 = "";
                                                if (dataSnapshot.child("comment1").getValue() != null) {
                                                    comment1 = Objects.requireNonNull(dataSnapshot.child("comment1").getValue()).toString();
                                                }
                                                ///Check  if 2nd comment is already given.....
                                                String comment2 = "";
                                                if (dataSnapshot.child("comment2").getValue() != null) {
                                                    comment2 = Objects.requireNonNull(dataSnapshot.child("comment2").getValue()).toString();
                                                }
                                                ///Check  if 3rd comment is already given.....
                                                String comment3 = "";
                                                if (dataSnapshot.child("comment3").getValue() != null) {
                                                    comment3 = Objects.requireNonNull(dataSnapshot.child("comment3").getValue()).toString();
                                                }
                                                ///Check  if 4th comment is already given.....
                                                String comment4 = "";
                                                if (dataSnapshot.child("comment4").getValue() != null) {
                                                    comment4 = Objects.requireNonNull(dataSnapshot.child("comment4").getValue()).toString();
                                                }

                                                if (TextUtils.isEmpty(comment1.trim())) {
                                                    dataSnapshot.getRef().child("comment1").setValue(comment);
                                                    dataSnapshot.getRef().child("comment1Name").setValue(currentUserID);
                                                } else {
                                                    Log.d(TAG, "onDataChange: First comment already exist...");

                                                    if (TextUtils.isEmpty(comment2.trim())) {
                                                        dataSnapshot.getRef().child("comment2").setValue(comment);
                                                        dataSnapshot.getRef().child("comment2Name").setValue(currentUserID);
                                                    } else {
                                                        Log.d(TAG, "onDataChange: Second comment already exist...");

                                                        if (TextUtils.isEmpty(comment3.trim())) {
                                                            dataSnapshot.getRef().child("comment3").setValue(comment);
                                                            dataSnapshot.getRef().child("comment3Name").setValue(currentUserID);
                                                        } else {
                                                            Log.d(TAG, "onDataChange: Third comment already exist...");

                                                            if (TextUtils.isEmpty(comment4.trim())) {
                                                                dataSnapshot.getRef().child("comment4").setValue(comment);
                                                                dataSnapshot.getRef().child("comment4Name").setValue(currentUserID);
                                                            } else {
                                                                Log.d(TAG, "onDataChange: Four comment already exist...");
                                                            }
                                                        }
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                            } else {
                                Toast.makeText(ViewPostActivity.this, "Error: Could not comment, please try again", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }
            }
        });

    }

    private void likesAddCounts(String likerId) {
        if (likers.contains(likerId)) {
            Log.d(TAG, "likesAddCounts: LIKED alerady");
        } else {
            vpostTotal_likers.setText(String.valueOf(likers.size() + 1));
            likers.add(likerId);
        }
    }

    private void likesRemoveCounts(String likerId) {
        if (likers.contains(likerId)) {
            vpostTotal_likers.setText(String.valueOf(likers.size() - 1));
            likers.remove(likerId);
        } else {
            Log.d(TAG, "likesRemoveCounts: Unknown like to remove");
        }
    }

    private void HatesAddCounts(String haterId) {
        if (haters.contains(haterId)) {
            Log.d(TAG, "HatesAddCounts: LIKED alerady");
        } else {
            vpostTotal_haters.setText(String.valueOf(likers.size() + 1));
            likers.add(haterId);
        }
    }

    private void HatesRemoveCounts(String haterId) {
        if (haters.contains(haterId)) {
            vpostTotal_haters.setText(String.valueOf(likers.size() - 1));
            likers.remove(haterId);
        } else {
            Log.d(TAG, "HatesRemoveCounts: Unknown like to remove");
        }
    }

    private void commentCounter(String commentId, String addORremove) {
        if (commentsArray.contains(commentId) && addORremove.equals("remove")) {
            String commentString = " Comments";
            if ((commentsArray.size() - 1) < 2) {
                commentString = " Comment";
            }
            String setCmt = String.valueOf(commentsArray.size() - 1) + commentString;
            total_comments.setText(setCmt);
            commentsArray.remove(commentId);

        } else if (!commentsArray.contains(commentId) && addORremove.equals("add")) {
            String commentString = " Comments";
            if ((commentsArray.size() + 1) < 2) {
                commentString = " Comment";
            }
            String setCmt = String.valueOf(commentsArray.size() + 1) + commentString;
            total_comments.setText(setCmt);
            commentsArray.add(commentId);

        } else {
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
