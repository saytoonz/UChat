package com.nsromapa.uchat.recyclerfeeds;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.nsromapa.uchat.databases.DBObjects;
import com.nsromapa.uchat.databases.DBOperations;
import com.nsromapa.uchat.recyclerviewchatfragment.ChatFragmentObject;

import java.io.File;
import java.util.HashMap;
import java.util.Objects;


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
            case "PostText": {
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
                postInfo.put("postId", postId);

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
                        publishProgress("Post creation was unsuccessful\nPlease try again!: " + e);
                    }
                });


                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


            }
            break;

            case "image":
            case "video": {
                final String textPost_caption = params[1];
                final String shareWithText = params[2];
                final String fontFamily = params[3];
                final String fontSize = params[4];
                final String backgroundSelected = params[5];
                final String _time = params[6];
                final String _date = params[7];
                final String fileType = params[8];
                final String fileUrl = params[9];
                final String fromUid = mAuth.getCurrentUser().getUid();
                final String postId = String.valueOf(System.currentTimeMillis());


                String folder = fileType;

                if (fileType.equals("image")) {
                    folder = "captures";
                }

                final StorageReference serverFilePath = FirebaseStorage.getInstance().getReference().child(folder).child(postId);
                if (Uri.fromFile(new File(fileUrl)) != null && !TextUtils.isEmpty(fileType)) {
                    UploadTask uploadTask = serverFilePath.putFile(Uri.parse(fileUrl));
                    uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            serverFilePath.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    final String fileUrl = Objects.requireNonNull(task.getResult()).toString();

                                    publishProgress("File Uploaded now....");

                                    HashMap<String, Object> postInfo = new HashMap<>();
                                    postInfo.put("text", textPost_caption);
                                    postInfo.put("size", fontSize);
                                    postInfo.put("style", fontFamily);
                                    postInfo.put("background", backgroundSelected);
                                    postInfo.put("privacy", shareWithText);
                                    postInfo.put("url", fileUrl);
                                    postInfo.put("date", _date);
                                    postInfo.put("time", _time);
                                    postInfo.put("from", fromUid);
                                    postInfo.put("state", "sent");
                                    postInfo.put("locLong", "");
                                    postInfo.put("locLat", "");
                                    postInfo.put("likes", "");
                                    postInfo.put("hates", "");
                                    postInfo.put("type", fileType);
                                    postInfo.put("postId", postId);


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
                                            publishProgress("Post creation was unsuccessful\nPlease try again!: " + e);
                                        }
                                    });


                                }
                            });
                        }
                    });
                } else {
                    publishProgress("Sorry there was an error....");
                }

                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


            }
            break;
            default: {
                publishProgress("Sorry, there was an unknown error");
            }
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
