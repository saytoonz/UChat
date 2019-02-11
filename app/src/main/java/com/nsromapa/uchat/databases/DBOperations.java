package com.nsromapa.uchat.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBOperations extends SQLiteOpenHelper {


    private static DBOperations instance;

    static public synchronized DBOperations getInstance(Context context) {
        if (instance == null)
            instance = new DBOperations(context);
        return instance;
    }


    private static final int DB_VERSION = 1;
    public static final String DB_NAME = "product.db";
    private static final String TAG = "Database Operations";

    public DBOperations(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DBObjects.DBHelperObjects.CREATE_FOLLOWERS_TABLE_QUERY);
        db.execSQL(DBObjects.DBHelperObjects.CREATE_FOLLOWING_TABLE_QUERY);
        db.execSQL(DBObjects.DBHelperObjects.CREATE_FRIENDS_TABLE_QUERY);
        db.execSQL(DBObjects.DBHelperObjects.CREATE_MESSAGES_TABLE_QUERY);
        db.execSQL(DBObjects.DBHelperObjects.CREATE_MY_STORIES_TABLE_QUERY);
        db.execSQL(DBObjects.DBHelperObjects.CREATE_USERS_TABLE_QUERY);

        Log.d(TAG, "Table is created successfully.......");
    }


    public void AddUser(SQLiteDatabase db, String uid, String index,
                        String name, String profileImage, String state,
                        String welcomed) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBObjects.DBHelperObjects.UID, uid);
        contentValues.put(DBObjects.DBHelperObjects.INDEX, index);
        contentValues.put(DBObjects.DBHelperObjects.NAME, name);
        contentValues.put(DBObjects.DBHelperObjects.PROFILEIMAGE, profileImage);
        contentValues.put(DBObjects.DBHelperObjects.USER_STATE, state);
        contentValues.put(DBObjects.DBHelperObjects.WELCOMED, welcomed);

        db.insert(DBObjects.DBHelperObjects.USERS_TABLE_NAME, null, contentValues);

        Log.d(TAG, "One row inserted into" + DBObjects.DBHelperObjects.USERS_TABLE_NAME + ".......");
    }


    public void AddtoFollowAndFriends(String table, SQLiteDatabase db, String uid,
                                      String index, String name, String profileImage,
                                      String state) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBObjects.DBHelperObjects.UID, uid);
        contentValues.put(DBObjects.DBHelperObjects.INDEX, index);
        contentValues.put(DBObjects.DBHelperObjects.NAME, name);
        contentValues.put(DBObjects.DBHelperObjects.PROFILEIMAGE, profileImage);
        contentValues.put(DBObjects.DBHelperObjects.USER_STATE, state);

        switch (table) {
            case DBObjects.DBHelperObjects.FOLLOWERS_TABLE_NAME:
                db.insert(DBObjects.DBHelperObjects.FOLLOWERS_TABLE_NAME, null, contentValues);
                Log.d(TAG, "One row inserted into" + DBObjects.DBHelperObjects.FOLLOWERS_TABLE_NAME + ".......");
                break;
            case DBObjects.DBHelperObjects.FOLLOWING_TABLE_NAME:
                db.insert(DBObjects.DBHelperObjects.FOLLOWING_TABLE_NAME, null, contentValues);
                Log.d(TAG, "One row inserted into" + DBObjects.DBHelperObjects.FOLLOWING_TABLE_NAME + ".......");
                break;
            case DBObjects.DBHelperObjects.FRIENDS_TABLE_NAME:
                db.insert(DBObjects.DBHelperObjects.FRIENDS_TABLE_NAME, null, contentValues);
                Log.d(TAG, "One row inserted into" + DBObjects.DBHelperObjects.FRIENDS_TABLE_NAME + ".......");
                break;
            default:
                Log.d(TAG, "Unknown table for insertion.......");
                break;
        }

    }


    public void AddMessage(SQLiteDatabase db, String messageId,
                           String fromId, String toId, String caption,
                           String date, String time, String message,
                           String type, String state, String local_loc, String sync_ed) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBObjects.DBHelperObjects.MESSAGE_ID, messageId);
        contentValues.put(DBObjects.DBHelperObjects.FROM_UID, fromId);
        contentValues.put(DBObjects.DBHelperObjects.TO_UID, toId);
        contentValues.put(DBObjects.DBHelperObjects.CAPTION, caption);
        contentValues.put(DBObjects.DBHelperObjects.DATE, date);
        contentValues.put(DBObjects.DBHelperObjects.TIME, time);
        contentValues.put(DBObjects.DBHelperObjects.MESSAGE, message);
        contentValues.put(DBObjects.DBHelperObjects.TYPE, type);
        contentValues.put(DBObjects.DBHelperObjects.STATE, state);
        contentValues.put(DBObjects.DBHelperObjects.LOCAL_LOCATION, local_loc);
        contentValues.put(DBObjects.DBHelperObjects.SYNCHRONIZED, sync_ed);

        db.insert(DBObjects.DBHelperObjects.MESSAGES_TABLE_NAME, null, contentValues);

        Log.d(TAG, "One row inserted into " + DBObjects.DBHelperObjects.MESSAGES_TABLE_NAME + "......." + DBObjects.DBHelperObjects.MESSAGE_ID);
    }


    public void AddStories(SQLiteDatabase db, String story_id, String caption,
                           String fileUrl, String date, String time,
                           String type, String timeBeg, String timeEnd,
                           String local_loc, String sync_ed) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBObjects.DBHelperObjects.STORY_ID, story_id);
        contentValues.put(DBObjects.DBHelperObjects.CAPTION, caption);
        contentValues.put(DBObjects.DBHelperObjects.IMAGE_URL, fileUrl);
        contentValues.put(DBObjects.DBHelperObjects.DATE, date);
        contentValues.put(DBObjects.DBHelperObjects.TIME, time);
        contentValues.put(DBObjects.DBHelperObjects.TIMEBEG, timeBeg);
        contentValues.put(DBObjects.DBHelperObjects.TIMEEND, timeEnd);
        contentValues.put(DBObjects.DBHelperObjects.TYPE, type);
        contentValues.put(DBObjects.DBHelperObjects.LOCAL_LOCATION, local_loc);
        contentValues.put(DBObjects.DBHelperObjects.SYNCHRONIZED, sync_ed);

        db.insert(DBObjects.DBHelperObjects.MY_STORIES_TABLE_NAME, null, contentValues);

        Log.d(TAG, "One row inserted into" + DBObjects.DBHelperObjects.MY_STORIES_TABLE_NAME + ".......");
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DBObjects.DBHelperObjects.DELETE_USERS_TABLE_QUERY);
        db.execSQL(DBObjects.DBHelperObjects.DELETE_FOLLOWERS_TABLE_QUERY);
        db.execSQL(DBObjects.DBHelperObjects.DELETE_FOLLOWING_TABLE_QUERY);
        db.execSQL(DBObjects.DBHelperObjects.DELETE_FRIENDS_TABLE_QUERY);
        db.execSQL(DBObjects.DBHelperObjects.DELETE_MESSAGES_TABLE_QUERY);
        db.execSQL(DBObjects.DBHelperObjects.DELETE_STORIES_TABLE_QUERY);
        onCreate(db);
    }


    public void deleteTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(DBObjects.DBHelperObjects.DELETE_USERS_TABLE_QUERY);
    }

    public Cursor readFromLocalDB(SQLiteDatabase database, String tableName, String[] projections) {
        return database.query(tableName, projections, null, null, null, null, null);
    }


    public boolean updateMessage(SQLiteDatabase database, String tableName, ContentValues values, String whereClause) {
        database.update(tableName, values, whereClause, null);
        database.close();
        return true;

    }
}
