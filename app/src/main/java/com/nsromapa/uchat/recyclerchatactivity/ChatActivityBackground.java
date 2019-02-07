package com.nsromapa.uchat.recyclerchatactivity;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.nsromapa.uchat.databases.DBObjects;
import com.nsromapa.uchat.databases.DBOperations;

import java.util.ArrayList;

public class ChatActivityBackground extends AsyncTask<Void, ChatsObjects, Void> {
    private static final String TAG = "ChatActivityBackground";
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private ArrayList<ChatsObjects> chatsObjects = new ArrayList<>();
    private Context context;

    public ChatActivityBackground(RecyclerView recyclerView, Context context) {
        this.recyclerView = recyclerView;
        this.context = context;
        Log.d(TAG, "ChatActivityBackground: In CONSTRUCTOR");
    }

    @Override
    protected void onPreExecute() {
        adapter = new ChatsAdapter(context, chatsObjects);
        recyclerView.setAdapter(adapter);
    }


    @Override
    protected Void doInBackground(Void... voids) {

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
                DBObjects.DBHelperObjects.LOCAL_LOCATION,
                DBObjects.DBHelperObjects.SYNCHRONIZED,
        };

        String messageID;
        String from;
        String message;
        String type;
        String caption;
        String date;
        String time;

        DBOperations dbOperations = new DBOperations(context);
        SQLiteDatabase readableDatabase = dbOperations.getReadableDatabase();

        Cursor cursor = dbOperations.readFromLocalDB(readableDatabase, DBObjects.DBHelperObjects.MESSAGES_TABLE_NAME, projections);

        while (cursor.moveToNext()) {
            messageID = cursor.getString(cursor.getColumnIndex(DBObjects.DBHelperObjects.MESSAGE_ID));
            from = cursor.getString(cursor.getColumnIndex(DBObjects.DBHelperObjects.MESSAGE_ID));
            message = cursor.getString(cursor.getColumnIndex(DBObjects.DBHelperObjects.MESSAGE_ID));
            type = cursor.getString(cursor.getColumnIndex(DBObjects.DBHelperObjects.MESSAGE_ID));
            caption = cursor.getString(cursor.getColumnIndex(DBObjects.DBHelperObjects.MESSAGE_ID));
            date = cursor.getString(cursor.getColumnIndex(DBObjects.DBHelperObjects.MESSAGE_ID));
            time = cursor.getString(cursor.getColumnIndex(DBObjects.DBHelperObjects.MESSAGE_ID));

            Log.d(TAG, "ChatActivityBackground: In doInBackground "+ messageID);
            
            publishProgress(new ChatsObjects(messageID, from, message, type, caption, date, time));

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
        cursor.close();
        readableDatabase.close();


        return null;
    }

    @Override
    protected void onProgressUpdate(ChatsObjects... values) {
        chatsObjects.add(values[0]);
        adapter.notifyDataSetChanged();
        Log.d(TAG, "onProgressUpdate: Ind Progress update");
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
    }
}
