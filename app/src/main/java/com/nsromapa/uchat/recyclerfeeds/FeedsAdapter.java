package com.nsromapa.uchat.recyclerfeeds;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
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
import com.nsromapa.emoticompack.samsung.SamsungEmoticonProvider;
import com.nsromapa.say.emogifstickerkeyboard.widget.EmoticonTextView;
import com.nsromapa.uchat.R;
import com.nsromapa.uchat.utils.FormatterUtil;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class FeedsAdapter extends RecyclerView.Adapter<FeedViewHolder> {
    private final static String TAG = "FeedsAdapter";
    private FirebaseAuth mAuth;
    private DatabaseReference mRootRef;

    List<FeedsObjects> postLists;
    private Context mContext;
    private String currentUserID;

    private InputMethodManager inputMethodManager;
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
        inputMethodManager = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        return new FeedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final FeedViewHolder feedViewHolder, final int position) {

        final FeedsObjects post = postLists.get(position);

        feedViewHolder.PostImageVideo_ImageView.setVisibility(View.GONE);
        feedViewHolder.post_VideoThumbnail_play.setVisibility(View.GONE);
        feedViewHolder.PostTextpost_TextView.setVisibility(View.GONE);
        feedViewHolder.PostCaption_TextView.setVisibility(View.GONE);
        feedViewHolder.UserName_TextView.setVisibility(View.GONE);
        feedViewHolder.PostActionButtons_delete.setVisibility(View.GONE);
//        feedViewHolder.comments_LinearLayout.setVisibility(View.GONE);


        feedViewHolder.PosterUserName.setText(post.getPosterName());
        Glide.with(mContext)
                .asBitmap()
                .apply(new RequestOptions().placeholder(R.drawable.profile_image))
                .load(post.posterImage)
                .into(feedViewHolder.PostedUserFromPFP);
//        feedViewHolder.PostCreationTime.setText(post.getDate() + " - " + post.getTime());


        CharSequence date = FormatterUtil.getRelativeTimeSpanStringShort(mContext, Long.parseLong(post.getPostId()));
        feedViewHolder.PostCreationTime.setText(date);

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
            feedViewHolder.PostTextpost_TextView.setEmoticonSize(((int)textSize)+12);

        } else if (post.getType().equals("video") || post.getType().equals("image")){
            feedViewHolder.PostImageVideo_ImageView.setVisibility(View.VISIBLE);

            ///Show caption if file has....
            if (!TextUtils.isEmpty(post.getText())){
                feedViewHolder.PostCaption_TextView.setVisibility(View.VISIBLE);
                feedViewHolder.UserName_TextView.setVisibility(View.VISIBLE);
                feedViewHolder.UserName_TextView.setText(post.getPosterName());
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
        feedViewHolder.postTotal_commenters.setText(String.valueOf(post.getComments().size()));

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







        feedViewHolder.PostActionButtons_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (feedViewHolder.create_New_Comment.getVisibility()!= View.VISIBLE){
                    feedViewHolder.create_New_Comment.setVisibility(View.VISIBLE);
                }

                feedViewHolder.CreateComment_TextEdit.requestFocus();
//                if (inputMethodManager != null){
//                    inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
//                }

                ///Hide post button if EditText is empty
                if (TextUtils.isEmpty(feedViewHolder.CreateComment_TextEdit.getText().toString())){
                    feedViewHolder.Send_Comment_Btn.setVisibility(View.GONE);
                }
            }
        });

        ///Hide/Show post button if EditText is is/not empty
        feedViewHolder.CreateComment_TextEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (s.length()<1){
                    feedViewHolder.Send_Comment_Btn.setVisibility(View.GONE);
                }else{
                    feedViewHolder.Send_Comment_Btn.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length()<1){
                    feedViewHolder.Send_Comment_Btn.setVisibility(View.GONE);
                }else{
                    feedViewHolder.Send_Comment_Btn.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length()<1){
                    feedViewHolder.Send_Comment_Btn.setVisibility(View.GONE);
                }else{
                    feedViewHolder.Send_Comment_Btn.setVisibility(View.VISIBLE);
                }
            }
        });


        feedViewHolder.Send_Comment_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String comment = feedViewHolder.CreateComment_TextEdit.getText().toString();
                feedViewHolder.CreateComment_TextEdit.setText("");

                if (TextUtils.isEmpty(comment)){
                    Toast.makeText(mContext, "Can't create empty comment", Toast.LENGTH_SHORT).show();
                    v.setVisibility(View.GONE);
                }else{
                    Toast.makeText(mContext, "Posting Comment...", Toast.LENGTH_SHORT).show();


                    String commentId = String.valueOf(System.currentTimeMillis());

                    DatabaseReference commentRef = FirebaseDatabase.getInstance().getReference()
                            .child("posts").child(post.getPostId()).child("comments").child(commentId);

                    Calendar calendarFordate = Calendar.getInstance();
                    SimpleDateFormat currentDateFormat = new SimpleDateFormat("MMM dd, yyyy");
                    String _date = currentDateFormat.format(calendarFordate.getTime());

                    Calendar calendarForTime= Calendar.getInstance();
                    SimpleDateFormat currentTimeFormat = new SimpleDateFormat("hh:mm a");
                    String _time = currentTimeFormat.format(calendarForTime.getTime());

                    HashMap<String,Object> commentMap = new HashMap<>();
                    commentMap.put("commentId",commentId);
                    commentMap.put("sender",mAuth.getCurrentUser().getUid());
                    commentMap.put("date",_date);
                    commentMap.put("time",_time);
                    commentMap.put("comment",comment);

                    commentRef.updateChildren(commentMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                           if (task.isSuccessful()){
                               Toast.makeText(mContext, "Comment sent...", Toast.LENGTH_SHORT).show();
                           }else{
                               Toast.makeText(mContext, "Error: Could not comment, please try again", Toast.LENGTH_SHORT).show();
                           }
                        }
                    });

                }
            }
        });



        feedViewHolder.Post_comments_all.setText(post.getCommentString());


