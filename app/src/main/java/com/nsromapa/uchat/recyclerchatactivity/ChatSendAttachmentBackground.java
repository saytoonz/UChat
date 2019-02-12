package com.nsromapa.uchat.recyclerchatactivity;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nsromapa.uchat.databases.DBObjects;
import com.nsromapa.uchat.databases.DBOperations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import timber.log.Timber;

public class ChatSendAttachmentBackground extends AsyncTask<String, ChatsObjects, String> {

    private Context context;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private ArrayList<ChatsObjects> chatsObjects = MessagesArrayList.chatsObjects;
    private FirebaseAuth mAuth;
    private String FriendReceiver;
    private static final String TAG = "ChatSendBackground";


    public ChatSendAttachmentBackground(RecyclerView recyclerView, Context context, String FriendId) {
        this.recyclerView = recyclerView;
        this.context = context;
        this.FriendReceiver = FriendId;
        Timber.d("ChatSendBackground: In CONSTRUCTOR");
    }


    @Override
    protected void onPreExecute() {
        mAuth = FirebaseAuth.getInstance();
        adapter = new ChatsAdapter(context, chatsObjects);
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

            publishProgress(new ChatsObjects(messageId, fromId, message, type, caption, _date, _time, state,local_loc,sync_ed));
            Timber.d("ChatActivityBackground: In doInBackground " + messageId + " now Published");

        } else {
            Timber.d("ChatActivityBackground: In doInBackground " + messageId + " is not counted here");
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
        recyclerView.smoothScrollToPosition(recyclerView.getAdapter().getItemCount());


    }


    @Override
    protected void onPostExecute(String s) {
        Log.d(TAG, "onPostExecute: " + s);
    }
}
