package com.nsromapa.uchat.recyclerfeeds;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.nsromapa.uchat.databases.DBObjects;
import com.nsromapa.uchat.databases.DBOperations;
import com.nsromapa.uchat.recyclerviewchatfragment.ChatFragmentObject;

import java.util.HashMap;


public class SendPostBackground extends AsyncTask<String, String, Void> {
    private final static String TAG = "SendPostBackground";
    private Context context;

    private FirebaseAuth mAuth;
    private DatabaseReference mPostRef;

    public SendPostBackground(Context context) {
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        mAuth = FirebaseAuth.getInstance();
        mPostRef = FirebaseDatabase.getInstance().getReference().child("posts");
        Log.d(TAG, "onPreExecute: Post creating.....");
        Toast.makeText(context, "You will be notified when your post is shared...", Toast.LENGTH_SHORT).show();
        ((Activity) context).finish();
    }

    @Override
    protected Void doInBackground(String... params) {


        switch (params[0]) {
            case "uploadText": {
                final String textPost_caption = params[1];
                final String shareWithText = params[2];
                final String fontFamily = params[3];
                final String fontSize = params[4];
                final String backgroundSelected = params[5];
                final String _time = params[6];
                final String _date = params[7];
                final String fromUid = mAuth.getCurrentUser().getUid();
                final String postId = String.valueOf(System.currentTimeMillis());


                HashMap<String, Object> postInfo = new HashMap<>();
                postInfo.put("text", textPost_caption);
                postInfo.put("size", fontSize);
                postInfo.put("style", fontFamily);
                postInfo.put("background", backgroundSelected);
                postInfo.put("privacy", shareWithText);
                postInfo.put("url", "");
                postInfo.put("date", _date);
                postInfo.put("time", _time);
                postInfo.put("from", fromUid);
                postInfo.put("state", "sent");
                postInfo.put("locLong", "");
                postInfo.put("locLat", "");
                postInfo.put("likes", "");
                postInfo.put("hates", "");
                postInfo.put("type", "test_post");
                postInfo.put("postId",postId);

                mPostRef.child(postId)
                        .updateChildren(postInfo).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            publishProgress("success");
                        } else {
                            publishProgress("Sorry there was an error....");
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        publishProgress("Post creation was unsuccessful\nPlease try again!: "+e);
                    }
                });


                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


            }
            break;
        }


        return null;
    }

    @Override
    protected void onProgressUpdate(String... s) {
        if (s[0].equals("success")) {
            Log.d(TAG, "onProgressUpdate: Post created");
            Toast.makeText(context, "Your post is shared successfully", Toast.LENGTH_SHORT).show();
        } else {
            Log.d(TAG, "onProgressUpdate: Post cound not create");
            Toast.makeText(context, s[0], Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
    }
}
