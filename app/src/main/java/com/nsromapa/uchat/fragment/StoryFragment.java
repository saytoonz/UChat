package com.nsromapa.uchat.fragment;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.nsromapa.uchat.R;
import com.nsromapa.uchat.customizations.CustomIntent;
import com.nsromapa.uchat.usersInfos.MyStories;
import com.nsromapa.uchat.usersInfos.UserInformation;
import com.nsromapa.uchat.recyclerviewstory.DisplayImageActivity;
import com.nsromapa.uchat.recyclerviewstory.StoryAdapter;
import com.nsromapa.uchat.recyclerviewstory.StoryObject;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class StoryFragment extends BaseFragment{

    public static StoryFragment create(){
        return new StoryFragment();
    }


    @Override
    public int getLayoutResId() {
        return R.layout.fragment_story;
    }

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private SwipeRefreshLayout mRefresh;

    private LinearLayout my_story_full_layout;
    private CircleImageView last_story_image;
    private TextView my_story_or_stories, my_story_last_story_time, my_story_number_of_stories;

    String myUid;

    @Override
    public void inOnCreateView(View view, @Nullable ViewGroup container, @Nullable Bundle saveInstanceState) {


        myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        last_story_image = view.findViewById(R.id.my_story_profileImageView);
        my_story_or_stories = view.findViewById(R.id.my_story_or_stories);
        my_story_last_story_time = view.findViewById(R.id.my_story_last_story_time);
        my_story_number_of_stories = view.findViewById(R.id.my_story_number_of_stories);
        my_story_full_layout = view.findViewById(R.id.my_story_full_layout);
        my_story_full_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                        Intent intent = new Intent(getContext(), DisplayImageActivity.class);
                        Bundle b = new Bundle();
                        b.putString("userId",myUid);
                        b.putString("chatOrStory", "story");
                        intent.putExtras(b);
                        getContext().startActivity(intent);
                        CustomIntent.customType(getContext(),"fadein-to-fadeout");

            }
        });
        if (MyStories.mystoriesList.size()>1){
            my_story_or_stories.setText(R.string.my_stoies);
        }else{
            my_story_or_stories.setText(R.string.my_story);
        }
        if (MyStories.mystoriesList != null)
           if (MyStories.mystoriesList.size()<10)
               my_story_number_of_stories.setText("0"+MyStories.mystoriesList.size());
            else
               my_story_number_of_stories.setText(""+MyStories.mystoriesList.size());
        else
            my_story_number_of_stories.setText("00");


        mRecyclerView = view.findViewById(R.id.recyclerView);
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new StoryAdapter(getDataSet(), getContext());
        mRecyclerView.setAdapter(mAdapter);


        mRefresh = view.findViewById(R.id.swipeRefreshStories);
        mRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                clear();
                listenForData();
            }
        });

        getMyLastStatusTimeDateAndImage();

    }



    private void clear() {
        int size = this.results.size();
        this.results.clear();
        mAdapter.notifyItemRangeChanged(0, size);
    }


    private ArrayList<StoryObject> results = new ArrayList<>();
    private ArrayList<StoryObject> getDataSet() {
        listenForData();
        return results;
    }




    private void listenForData() {

        for(int i = 0; i < UserInformation.listFollowing.size(); i++){

//            mRefresh.setRefreshing(true);

            DatabaseReference followingStoryDb = FirebaseDatabase.getInstance().getReference().child("users").child(UserInformation.listFollowing.get(i));
            followingStoryDb.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String email = dataSnapshot.child("email").getValue().toString();
                    String name = dataSnapshot.child("name").getValue().toString();
                    String profileImageUrl = dataSnapshot.child("profileImageUrl").getValue().toString();
                    String uid = dataSnapshot.getRef().getKey();
                    long timestampBeg = 0;
                    long timestampEnd = 0;
                    for(DataSnapshot storySnapshot : dataSnapshot.child("stories").getChildren()){

                        if(storySnapshot.child("timestampBeg").getValue() != null &&
                                storySnapshot.child("timestampEnd").getValue() != null){
                            timestampBeg = Long.parseLong(storySnapshot.child("timestampBeg").getValue().toString());
                            timestampEnd = Long.parseLong(storySnapshot.child("timestampEnd").getValue().toString());
                        }

                        long timestampCurrent = System.currentTimeMillis();
                        if(timestampCurrent >= timestampBeg && timestampCurrent <= timestampEnd){
                            StoryObject obj = new StoryObject(name, uid, profileImageUrl);

                            if(!results.contains(obj)){
                                results.add(obj);
                                mAdapter.notifyDataSetChanged();

                                if (mRefresh.isRefreshing())
                                    mRefresh.setRefreshing(false);
                            }
                        }

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }
    }

    private void getMyLastStatusTimeDateAndImage(){
       if (MyStories.mystoriesList!=null && MyStories.mystoriesList.size()>0){
           DatabaseReference usersDb = FirebaseDatabase.getInstance().getReference().child("users").child(myUid)
                   .child("stories");
           String searchFor = MyStories.mystoriesList.get(MyStories.mystoriesList.size()-1);
           Query query = usersDb.orderByChild("imageUrl")
                   .startAt(searchFor)
                   .endAt(searchFor + "\uf8ff");

           query.addChildEventListener(new ChildEventListener() {
               @Override
               public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                   if (dataSnapshot.hasChildren()){
                       my_story_last_story_time.setText(dataSnapshot.child("date").getValue().toString()
                                                        +", "+
                                                        dataSnapshot.child("time").getValue().toString());

                       Glide.with(getContext())
                               .asBitmap()
                               .load(dataSnapshot.child("imageUrl").getValue().toString())
                               .into(last_story_image);
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

}
