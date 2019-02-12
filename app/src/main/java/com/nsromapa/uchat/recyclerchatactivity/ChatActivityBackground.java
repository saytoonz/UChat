package com.nsromapa.uchat.recyclerchatactivity;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.nsromapa.uchat.databases.DBObjects;
import com.nsromapa.uchat.databases.DBOperations;

import java.util.ArrayList;

import timber.log.Timber;

public class ChatActivityBackground extends AsyncTask<String, ChatsObjects, Void> {
    private static final String TAG = "ChatActivityBackground";
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private ArrayList<ChatsObjects> chatsObjects = new ArrayList<>();
    private Context context;
    private FirebaseAuth mAuth;

    public ChatActivityBackground(RecyclerView recyclerView, Context context) {
        this.recyclerView = recyclerView;
        this.context = context;
        Log.d(TAG, "ChatActivityBackground: In CONSTRUCTOR");
    }

    @Override
    protected void onPreExecute() {
        mAuth = FirebaseAuth.getInstance();
        adapter = new ChatsAdapter(context, chatsObjects);
        recyclerView.setAdapter(adapter);
    }


    @Override
    protected Void doInBackground(String... params) {

        Log.d(TAG, "ChatActivityBackground: In doInBackground");

        String[] projections = {
                DBObjects.DBHelperObjects.MESSAGE_ID,
                DBObjects.DBHelperObjects.FROM_UID,
                DBObjects.DBHelperObjects.TO_UID,
                DBObjects.DBHelperObjects.CAPTION,
                DBObjects.DBHelperObjects.DATE,
                DBObjects.DBHelperObjects.TIME,
                DBObjects.DBHelperObjects.MESSAGE,
                DBObjects.DBHelperObjects.TYPE,
                DBObjects.DBHelperObjects.STATE,
                DBObjects.DBHelperObjects.LOCAL_LOCATION,
                DBObjects.DBHelperObjects.SYNCHRONIZED,
        };

        String messageID;
        String from;
        String toUid;
        String message;
        String type;
        String caption;
        String date;
        String time;
        String state;
        String local_loc;
        String sync_ed;

        String selectedFriendUid = params[0];

        DBOperations dbOperations = new DBOperations(context);
        SQLiteDatabase readableDatabase = dbOperations.getReadableDatabase();

        Cursor cursor = dbOperations.readFromLocalDB(readableDatabase, DBObjects.DBHelperObjects.MESSAGES_TABLE_NAME, projections);

        while (cursor.moveToNext()) {
            messageID = cursor.getString(cursor.getColumnIndex(DBObjects.DBHelperObjects.MESSAGE_ID));
            from = cursor.getString(cursor.getColumnIndex(DBObjects.DBHelperObjects.FROM_UID));
            toUid = cursor.getString(cursor.getColumnIndex(DBObjects.DBHelperObjects.TO_UID));
            message = cursor.getString(cursor.getColumnIndex(DBObjects.DBHelperObjects.MESSAGE));
            type = cursor.getString(cursor.getColumnIndex(DBObjects.DBHelperObjects.TYPE));
            caption = cursor.getString(cursor.getColumnIndex(DBObjects.DBHelperObjects.CAPTION));
            date = cursor.getString(cursor.getColumnIndex(DBObjects.DBHelperObjects.DATE));
            time = cursor.getString(cursor.getColumnIndex(DBObjects.DBHelperObjects.TIME));
            state = cursor.getString(cursor.getColumnIndex(DBObjects.DBHelperObjects.STATE));
            local_loc = cursor.getString(cursor.getColumnIndex(DBObjects.DBHelperObjects.LOCAL_LOCATION));
            sync_ed = cursor.getString(cursor.getColumnIndex(DBObjects.DBHelperObjects.SYNCHRONIZED));

            if ((from.equals(selectedFriendUid) && toUid.equals(mAuth.getCurrentUser().getUid()) ||
                    toUid.equals(selectedFriendUid) && from.equals(mAuth.getCurrentUser().getUid()))) {


                publishProgress(new ChatsObjects(messageID, from, message, type, caption, date, time,state,local_loc,sync_ed));
                Timber.d("ChatActivityBackground: In doInBackground " + messageID + " now Published");

                try {
                    Thread.sleep(70);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }else{
                Timber.d("ChatActivityBackground: In doInBackground " + messageID + " is not counted here");
                try {
                    Thread.sleep(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }
        cursor.close();
        readableDatabase.close();


        return null;
    }

    @Override
    protected void onProgressUpdate(ChatsObjects... values) {
        chatsObjects.add(values[0]);
        new MessagesArrayList().setChatsObjects(chatsObjects);
        adapter.notifyDataSetChanged();
        Log.d(TAG, "onProgressUpdate: In Progress update");
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        recyclerView.smoothScrollToPosition(recyclerView.getAdapter().getItemCount());
    }
}
