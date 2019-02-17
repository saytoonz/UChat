package com.nsromapa.uchat;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.nsromapa.uchat.cameraUtils.Config;
import com.nsromapa.uchat.usersInfos.UserInformation;
import com.nsromapa.uchat.recyclerviewreceiver.ReceiverAdapater;
import com.nsromapa.uchat.recyclerviewreceiver.ReceiverObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ChooseReceiverActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private String Uid, caption, fileType, fileLoc;
    private Bitmap bitmap;

    private CheckBox mStory;
    private LinearLayout full_story_btn;
    private TextView storyTextView;
    private Long currentTimestamp;
    private Long endTimestamp;

    private ProgressBar mProgressBar;

    private String currentDate,currentTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_receiver);

        caption = getIntent().getStringExtra("caption");
        fileLoc = getIntent().getStringExtra("fileLoc");
        fileType = getIntent().getStringExtra("fileType");

        bitmap = BitmapFactory.decodeFile(fileLoc);

        Uid = FirebaseAuth.getInstance().getUid();

        mProgressBar = findViewById(R.id.a_choose_receiver_progressBar);

        mStory = findViewById(R.id.story);
        storyTextView = findViewById(R.id.story_text);
        full_story_btn = findViewById(R.id.full_story_btn);
        mStory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectAndDeselectStory();
            }
        });
        full_story_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectAndDeselectStory();
            }
        });

        mRecyclerView = findViewById(R.id.recyclerView_send_message_user);
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getApplication());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new ReceiverAdapater(getDataSet(),getApplication());
        mRecyclerView.setAdapter(mAdapter);

        FloatingActionButton mFab = findViewById(R.id.fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mStory.isChecked() || results.size()>0){
                    saveToStories();
                    Toast.makeText(ChooseReceiverActivity.this, "Sending....", Toast.LENGTH_SHORT).show();
                    finish();

                }else{

                    Toast.makeText(ChooseReceiverActivity.this, "Select story or receiver...,", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    private ArrayList<ReceiverObject> results = new ArrayList<>();
    private ArrayList<ReceiverObject> getDataSet() {
        listenForData();
        return results;
    }

    private void listenForData() {
        for(int i = 0; i < UserInformation.listFollowing.size(); i++){
            DatabaseReference usersDb = FirebaseDatabase.getInstance().getReference().child("users").child(UserInformation.listFollowing.get(i));
            usersDb.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String name = "";
                    String index ="";
                    String profileImageUrl = "";
                    String uid = dataSnapshot.getRef().getKey();
                    if(dataSnapshot.child("email").getValue() != null){
                        name = dataSnapshot.child("name").getValue().toString();
                        index = dataSnapshot.child("index").getValue().toString();
                        profileImageUrl = dataSnapshot.child("profileImageUrl").getValue().toString();
                    }
                    ReceiverObject obj = new ReceiverObject(name, uid, index, profileImageUrl,false);
                    if(!results.contains(obj)){
                        results.add(obj);
                        mAdapter.notifyDataSetChanged();
                        if (mProgressBar.getVisibility() == View.VISIBLE)
                            mProgressBar.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }


    private void saveToStories() {
        final DatabaseReference userStoryDb = FirebaseDatabase.getInstance().getReference().child("users").child(Uid).child("stories");
        final String key = userStoryDb.push().getKey();

        final StorageReference filePath;

        UploadTask uploadTask;

        if (fileType.equals(Config.MessageType.IMAGE)){
            filePath = FirebaseStorage.getInstance().getReference().child("captures").child(key);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
            byte[] dataToUpload = baos.toByteArray();
            uploadTask = filePath.putBytes(dataToUpload);
        }else {
            filePath = FirebaseStorage.getInstance().getReference().child("video").child(key);
            uploadTask = filePath.putFile(Uri.fromFile(new File(fileLoc)));
//            Toast.makeText(this, "Check type is "+ fileType, Toast.LENGTH_SHORT).show();
//            return;
        }
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                filePath.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        String fileUrl =Objects.requireNonNull(task.getResult()).toString();

                        Calendar calendarFordate = Calendar.getInstance();
                        SimpleDateFormat currentDateFormat = new SimpleDateFormat("MMM dd, yyyy");
                        currentDate = currentDateFormat.format(calendarFordate.getTime());

                        Calendar calendarForTime= Calendar.getInstance();
                        SimpleDateFormat currentTimeFormat = new SimpleDateFormat("hh:mm a");
                        currentTime = currentTimeFormat.format(calendarForTime.getTime());


                if(mStory.isChecked()){
                    Map<String, Object> mapToUpload = new HashMap<>();
                    mapToUpload.put("caption",caption);
                    mapToUpload.put("imageUrl", fileUrl);
                    mapToUpload.put("type", fileType);
                    mapToUpload.put("timestampBeg", currentTimestamp);
                    mapToUpload.put("timestampEnd", endTimestamp);
                    mapToUpload.put("date",currentDate);
                    mapToUpload.put("time",currentTime);
                    mapToUpload.put("state","sent");
                    userStoryDb.child(key).setValue(mapToUpload);
                }
                for(int i = 0; i< results.size(); i++){
                    if(results.get(i).getReceive()){
                        SendUrlInMessagesMessage(Uid, results.get(i).getUid(), fileUrl, caption);
                    }
                }

                finish();

                    }
                });
            }
        });



        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                finish();
            }
        });

    }


    private void SendUrlInMessagesMessage(String sender_user_id, String receiver_user_id,String fileUrl, String caption) {

        DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();

            String messageSenderRef = "messages/"+ sender_user_id +"/"+ receiver_user_id;
            String messageReceivererRef = "messages/"+ receiver_user_id +"/"+ sender_user_id;
            String  messagePushKey = mRootRef.child("Messages")
                    .child(sender_user_id).child(receiver_user_id).push().getKey();


        Map<String,Object> messageTextBody = new HashMap<>();
        messageTextBody.put("messageID",messagePushKey);
        messageTextBody.put("message",fileUrl);
        messageTextBody.put("caption",caption);
        messageTextBody.put("type",fileType);
        messageTextBody.put("from",sender_user_id);
        messageTextBody.put("date",currentDate);
        messageTextBody.put("time",currentTime);
        messageTextBody.put("state","sent");


        Map<String, Object> messageBodyDetails = new HashMap<>();
        messageBodyDetails.put(messageSenderRef + "/" + messagePushKey, messageTextBody);
        messageBodyDetails.put(messageReceivererRef + "/" + messagePushKey, messageTextBody);

        mRootRef.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (!task.isSuccessful()){
                    Toast.makeText(ChooseReceiverActivity.this, "Message could not send....", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    private void selectAndDeselectStory() {
        if (mStory.isChecked()){
            mStory.setChecked(false);
            full_story_btn.setBackgroundDrawable(ContextCompat.getDrawable(ChooseReceiverActivity.this,R.drawable.select_story_or_feed_background));
            storyTextView.setTextColor(ContextCompat.getColor(ChooseReceiverActivity.this, R.color.black));
        }else{
            mStory.setChecked(true);
            full_story_btn.setBackgroundDrawable(ContextCompat.getDrawable(ChooseReceiverActivity.this,R.drawable.select_story_or_feed_background_second));
            storyTextView.setTextColor(ContextCompat.getColor(ChooseReceiverActivity.this, R.color.white));

            final CharSequence[] days = {"12 Hours","1 Day","2 Days","3 Days"};
            AlertDialog.Builder builder = new AlertDialog.Builder(ChooseReceiverActivity.this);
            builder.setTitle("Select time for your story...");
            builder.setIcon(R.drawable.ic_group_work_24dp);
            builder.setCancelable(false);
            builder.setSingleChoiceItems(days, -1, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    currentTimestamp = System.currentTimeMillis();

                    int time = (24 * 60 * 60 * 1000);
                    if (which == 0) {
                        time = (12 * 60 * 60 * 1000);
                    } else if (which == 2) {
                        time = (48 * 60 * 60 * 1000);
                    } else if (which == 3) {
                        time = (72 * 60 * 60 * 1000);
                    }
                    endTimestamp = currentTimestamp + time;

                    Toast.makeText(ChooseReceiverActivity.this, currentTimestamp+" "+endTimestamp, Toast.LENGTH_SHORT).show();
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    currentTimestamp = System.currentTimeMillis();
                    endTimestamp = currentTimestamp + ((24 * 60 * 60 * 1000));
                    mStory.setChecked(false);
                    full_story_btn.setBackgroundDrawable(ContextCompat.getDrawable(ChooseReceiverActivity.this, R.drawable.select_story_or_feed_background));
                    storyTextView.setTextColor(ContextCompat.getColor(ChooseReceiverActivity.this, R.color.black));
                }
            });

            AlertDialog alert = builder.create();
            alert.show();

        }
    }


    private void updateStatus(String state) {

        HashMap<String, Object> onlineState = new HashMap<>();
        onlineState.put("state", state);

        FirebaseDatabase.getInstance().getReference()
                .child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("userState")
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
