package com.nsromapa.uchat.databases;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.widget.Toast;


public class BackgroundTask extends AsyncTask<String, Void, String> {

    private Context context;

    public BackgroundTask(Context context) {
        this.context = context;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }


    @Override
    protected String doInBackground(String... params) {

        DBOperations dbOperations = new DBOperations(context);
        SQLiteDatabase database = dbOperations.getWritableDatabase();

        switch (params[0]) {
            case "user_db": {
                String uid = params[1];
                String index = params[2];
                String name = params[3];
                String profileImage = params[4];
                String state = params[5];
                String welcomed = params[6];

                dbOperations.AddUser(database, uid, index, name, profileImage, state, welcomed);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                database.close();
                return "One user row inserted for user......";


            }
            case "friends_db":
            case "followers_db":
            case "following_db": {
                String tablename = "";
                switch (params[0]) {
                    case "friends_db":
                        tablename = DBObjects.DBHelperObjects.FRIENDS_TABLE_NAME;
                        break;
                    case "followers_db":
                        tablename = DBObjects.DBHelperObjects.FOLLOWERS_TABLE_NAME;

                        break;
                    case "following_db":
                        tablename = DBObjects.DBHelperObjects.FOLLOWING_TABLE_NAME;
                        break;
                }
                String uid = params[1];
                String index = params[2];
                String name = params[3];
                String profileImage = params[4];
                String state = params[5];

                dbOperations.AddtoFollowAndFriends(tablename, database, uid, index, name, profileImage, state);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                database.close();
                return "One " + params[0] + " row inserted correctly......";


            }
            case "message_db":
                String messageId = params[1];
                String fromId = params[2];
                String toId = params[3];
                String caption = params[4];
                String _date = params[5];
                String _time = params[6];
                String message = params[6];
                String type = params[6];
                String local_loc = params[6];
                String sync_ed = params[6];


                dbOperations.AddMessage(database, messageId, fromId, toId, caption,
                        _date, _time, message, type, local_loc, sync_ed);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                database.close();
                return "One user row inserted for messages......";


            default: {

                String id = params[1];
                String name = params[2];
                dbOperations.AddStories(database, id, name, name, name, name, name, name, name, id, id);

                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                database.close();
                break;
            }
        }

        return null;

    }


    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(String result) {
        Toast.makeText(context, result, Toast.LENGTH_LONG).show();
    }

}
