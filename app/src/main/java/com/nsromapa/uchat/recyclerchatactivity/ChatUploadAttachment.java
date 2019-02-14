package com.nsromapa.uchat.recyclerchatactivity;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.nsromapa.uchat.ChatActivity;
import com.nsromapa.uchat.databases.DBObjects;
import com.nsromapa.uchat.databases.DBOperations;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import timber.log.Timber;

public class ChatUploadAttachment extends AsyncTask<String, String, String> {

    private Context context;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private ArrayList<ChatsObjects> chatsObjects = MessagesArrayList.chatsObjects;
    private FirebaseAuth mAuth;
    private String FriendId;
    private ProgressBar uploadProgressBar;
    private ImageView uploadRetry;
    private static final String TAG = "ChatSendBackground";


    public ChatUploadAttachment(ChatsAdapter adapter, RecyclerView recyclerView, Context context,
                                String FriendId, ProgressBar uploadProgressBar, ImageView uploadRetry) {
        this.recyclerView = recyclerView;
        this.context = context;
        this.FriendId = FriendId;
        this.uploadProgressBar = uploadProgressBar;
        this.uploadRetry = uploadRetry;
        this.adapter = adapter;
    }


    @Override
    protected void onPreExecute() {

        if (uploadRetry.getVisibility() == View.VISIBLE) {
            uploadRetry.setVisibility(View.GONE);
        }
        if (uploadProgressBar.getVisibility() == View.GONE || uploadProgressBar.getVisibility() == View.INVISIBLE) {
            uploadProgressBar.setVisibility(View.VISIBLE);
        }

        uploadProgressBar.setProgress(0);

        mAuth = FirebaseAuth.getInstance();
//        adapter = new ChatsAdapter(context, chatsObjects, recyclerView, FriendId);
        //recyclerView.setAdapter(adapter);


    }


