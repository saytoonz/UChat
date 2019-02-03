package com.nsromapa.uchat.recyclerviewstory;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
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
import com.nsromapa.uchat.R;
import com.nsromapa.uchat.customizations.CustomIntent;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import de.hdodenhof.circleimageview.CircleImageView;


public class StoryAdapter extends RecyclerView.Adapter<StoryViewHolders>{

    private List<StoryObject> usersList;
    private List<String> individualStories= new ArrayList<>();
    private List<String> individualStoriesTime = new ArrayList<>();;
    private Context context;

    public StoryAdapter(List<StoryObject> usersList, Context context){
        this.usersList = usersList;
        this.context = context;
    }
    @Override
    public StoryViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_story_item, null);
        StoryViewHolders rcv = new StoryViewHolders(layoutView);
        return rcv;
    }

    @Override
    public void onBindViewHolder(final StoryViewHolders holder, final int position) {

        getIndividualStoryInfos(usersList.get(position).getUid(), holder.number_of_stories);


        holder.mEmail.setText(usersList.get(position).getName());

        holder.mLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DisplayImageActivity.class);
                Bundle b = new Bundle();
                b.putString("userId", usersList.get(position).getUid());
                b.putString("chatOrStory", "story");
                intent.putExtras(b);
                context.startActivity(intent);
                CustomIntent.customType(context, "fadein-to-fadeout");
            }
        });



        Picasso.get()
        .load(usersList.get(position).getProfileImageUrl())
                .placeholder(R.drawable.profile_image)
                .into(holder.profileImage);


        holder.story_item_last_story_time.setText("Date, Time");


    }


    @Override
    public int getItemCount() {
        return this.usersList.size();
    }

    private void getIndividualStoryInfos(String userId, final TextView view) {
        DatabaseReference StoryInfosDb = FirebaseDatabase.getInstance().getReference().child("users").child(userId).child("stories");
        StoryInfosDb.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.exists()){

                    individualStories.clear();
                   // Set<String> set = new HashSet<>();
                    Iterator iterator = dataSnapshot.getChildren().iterator();
                    while (iterator.hasNext()){
                       // set.add(((DataSnapshot)iterator.next()).getKey());
                        individualStories.add(((DataSnapshot)iterator.next()).getKey());
                        view.setText(""+individualStories.size());
                    }
                }

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    String imageUrl = dataSnapshot.child("imageUrl").getValue().toString();
                    if (!imageUrl.equals("")){
                        individualStories.remove(imageUrl);
                    }
                }
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
