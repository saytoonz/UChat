package com.nsromapa.uchat.recyclerchatactivity;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nsromapa.uchat.ChatActivity;
import com.nsromapa.uchat.databases.DBObjects;
import com.nsromapa.uchat.databases.DBOperations;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import timber.log.Timber;

public class ChatSendBackground extends AsyncTask<String, ChatsObjects, String> {

    private Context context;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private ArrayList<ChatsObjects> chatsObjects = MessagesArrayList.chatsObjects;
    private FirebaseAuth mAuth;
    private String FriendId;
    private static final String TAG = "ChatSendBackground";


    public ChatSendBackground(RecyclerView recyclerView, Context context, String FriendId) {
        this.recyclerView = recyclerView;
        this.context = context;
        this.FriendId =FriendId;
        Log.d(TAG,"ChatSendBackground: In CONSTRUCTOR");
    }


    @Override
    protected void onPreExecute() {
        mAuth = FirebaseAuth.getInstance();
        adapter = new ChatsAdapter(context,chatsObjects,recyclerView,FriendId);
        recyclerView.setAdapter(adapter);
    }


    @Override
    protected String doInBackground(String... params) {

        DBOperations dbOperations = new DBOperations(context);
        SQLiteDatabase database = dbOperations.getWritableDatabase();

        String messageId = params[1];
        String fromId = params[2];
        String toId = params[3];
        String caption = params[4];
        String _date = params[5];
        String _time = params[6];
        String message = params[7];
        String type = params[8];
        String state = "not sent";
        String local_loc = params[9];
        String sync_ed = params[10];

        if ((fromId.equals(toId) && toId.equals(mAuth.getCurrentUser().getUid()) ||
                toId.equals(toId) && fromId.equals(mAuth.getCurrentUser().getUid()))) {


            dbOperations.AddMessage(database, messageId, fromId, toId, caption,
                    _date, _time, message, type, state, local_loc, sync_ed);

            publishProgress(new ChatsObjects(messageId, fromId, message, type, caption, _date, _time,state,local_loc,sync_ed));
            Log.d(TAG,"ChatActivityBackground: In doInBackground " + messageId + " now Published");

        }else{
            Log.d(TAG,"ChatActivityBackground: In doInBackground " + messageId + " is not counted here");
        }

        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        database.close();
        return "One user row inserted for messages......";
    }

    @Override
    protected void onProgressUpdate(final ChatsObjects... values) {
        chatsObjects.add(values[0]);
        adapter.notifyDataSetChanged();
        MessagesArrayList.setChatsObjects(chatsObjects);

        //Scroll to the bottom of the chat.....
        recyclerView.smoothScrollToPosition(Objects.requireNonNull(recyclerView.getAdapter()).getItemCount());


        //Firebase Database References for messages
        //For both user and reciever
        String  messageSenderRef = "messages/"+ Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()  +"/"+ FriendId;
        String messageReceivererRef = "messages/"+ FriendId +"/"+ FirebaseAuth.getInstance().getCurrentUser().getUid();

        ////Get unique key for message
//        final String messagePushKey = theseUsersMessageTableRef.push().getKey();
        final String messagePushKey = values[0].getMessageID();


        Map<String,Object> messageTextBody = new HashMap<>();
        messageTextBody.put("messageID",messagePushKey);
        messageTextBody.put("message",values[0].getMessage());
        messageTextBody.put("caption",values[0].getCaption());
        messageTextBody.put("type",values[0].getType());
        messageTextBody.put("from",values[0].getFrom());
        messageTextBody.put("date",values[0].getDate());
        messageTextBody.put("time",values[0].getTime());
        messageTextBody.put("state","sent");





        Map<String, Object> messageBodyDetails = new HashMap<>();
        messageBodyDetails.put(messageSenderRef + "/" + messagePushKey, messageTextBody);
        messageBodyDetails.put(messageReceivererRef + "/" + messagePushKey, messageTextBody);

        FirebaseDatabase.getInstance().getReference().
                updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (!task.isSuccessful()){
                    Log.d(TAG, "onComplete: Message could not send....");
                }else{

                    Log.d(TAG,"onProgressUpdate: Message Sent");

                    //If inserted into Firebase
                    //Update local DB message state and synchronised
                    //And add it to the  main ui thread
                     DBOperations dbOperations = new DBOperations(context);
                    SQLiteDatabase database = dbOperations.getWritableDatabase();

                    ContentValues contentValues = new ContentValues();
                    contentValues.put(DBObjects.DBHelperObjects.STATE, "sent");
                    contentValues.put(DBObjects.DBHelperObjects.SYNCHRONIZED, "yes");

                    final String whereClause = DBObjects.DBHelperObjects.MESSAGE_ID +" = '"+messagePushKey+"'";


                    if (dbOperations.updateMessage(database, DBObjects.DBHelperObjects.MESSAGES_TABLE_NAME,contentValues,whereClause)) {
                        values[0].setState("sent");
                        adapter.notifyDataSetChanged();
                        Log.d(TAG,"onProgressUpdate: Message state updated to sent...");


                        //Then Add Value listerner to the message state
                        FirebaseDatabase.getInstance().getReference().child("messages")
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .child(FriendId).child(messagePushKey).child("state")
                                .addValueEventListener(new ValueEventListener() {

                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()){
                                            String newMessageState = dataSnapshot.getValue(String.class);

                                            DBOperations dbOperations = new DBOperations(context);
                                            SQLiteDatabase database = dbOperations.getWritableDatabase();

                                            ContentValues contentValues = new ContentValues();
                                            contentValues.put(DBObjects.DBHelperObjects.STATE, newMessageState);

                                            if (dbOperations.updateMessage(database, DBObjects.DBHelperObjects.MESSAGES_TABLE_NAME,contentValues,whereClause)){
                                                values[0].setState(newMessageState);
                                                adapter.notifyDataSetChanged();

                                                Log.d(TAG,"onDataChange: Message state updated to "+newMessageState+"...");
                                            }

                                        }else{
                                            Log.d(TAG, "onDataChange: Child has been moved or removed....");
                                        }

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                    }
                }
            }
        });
    }



    @Override
    protected void onPostExecute(String s) {
        Log.d( TAG,"onPostExecute: "+s);
    }
}
