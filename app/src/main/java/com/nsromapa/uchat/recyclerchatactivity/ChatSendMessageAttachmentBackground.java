package com.nsromapa.uchat.recyclerchatactivity;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.nsromapa.uchat.databases.DBOperations;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import timber.log.Timber;

public class ChatSendMessageAttachmentBackground extends AsyncTask<String, ChatsObjects, String> {

    private Context context;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private ArrayList<ChatsObjects> chatsObjects = MessagesArrayList.chatsObjects;
    private FirebaseAuth mAuth;
    private String FriendId;
    private String friendName;
    private static final String TAG = "ChatSendBackground";


    public ChatSendMessageAttachmentBackground(RecyclerView recyclerView,
                                               Context context,
                                               String FriendId,
                                               String friendName) {
        this.recyclerView = recyclerView;
        this.context = context;
        this.FriendId = FriendId;
        this.friendName = friendName;
    }


    @Override
    protected void onPreExecute() {
        mAuth = FirebaseAuth.getInstance();
        adapter = new ChatsAdapter(context, chatsObjects,recyclerView,FriendId,friendName);
        //recyclerView.setAdapter(adapter);
    }


    @Override
    protected String doInBackground(String... params) {

        switch (params[0]) {
            case "sendMessageAttachment": {
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

                if ((fromId.equals(toId) && toId.equals(Objects.requireNonNull(mAuth.getCurrentUser()).getUid()) ||
                        fromId.equals(Objects.requireNonNull(mAuth.getCurrentUser()).getUid()))) {


                    dbOperations.AddMessage(database, messageId, fromId, toId, caption,
                            _date, _time, message, type, state, local_loc, sync_ed);

                    publishProgress(new ChatsObjects(messageId, fromId, message, type, caption, _date, _time, state, local_loc, sync_ed));
                    Log.d(TAG,"ChatActivityBackground: In doInBackground " + messageId + " now Published");

                } else {
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

        }

        return null;

    }


    @Override
    protected void onProgressUpdate(final ChatsObjects... values) {
        chatsObjects.add(values[0]);
        adapter.notifyDataSetChanged();
        MessagesArrayList.setChatsObjects(chatsObjects);
        //Scroll to the bottom of the chat.....
        recyclerView.smoothScrollToPosition(recyclerView.getAdapter().getItemCount());

        Log.d(TAG, "onProgressUpdate: Time to upload.....");
    }


    @Override
    protected void onPostExecute(String s) {
        Log.d(TAG, "onPostExecute: " + s);
    }
}
