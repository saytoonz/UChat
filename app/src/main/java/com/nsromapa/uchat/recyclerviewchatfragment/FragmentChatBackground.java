package com.nsromapa.uchat.recyclerviewchatfragment;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.nsromapa.uchat.databases.DBObjects;
import com.nsromapa.uchat.databases.DBOperations;

import java.util.ArrayList;

public class FragmentChatBackground extends AsyncTask<String, ChatFragmentObject, Void> {
    private static final String TAG = "FragmentChatBackground";
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private ArrayList<ChatFragmentObject> chatFragmentObjects = new ArrayList<>();
    private Context context;
    private ProgressBar progressBar;


    public FragmentChatBackground(RecyclerView recyclerView, ProgressBar progressBar, Context context) {
        this.recyclerView = recyclerView;
        this.progressBar = progressBar;
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        adapter = new ChatFragmentAdapater(chatFragmentObjects, context);
        recyclerView.setAdapter(adapter);
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected Void doInBackground(String... params) {

        DBOperations dbOperations = new DBOperations(context);
        SQLiteDatabase database = dbOperations.getReadableDatabase();
        Cursor cursor=null;

        if (params[0].equals("friends_forChat")){
            String[] projections = {DBObjects.DBHelperObjects.UID,
                    DBObjects.DBHelperObjects.NAME,
                    DBObjects.DBHelperObjects.PROFILEIMAGE,
                    DBObjects.DBHelperObjects.USER_STATE};

            String name;
            String uid;
            String state;
            String profileImageUrl;

            cursor = dbOperations.readFromLocalDB(database, DBObjects.DBHelperObjects.FOLLOWING_TABLE_NAME, projections);
            while (cursor.moveToNext()) {

                name = cursor.getString(cursor.getColumnIndex(DBObjects.DBHelperObjects.NAME));
                uid = cursor.getString(cursor.getColumnIndex(DBObjects.DBHelperObjects.UID));
                state = cursor.getString(cursor.getColumnIndex(DBObjects.DBHelperObjects.USER_STATE));
                profileImageUrl = cursor.getString(cursor.getColumnIndex(DBObjects.DBHelperObjects.PROFILEIMAGE));

                publishProgress(new ChatFragmentObject(name, uid, state, profileImageUrl));

                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }


        cursor.close();
        database.close();
        return null;
    }

    @Override
    protected void onProgressUpdate(ChatFragmentObject... values) {
        chatFragmentObjects.add(values[0]);
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        progressBar.setVisibility(View.GONE);
    }
}
