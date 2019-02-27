package com.nsromapa.uchat.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nsromapa.uchat.CreatePostActivity;
import com.nsromapa.uchat.R;
import com.nsromapa.uchat.recyclerfeeds.FeedsAdapter;
import com.nsromapa.uchat.recyclerfeeds.FeedsObjects;
import com.nsromapa.uchat.usersInfos.UserInformation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class FeedsFragment extends BaseFragment {

    public static FeedsFragment create() {
        return new FeedsFragment();
    }

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_feeds;
    }


    private FloatingActionButton fab_addPost;


    private RecyclerView mRecyclerView;
    public RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private TextView postNotification;

    ArrayList<FeedsObjects> postsList = new ArrayList<>();



    @Override
    public void inOnCreateView(View view, @Nullable ViewGroup container, @Nullable Bundle saveInstanceState) {

        fab_addPost = view.findViewById(R.id.fab_addPost);
        fab_addPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), CreatePostActivity.class);
                startActivity(intent);
            }
        });

        postNotification = view.findViewById(R.id.postNotification);
        postNotification.append("\nPlease Wait");

        mRecyclerView = view.findViewById(R.id.feed_PostRecyclerView);
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());
        ((LinearLayoutManager) mLayoutManager).setReverseLayout(true);
        ((LinearLayoutManager) mLayoutManager).setStackFromEnd(true);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new FeedsAdapter(postsList, getContext());
        mRecyclerView.setAdapter(mAdapter);

        startGettingPosts();
    }

    private void startGettingPosts() {
        final DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();

        mRootRef.child("posts").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapShot, @Nullable String s) {
                if (snapShot.hasChildren()) {

                    postNotification.setText("");
                    postNotification.setVisibility(View.GONE);


                    final String background = snapShot.child("background").getValue().toString();
                    final String date = snapShot.child("date").getValue().toString();
                    final String from = snapShot.child("from").getValue().toString();
                    final String hates = snapShot.child("hates").getValue().toString();
                    final String likes = String.valueOf(0);
                    final String locLat = snapShot.child("locLat").getValue().toString();
                    final String locLong = snapShot.child("locLong").getValue().toString();
                    final String postId = snapShot.child("postId").getValue().toString();
                    final String privacy = snapShot.child("privacy").getValue().toString();
                    final String size = snapShot.child("size").getValue().toString();
                    final String state = snapShot.child("state").getValue().toString();
                    final String style = snapShot.child("style").getValue().toString();
                    final String text = snapShot.child("text").getValue().toString();
                    final String time = snapShot.child("time").getValue().toString();
                    final String type = snapShot.child("type").getValue().toString();
                    final String url = snapShot.child("url").getValue().toString();

                    final Object cmtCnter = snapShot.child("commentCounter").getValue();
                    final Object comment1Obj = snapShot.child("comment1").getValue();
                    final Object comment2Obj = snapShot.child("comment2").getValue();
                    final Object comment3Obj = snapShot.child("comment3").getValue();
                    final Object comment4Obj = snapShot.child("comment4").getValue();

                    final List<String> likers = new ArrayList<>();
                    final List<String> haters = new ArrayList<>();

                   final String commentCounter;
                    final String Fullcomment1;
                    final String Fullcomment2;
                    final String Fullcomment3;
                    final String Fullcomment4;

                    if (cmtCnter!=null){
                        commentCounter = cmtCnter.toString();
                    }else {
                        commentCounter = "0";
                    }

                    if (comment1Obj!=null){
                        Fullcomment1 = comment1Obj.toString();
                    }else {
                        Fullcomment1 = "";
                    }

                    if (comment2Obj!=null){
                        Fullcomment2 = "\n\n"+comment2Obj.toString();;
                    }else {
                        Fullcomment2 = "";
                    }

                    if (comment3Obj!=null){
                        Fullcomment3 = "\n\n"+ comment3Obj.toString();;
                    }else {
                        Fullcomment3 = "";
                    }



                    if (comment4Obj!=null){
                        Fullcomment4 = "\n\n"+comment4Obj.toString();;
                    }else {
                        Fullcomment4 = "";
                    }

                    snapShot.getRef().child("likers")
                            .addChildEventListener(new ChildEventListener() {
                                @Override
                                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                                    String liker = dataSnapshot.getValue().toString();
                                    likers.add(liker);
                                    mAdapter.notifyDataSetChanged();
                                }

                                @Override
                                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                                }

                                @Override
                                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                                    String liker = dataSnapshot.getValue().toString();
                                    likers.remove(liker);
                                    mAdapter.notifyDataSetChanged();
                                }

                                @Override
                                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                                    String liker = dataSnapshot.getValue().toString();
                                    likers.remove(liker);
                                    mAdapter.notifyDataSetChanged();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });


                    snapShot.getRef().child("haters")
                            .addChildEventListener(new ChildEventListener() {
                                @Override
                                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                                    String hater = dataSnapshot.getValue().toString();
                                    haters.add(hater);
                                    mAdapter.notifyDataSetChanged();
                                }

                                @Override
                                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                                }

                                @Override
                                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                                    String hater = dataSnapshot.getValue().toString();
                                    haters.remove(hater);
                                    mAdapter.notifyDataSetChanged();
                                }

                                @Override
                                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                                    String hater = dataSnapshot.getValue().toString();
                                    haters.remove(hater);
                                    mAdapter.notifyDataSetChanged();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });




                    mRootRef.child("users").child(from)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    String posterName = dataSnapshot.child("name").getValue().toString();
                                    String posterImage = dataSnapshot.child("profileImageUrl").getValue().toString();

                                    String likes = String.valueOf(likers.size());
                                    String hates = String.valueOf(haters.size());

                                    FeedsObjects feedsObjects = new FeedsObjects(background, date, from,
                                            posterName, posterImage, hates,
                                            likes, locLat, locLong, postId,
                                            privacy, size, state, style,
                                            text, time, type, url,
                                            likers, haters,commentCounter,
                                            Fullcomment1,Fullcomment2,
                                            Fullcomment3,Fullcomment4);

                                    if (!postsList.contains(feedsObjects)) {
                                        postsList.add(feedsObjects);
                                    }
                                    mAdapter.notifyDataSetChanged();

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });


                } else {

                    postNotification.setText("You do not have any post to display.\n" +
                            " Connect with friends and start following those you know to enhance your posts...");
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


}