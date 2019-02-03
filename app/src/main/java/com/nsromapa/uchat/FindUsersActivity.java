package com.nsromapa.uchat;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.nsromapa.uchat.recyclerviewfollow.FollowAdapater;
import com.nsromapa.uchat.recyclerviewfollow.FollowObject;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class FindUsersActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    EditText mInput;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_users);

        mInput = findViewById(R.id.findUser_toolbar_search_edittext);

        mRecyclerView = findViewById(R.id.activity_find_users_recyclerView);
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getApplication());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new FollowAdapater(getDataSet(),getApplication());
        mRecyclerView.setAdapter(mAdapter);

        progressBar = findViewById(R.id.findUser_activity_progressBar);
        progressBar.setVisibility(View.VISIBLE);



        ImageView mSearch = findViewById(R.id.search_Button_imageView);
        mSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                clear();
                listenForData();
            }
        });
        ImageView findUser_toolbar_go_back = findViewById(R.id.findUser_toolbar_go_back);
        findUser_toolbar_go_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }


    private void listenForData() {

        DatabaseReference usersDb = FirebaseDatabase.getInstance().getReference().child("users");
        String searchFor = mInput.getText().toString();
        Query query = usersDb.orderByChild("name")
                .startAt(searchFor)
                .endAt(searchFor + "\uf8ff");

        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String s) {
                String name = "";
                String index = "";
                String profileImg = "";
                String uid = dataSnapshot.getRef().getKey();
                if(dataSnapshot.child("email").getValue() != null){
                    name = Objects.requireNonNull(dataSnapshot.child("name").getValue()).toString();
                    index = Objects.requireNonNull(dataSnapshot.child("index").getValue()).toString();
                    profileImg = Objects.requireNonNull(dataSnapshot.child("profileImageUrl").getValue()).toString();

                    if(!dataSnapshot.child("email").getValue().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())) {
                        FollowObject obj = new FollowObject(name, uid, index, profileImg);
                        results.add(obj);
                        mAdapter.notifyDataSetChanged();

                        progressBar.setVisibility(View.GONE);
                    }
                }



            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void clear() {
       int size = results.size();
       results.clear();
       mAdapter.notifyItemRangeChanged(0, size);
    }



    private ArrayList<FollowObject> results = new ArrayList<>();
    private ArrayList<FollowObject> getDataSet() {
        listenForData();
        return results;
    }

    private void updateStatus(String state) {

        HashMap<String, Object> onlineState = new HashMap<>();
        onlineState.put("state", state);

        FirebaseDatabase.getInstance().getReference()
                .child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("userState")
                .updateChildren(onlineState);
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            updateStatus("online");
        }
    }
}