//        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//
//        feedViewHolder.comments_LinearLayout.setOrientation(LinearLayout.VERTICAL);
//
//        for (int i=0; i < post.getComments().size(); i++){
//            Object comment = post.getComments().get(i);
//
//
//            EmoticonTextView commenterTextView = new EmoticonTextView(mContext);
//            commenterTextView.setId(i);
//            commenterTextView.setText("commenter Name");
//            commenterTextView.setLayoutParams(params);
//            commenterTextView.setEmoticonSize(25);
//            commenterTextView.setTextSize(12);
//            commenterTextView.setTextColor(ContextCompat.getColor(mContext, R.color.black));
//            commenterTextView.setTypeface(commenterTextView.getTypeface(),Typeface.BOLD);
//            commenterTextView.setEmoticonProvider(SamsungEmoticonProvider.create());
//
//
//            EmoticonTextView commentTextView = new EmoticonTextView(mContext);
//            commentTextView.setId(i+100000);
//            commentTextView.setText("commenter Comment Posted "+i);
//            commentTextView.setLayoutParams(params);
//            commentTextView.setEmoticonSize(28);
//            commentTextView.setTextSize(14);
//            commentTextView.setTextColor(ContextCompat.getColor(mContext, R.color.black));
//            commentTextView.setTypeface(commentTextView.getTypeface(),Typeface.NORMAL);
//            commentTextView.setPadding(0,0,0,5);
//            commentTextView.setEmoticonProvider(SamsungEmoticonProvider.create());
//
//            feedViewHolder.comments_LinearLayout.addView(commenterTextView);
//            feedViewHolder.comments_LinearLayout.addView(commentTextView);
//        }

//        style="@style/Base.TextAppearance.AppCompat.Large"


//        style="@style/Base.TextAppearance.AppCompat.Medium"


//        final int id = (int) System.currentTimeMillis();

//        mRootRef.child("posts").child(post.getPostId()).getRef().child("comments")
//                .addChildEventListener(new ChildEventListener() {
//                    @Override
//                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//                        final String comment = dataSnapshot.child("comment").getValue().toString();
//                        final String _date = dataSnapshot.child("date").getValue().toString();
//                        final String _time = dataSnapshot.child("time").getValue().toString();
//                        final String commentId = dataSnapshot.child("commentId").getValue().toString();
//                        final String sender = dataSnapshot.child("sender").getValue().toString();
//
//                        mRootRef.child("users").child(sender)
//                                .addListenerForSingleValueEvent(new ValueEventListener() {
//                                    @Override
//                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                        String commnterName = dataSnapshot.child("name").getValue().toString();
//                                        String commenterImage = dataSnapshot.child("profileImageUrl").getValue().toString();
//
//                                        LinearLayout.LayoutParams params =
//                                                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
//                                                        LinearLayout.LayoutParams.WRAP_CONTENT);
//
//                                        feedViewHolder.comments_LinearLayout.setOrientation(LinearLayout.VERTICAL);
//
//
//
//                                        EmoticonTextView commenterTextView = new EmoticonTextView(mContext);
//                                        commenterTextView.setId(id);
//                                        commenterTextView.setText(commnterName);
//                                        commenterTextView.setLayoutParams(params);
//                                        commenterTextView.setEmoticonSize(25);
//                                        commenterTextView.setTextSize(12);
//                                        commenterTextView.setTextColor(ContextCompat.getColor(mContext, R.color.black));
//                                        commenterTextView.setTypeface(commenterTextView.getTypeface(),Typeface.BOLD);
//                                        commenterTextView.setEmoticonProvider(SamsungEmoticonProvider.create());
//
//
//                                        EmoticonTextView commentTextView = new EmoticonTextView(mContext);
//                                        commentTextView.setId(id+100000);
//                                        commentTextView.setText(comment);
//                                        commentTextView.setLayoutParams(params);
//                                        commentTextView.setEmoticonSize(28);
//                                        commentTextView.setTextSize(14);
//                                        commentTextView.setTextColor(ContextCompat.getColor(mContext, R.color.black));
//                                        commentTextView.setTypeface(commentTextView.getTypeface(),Typeface.NORMAL);
//                                        commentTextView.setPadding(0,0,0,5);
//                                        commentTextView.setEmoticonProvider(SamsungEmoticonProvider.create());
//
//                                        feedViewHolder.comments_LinearLayout.addView(commenterTextView);
//                                        feedViewHolder.comments_LinearLayout.addView(commentTextView);
//
//                                    }
//
//                                    @Override
//                                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                                    }
//                                });
//
//                    }
//
//                    @Override
//                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//                    }
//
//                    @Override
//                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
////                        String hater = dataSnapshot.getValue().toString();
////                        comments.remove(hater);
////                        mAdapter.notifyDataSetChanged();
//                    }
//
//                    @Override
//                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
////                        String hater = dataSnapshot.getValue().toString();
////                        comments.remove(hater);
////                        mAdapter.notifyDataSetChanged();
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                    }
//                });

    }


    @Override
    public int getItemCount() {
        return postLists.size();

    }



    private static Drawable GetImage(Context c, String ImageName) {
        return c.getResources().getDrawable(c.getResources().getIdentifier(ImageName, "drawable", c.getPackageName()));
    }
}
