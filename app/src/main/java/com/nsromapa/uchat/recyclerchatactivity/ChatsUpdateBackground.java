package com.nsromapa.uchat.recyclerchatactivity;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.nsromapa.uchat.databases.DBObjects;
import com.nsromapa.uchat.databases.DBOperations;

public class ChatsUpdateBackground extends AsyncTask<String,Void,String>{
    private static final String TAG = "ChatsUpdateBackground";
    private Context context;


    public ChatsUpdateBackground(Context context) {
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params) {

        switch (params[0]){
            case "message_db":{
                String messageId = params[1];
                String fromId = params[2];
                String toId = params[3];
                String caption = params[4];
                String _date = params[5];
                String _time = params[6];
                String message = params[7];
                String type = params[8];
                String state = params[9];
                String local_loc = params[10];
                String sync_ed = params[11];

                DBOperations dbOperations = new DBOperations(context);
                SQLiteDatabase database = dbOperations.getWritableDatabase();

                dbOperations.AddMessage(database, messageId, fromId, toId, caption,
                        _date, _time, message, type, state ,local_loc, sync_ed);

                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                database.close();
                return "One user row inserted for messages......";
            }
            case "update_message":{
                String messageId = params[1];
                String fromId = params[2];
                String toId = params[3];
                String caption = params[4];
                String _date = params[5];
                String _time = params[6];
                String message = params[7];
                String type = params[8];
                String state = params[9];
                String local_loc = params[10];
                String sync_ed = params[11];

                DBOperations dbOperations = new DBOperations(context);
                SQLiteDatabase database = dbOperations.getWritableDatabase();

                ContentValues contentValues = new ContentValues();
                contentValues.put(DBObjects.DBHelperObjects.FROM_UID, fromId);
                contentValues.put(DBObjects.DBHelperObjects.TO_UID, toId);
                contentValues.put(DBObjects.DBHelperObjects.CAPTION, caption);
                contentValues.put(DBObjects.DBHelperObjects.DATE, _date);
                contentValues.put(DBObjects.DBHelperObjects.TIME, _time);
                contentValues.put(DBObjects.DBHelperObjects.MESSAGE, message);
                contentValues.put(DBObjects.DBHelperObjects.TYPE, type);
                contentValues.put(DBObjects.DBHelperObjects.STATE, state);
                contentValues.put(DBObjects.DBHelperObjects.LOCAL_LOCATION, local_loc);
                contentValues.put(DBObjects.DBHelperObjects.SYNCHRONIZED, sync_ed);

                final String whereClause = DBObjects.DBHelperObjects.MESSAGE_ID +" = '"+messageId+"'";

                if (dbOperations.updateMessage(database, DBObjects.DBHelperObjects.MESSAGES_TABLE_NAME,contentValues,whereClause)){

                    Log.d(TAG,"doInBackground: Message state updated to "+state+"...");
                }

                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                database.close();
                return "One row for messages updated......";
            }
            case "message_deleted":{

                String messageId = params[1];
                String fromId = params[2];
                String toId = params[3];
                String caption = params[4];
                String _date = params[5];
                String _time = params[6];
                String message = params[7];
                String type = params[8];
                String state = params[9];
                String local_loc = params[10];
                String sync_ed = params[11];

                DBOperations dbOperations = new DBOperations(context);
                SQLiteDatabase database = dbOperations.getWritableDatabase();

                ContentValues contentValues = new ContentValues();
                contentValues.put(DBObjects.DBHelperObjects.FROM_UID, fromId);
                contentValues.put(DBObjects.DBHelperObjects.TO_UID, toId);
                contentValues.put(DBObjects.DBHelperObjects.CAPTION, "");
                contentValues.put(DBObjects.DBHelperObjects.DATE, _date);
                contentValues.put(DBObjects.DBHelperObjects.TIME, _time);
                contentValues.put(DBObjects.DBHelperObjects.MESSAGE, "Message has been deleted...");
                contentValues.put(DBObjects.DBHelperObjects.TYPE, "text");
                contentValues.put(DBObjects.DBHelperObjects.STATE, state);
                contentValues.put(DBObjects.DBHelperObjects.LOCAL_LOCATION, "");
                contentValues.put(DBObjects.DBHelperObjects.SYNCHRONIZED, sync_ed);

                final String whereClause = DBObjects.DBHelperObjects.MESSAGE_ID +" = '"+messageId+"'";

                if (dbOperations.updateMessage(database, DBObjects.DBHelperObjects.MESSAGES_TABLE_NAME,contentValues,whereClause)){

                    Log.d(TAG,"doInBackground: Message has been deleted...");
                }

                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                database.close();
                return "One row for messages deleted......";
            }
            default:{

            }
            break;
        }

        return null;
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(String s) {
        Log.d(TAG, "onPostExecute: "+s);
    }
}
