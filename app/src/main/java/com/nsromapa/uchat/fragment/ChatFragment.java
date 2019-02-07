package com.nsromapa.uchat.fragment;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nsromapa.uchat.MainActivity;
import com.nsromapa.uchat.R;
import com.nsromapa.uchat.recyclerviewchatfragment.ChatFragmentAdapater;
import com.nsromapa.uchat.recyclerviewchatfragment.ChatFragmentObject;
import com.nsromapa.uchat.recyclerviewchatfragment.FragmentChatBackground;
import com.nsromapa.uchat.usersInfos.UserInformation;

import java.util.ArrayList;
import java.util.Objects;

public class ChatFragment extends BaseFragment {


    public static ChatFragment create(){
        return new ChatFragment();
    }


    @Override
    public int getLayoutResId() {
        return R.layout.fragment_chat;
    }

    private int currentListSize=0;

    private RecyclerView mRecyclerView;
    public RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;


    private ProgressBar progressBar;

    @Override
    public void inOnCreateView(View view, @Nullable ViewGroup container, @Nullable Bundle saveInstanceState) {

        progressBar = view.findViewById(R.id.fragment_chat_progress_bar);

        mRecyclerView = view.findViewById(R.id.fragment_chat_recyclerView);
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        //        mAdapter = new ChatFragmentAdapater(getDataSet(),getContext());
//        mRecyclerView.setAdapter(mAdapter);


//        new CountDownTimer(10000,10000){
//
//            @Override
//            public void onTick(long millisUntilFinished){}
//            @Override
//            public void onFinish() {
//                int listSize = UserInformation.listFollowers.size();
//                if (listSize != currentListSize){
//                    Toast.makeText(getContext(), "Working", Toast.LENGTH_SHORT).show();
//                    currentListSize = listSize;
//                    mAdapter = new ChatFragmentAdapater(getDataSet(),getContext());
//                    mRecyclerView.setAdapter(mAdapter);
//                }
//                start();
//            }
//        }.start();
        new FragmentChatBackground(mRecyclerView, progressBar, getContext()).execute("friends_forChat");

    }

    //    private ArrayList<ChatFragmentObject> results = new ArrayList<>();
//    private ArrayList<ChatFragmentObject> getDataSet() {
//        listenForData();
//        return results;
//    }
//
//    private void listenForData() {
//        if (progressBar.getVisibility() == View.GONE ) {
//            progressBar.setVisibility(View.VISIBLE);
//        }
//        if ( UserInformation.listFollowers.size()>0){
//            Toast.makeText(getContext(), "No users to add...", Toast.LENGTH_SHORT).show();
//        }
//        for(int i = 0; i < UserInformation.listFollowing.size(); i++){
//            DatabaseReference usersDb = FirebaseDatabase.getInstance().getReference().child("users").child(UserInformation.listFollowing.get(i));
//            usersDb.addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                    String name = "";
//                    String userState ="";
//                    String profileImageUrl = "default";
//                    String uid = dataSnapshot.getRef().getKey();
//                    if(dataSnapshot.child("email").getValue() != null){
//                        name = Objects.requireNonNull(dataSnapshot.child("name").getValue()).toString();
//                        profileImageUrl = Objects.requireNonNull(dataSnapshot.child("profileImageUrl").getValue()).toString();
//
//                        if (dataSnapshot.child("userState").hasChild("state")){
//                            String state = Objects.requireNonNull(dataSnapshot.child("userState").child("state").getValue()).toString();
//                            String date = Objects.requireNonNull(dataSnapshot.child("userState").child("date").getValue()).toString();
//                            String time = Objects.requireNonNull(dataSnapshot.child("userState").child("time").getValue()).toString();
//
//                            if (!state.equals("offline"))
//                                userState = state;
//                            else
//                                userState = "Last seen"+date+"-"+time;
//                        }
//
//                    }
//                    ChatFragmentObject obj = new ChatFragmentObject(name, uid, userState, profileImageUrl);
//                    if(!results.contains(obj)){
//                        results.add(obj);
//                        mAdapter.notifyDataSetChanged();
//
//                        if (progressBar.getVisibility() == View.VISIBLE){
//                            progressBar.setVisibility(View.GONE);
//                        }
//                    }
//                }
//
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//
//                }
//            });
//        }
//    }


}
