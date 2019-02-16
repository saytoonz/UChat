package com.nsromapa.uchat.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
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
import com.nsromapa.uchat.CreatePostActivity;
import com.nsromapa.uchat.R;
import com.nsromapa.uchat.recyclerfeeds.FeedsAdapter;
import com.nsromapa.uchat.recyclerfeeds.FeedsObjects;
import com.nsromapa.uchat.usersInfos.UserInformation;

import java.util.ArrayList;
import java.util.List;

public class FeedsFragment extends BaseFragment {

    public static FeedsFragment create(){
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
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new FeedsAdapter(postsList,getContext());
        mRecyclerView.setAdapter(mAdapter);

        startGettingPosts();
    }

    private void startGettingPosts() {
        DatabaseReference mPostRef = FirebaseDatabase.getInstance().getReference().child("posts");

        mPostRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapShot, @Nullable String s) {
               if (snapShot.hasChildren()){

                   postNotification.setText("");
                   postNotification.setVisibility(View.GONE);

//                   for (DataSnapshot snapShot : dataSnapshot.getChildren()) {

                       String background = snapShot.child("background").getValue().toString();
                       String date = snapShot.child("date").getValue().toString();
                       String from = snapShot.child("from").getValue().toString();
                       String hates = snapShot.child("hates").getValue().toString();
                       String likes = snapShot.child("likes").getValue().toString();
                       String locLat = snapShot.child("locLat").getValue().toString();
                       String locLong = snapShot.child("locLong").getValue().toString();
                       String postId = snapShot.child("postId").getValue().toString();
                       String privacy = snapShot.child("privacy").getValue().toString();
                       String size = snapShot.child("size").getValue().toString();
                       String state = snapShot.child("state").getValue().toString();
                       String style = snapShot.child("style").getValue().toString();
                       String text = snapShot.child("text").getValue().toString();
                       String time = snapShot.child("time").getValue().toString();
                       String type = snapShot.child("type").getValue().toString();
                       String url = snapShot.child("url").getValue().toString();
                       final List<String> likers = new ArrayList<>();
                       final List<String> haters = new ArrayList<>();;

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


                       FeedsObjects feedsObjects = new FeedsObjects(background, date, from, hates,
                               likes, locLat, locLong, postId,
                               privacy, size, state, style,
                               text, time, type, url,
                               likers, haters);
                       postsList.add(feedsObjects);
                       mAdapter.notifyDataSetChanged();

//                   }
               }else{
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