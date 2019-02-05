package com.nsromapa.uchat.databases;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nsromapa.uchat.R;



public class UpdateLocalDB extends AppCompatActivity {


    private static final String TAG = "UpdaateLocalDB";
    private String networkState = "";
    private FirebaseAuth mAuth;
    private DatabaseReference mRoot;

    boolean bool = false;

    AlertDialog.Builder builder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_local_db);

        ProgressBar progress_ = findViewById(R.id.progress_);
        progress_.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(UpdateLocalDB.this, networkState, Toast.LENGTH_SHORT).show();
            }
        });


        mAuth = FirebaseAuth.getInstance();
        mRoot = FirebaseDatabase.getInstance().getReference();


        if (getUserInformation()){
            Log.d(TAG, "onCreate: User informaitons Added Successfully.....");

            if (getFollowerFollowingAndFriendsInformation("friends")){
                Log.d(TAG, "onCreate: All Friends Added");

                if (getFollowerFollowingAndFriendsInformation("followers")){
                    Log.d(TAG, "onCreate: All Followers Added");

                    if (getFollowerFollowingAndFriendsInformation("following")){

                        Log.d(TAG, "onCreate: All Following Added");



                        builder = new AlertDialog.Builder(this);
                        builder.setTitle(" ");
                        builder.setCancelable(false);
                        builder.setMessage("Do you want to get your previous messages?\n");
                        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                              finish();
                            }
                        });

                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (getUserMessages()){
                                    Log.d(TAG, "onCreate: All Messages Added");
                                }else{
                                    Log.d(TAG, "onCreate: could not add Messages Successfully.....");
                                }

                                finish();
                            }
                        });

                        builder.show();

                    }else{
                        Log.d(TAG, "onCreate: could not add following informaitons Successfully.....");
                    }
                }else{
                    Log.d(TAG, "onCreate: could not add followers informaitons Successfully.....");
                }
            }else{
                Log.d(TAG, "onCreate: could not add friends informaitons Successfully.....");
            }
        }else{
            Log.d(TAG, "onCreate: could not add User informaitons Successfully.....");
        }

    }

    private boolean getUserMessages() {

        mRoot.child("messages").child(mAuth.getCurrentUser().getUid())
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        int counter = 0;
                        if (fetchMessageForFriend(dataSnapshot.getKey())){
                            counter++;
                            Log.d(TAG, "onChildAdded: messages added = "+counter);
                        }
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        return;
                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                        return;
                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        return;
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        return;
                    }
                });


        return true;
    }

    private boolean fetchMessageForFriend(final String key) {

        mRoot.child("messages").child(mAuth.getCurrentUser().getUid()).child(key)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                        String MESSAGE_ID = dataSnapshot.getKey();
                        String FROM_UID = dataSnapshot.child("from").getValue().toString();
                        String TO_UID = key;
                        String CAPTION = dataSnapshot.child("caption").getValue().toString();
                        String DATE = dataSnapshot.child("date").getValue().toString();
                        String TIME = dataSnapshot.child("time").getValue().toString();
                        String MESSAGE = dataSnapshot.child("message").getValue().toString();
                        String TYPE = dataSnapshot.child("type").getValue().toString();
                        String LOCAL_LOCATION = " ";
                        String SYNCHRONIZED = "yes";

                        if (FROM_UID.equals(key)){
                            TO_UID = mAuth.getCurrentUser().getUid();
                        }

                        BackgroundTask backgroundTask = new BackgroundTask(UpdateLocalDB.this);
                        backgroundTask.execute("message_db", MESSAGE_ID, FROM_UID, TO_UID,
                                                CAPTION, DATE, TIME, MESSAGE, TYPE, LOCAL_LOCATION, SYNCHRONIZED);

                        Log.d(TAG, "onDataChange:  Message "+MESSAGE_ID+" added to table insertion........");



                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        return;
                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                        return;
                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        return;
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        return;
                    }
                });

        return true;
    }

    private boolean getFollowerFollowingAndFriendsInformation(final String which) {

        Log.d(TAG, "onCreate: getFollowerFollowingAndFriendsInformation");
        bool = false;
        mRoot.child("users").child(mAuth.getCurrentUser().getUid()).child(which)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        if (dataSnapshot.exists()){
                            String uid = dataSnapshot.getRef().getKey();
                            if (uid != null && !uid.equals(mAuth.getCurrentUser().getUid())){

                                if (getUserDetailsAndSave(which,uid)){
                                    mRoot.child("users").child(mAuth.getCurrentUser().getUid()).child(which)
                                            .child(uid).setValue("doneWith");
                                }else{
                                    Log.d(TAG, "onChildAdded: "+uid+" Couldnt be inserted in local db");
                                }
                            }


                        }
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        return;
                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                        return;
                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        return;
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        return;

                    }
                });

        return true;
    }

    private boolean getUserDetailsAndSave(final String which, String key) {
        Log.d(TAG, "onCreate: getUserDetailsAndSave");
        bool = true;
        mRoot.child("users").child(key)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChildren()){
                            String uid = dataSnapshot.getKey();
                            String indexNo = dataSnapshot.child("index").getValue().toString();
                            String name = dataSnapshot.child("name").getValue().toString();
                            String profileImage = dataSnapshot.child("profileImageUrl").getValue().toString();
                            String userState = " ";

                            BackgroundTask backgroundTask = new BackgroundTask(UpdateLocalDB.this);
                            backgroundTask.execute(which+"_db",uid,indexNo,name,profileImage,userState);

                            Log.d(TAG, "onDataChange:  "+which+" "+name+" table inserted........");

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                       return;
                    }
                });
        return true;
    }

    private boolean getUserInformation() {
        bool = false;

        mRoot.child("users").child(mAuth.getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChildren()){
                            String uid = dataSnapshot.getKey();
                            String indexNo = dataSnapshot.child("index").getValue().toString();
                            String name = dataSnapshot.child("name").getValue().toString();
                            String profileImage = dataSnapshot.child("profileImageUrl").getValue().toString();
                            String userState = " ";
                            String welcomed = dataSnapshot.child("welcomed").getValue().toString();

                            BackgroundTask backgroundTask = new BackgroundTask(UpdateLocalDB.this);
                            backgroundTask.execute("user_db",uid,indexNo,name,profileImage,userState,welcomed);

                            Log.d(TAG, "onDataChange: user "+name+" inserted into table........");

                        }

                        bool = true;
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                      return;
                    }
                });


        return true;
    }





    private BroadcastReceiver networkStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info = manager.getActiveNetworkInfo();

            if (info!=null){
                networkState = String.valueOf(info.isConnected());
                Toast.makeText(context, networkState, Toast.LENGTH_SHORT).show();
            }
        }
    };





    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(networkStateReceiver,new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }



    @Override
    protected void onPause() {
        unregisterReceiver(networkStateReceiver);
        super.onPause();
    }



    @Override
    public void onBackPressed() {
        Toast.makeText(this,
                "Please be patience, teminating this process will crush your account...",
                Toast.LENGTH_SHORT)
                .show();
    }
}