    @Override
    protected String doInBackground(String... params) {

        switch (params[0]) {
            case "upload_attachment": {

                final String messageId = params[1];
                String local_location = params[2];
                final String fileType = params[3];
                final String caption = params[4];
                final int messagePosition = Integer.parseInt(params[5]);

                final File fileUri = new File(local_location);
                String folder = fileType;

                if (fileType.equals("image")) {
                    folder = "captures";
                }

                final StorageReference serverFilePath = FirebaseStorage.getInstance().getReference().child(folder).child(messageId);

                if (Uri.fromFile(fileUri) != null && !TextUtils.isEmpty(fileType)) {


                    //If inserted into Firebase
                    //Update local DB message state and synchronised
                    //And add it to the  main ui thread
                    DBOperations dbOperations = new DBOperations(context);
                    SQLiteDatabase database = dbOperations.getWritableDatabase();

                    ContentValues contentValues = new ContentValues();
                    contentValues.put(DBObjects.DBHelperObjects.MESSAGE, "");

                    String whereClause = DBObjects.DBHelperObjects.MESSAGE_ID + "='" + messageId + "'";

                    if (dbOperations.updateMessage(database,
                            DBObjects.DBHelperObjects.MESSAGES_TABLE_NAME,
                            contentValues, whereClause)) {
                        database.close();


                        final UploadTask uploadTask = serverFilePath.putFile(Uri.fromFile(fileUri));
                        uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                serverFilePath.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Uri> task) {
                                        final String fileUrl = Objects.requireNonNull(task.getResult()).toString();

                                        Calendar calendarFordate = Calendar.getInstance();
                                        SimpleDateFormat currentDateFormat = new SimpleDateFormat("MMM dd, yyyy");
                                        String currentDate = currentDateFormat.format(calendarFordate.getTime());

                                        Calendar calendarForTime = Calendar.getInstance();
                                        SimpleDateFormat currentTimeFormat = new SimpleDateFormat("hh:mm a");
                                        String currentTime = currentTimeFormat.format(calendarForTime.getTime());


                                        Map<String, Object> messageTextBody = new HashMap<>();
                                        messageTextBody.put("message", fileUrl);
                                        messageTextBody.put("messageID", messageId);
                                        messageTextBody.put("caption", caption);
                                        messageTextBody.put("type", fileType);
                                        messageTextBody.put("from", mAuth.getCurrentUser().getUid());
                                        messageTextBody.put("date", currentDate);
                                        messageTextBody.put("time", currentTime);
                                        messageTextBody.put("state", "sent");

                                        //Firebase Database References for messages
                                        //For both user and reciever
                                        String messageSenderRef = "messages/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/" + FriendId;
                                        String messageReceivererRef = "messages/" + FriendId + "/" + FirebaseAuth.getInstance().getCurrentUser().getUid();

                                        Map<String, Object> messageBodyDetails = new HashMap<>();
                                        messageBodyDetails.put(messageSenderRef + "/" + messageId, messageTextBody);
                                        messageBodyDetails.put(messageReceivererRef + "/" + messageId, messageTextBody);

                                        FirebaseDatabase.getInstance().getReference()
                                                .updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (!task.isSuccessful()) {
                                                    Log.d(TAG, "onComplete: Message could not send....");
                                                } else {

                                                    Log.d(TAG, "onProgressUpdate: Message Sent");

                                                    //If inserted into Firebase
                                                    //Update local DB message state and synchronised
                                                    //And add it to the  main ui thread
                                                    DBOperations dbOperations = new DBOperations(context);
                                                    SQLiteDatabase database = dbOperations.getWritableDatabase();

                                                    ContentValues contentValues = new ContentValues();
                                                    contentValues.put(DBObjects.DBHelperObjects.STATE, "sent");
                                                    contentValues.put(DBObjects.DBHelperObjects.SYNCHRONIZED, "yes");
                                                    contentValues.put(DBObjects.DBHelperObjects.MESSAGE, fileUrl);

                                                    String whereClause = DBObjects.DBHelperObjects.MESSAGE_ID + "='" + messageId + "'";

                                                    if (dbOperations.updateMessage(database,
                                                            DBObjects.DBHelperObjects.MESSAGES_TABLE_NAME,
                                                            contentValues, whereClause)) {
                                                        database.close();
                                                        chatsObjects.get(messagePosition).setState("sent");
                                                        chatsObjects.get(messagePosition).setSync_ed("yes");
                                                        chatsObjects.get(messagePosition).setMessage(fileUrl);
//
                                                        adapter.notifyDataSetChanged();
                                                        MessagesArrayList.setChatsObjects(chatsObjects);

                                                        //Scroll to the bottom of the chat.....
                                                        recyclerView.smoothScrollToPosition(recyclerView.getAdapter().getItemCount());

                                                    }
                                                }
                                            }
                                        });

                                    }
                                });

                            }
                        })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        publishProgress("failed");
                                    }
                                })
                                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                        int currentProgress = (int) (100 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                                        publishProgress(String.valueOf(currentProgress));
                                    }
                                });


                    } else {
                        Toast.makeText(context, "Unknown error...", Toast.LENGTH_SHORT).show();
                    }

                }

            }
        }

        return null;

    }


    @Override
    protected void onProgressUpdate(String... currentProgress) {
        if (currentProgress[0].equals("failed")){
            if (uploadRetry.getVisibility() == View.GONE ||
                    uploadRetry.getVisibility() == View.INVISIBLE) {
                uploadRetry.setVisibility(View.VISIBLE);
            }
            if (uploadProgressBar.getVisibility() == View.VISIBLE) {
                uploadProgressBar.setVisibility(View.GONE);
            }

            Toast.makeText(context, "There was an error....", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "onProgressUpdate: There was an error....");
        }else{
            uploadProgressBar.setProgress(Integer.parseInt(currentProgress[0]));
            Log.d(TAG, "onProgressUpdate: uploading....."+currentProgress[0]);
        }

    }


    @Override
    protected void onPostExecute(String s) {
        Log.d(TAG, "onPostExecute: " + s);

    }
}
