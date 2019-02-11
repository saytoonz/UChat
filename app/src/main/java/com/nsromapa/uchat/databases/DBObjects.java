package com.nsromapa.uchat.databases;

public final class DBObjects {
    public static abstract class DBHelperObjects {

        public static final String PASS_PHASE = "P@@ssw0rd11223573";

        ///TABLE NAMES
        public static final String USERS_TABLE_NAME = "users";
        public static final String FOLLOWERS_TABLE_NAME = "followers";
        public static final String FOLLOWING_TABLE_NAME = "following";
        public static final String FRIENDS_TABLE_NAME = "friends";
        public static final String MESSAGES_TABLE_NAME = "messages";
        public static final String MY_STORIES_TABLE_NAME = "stories";

        public static final String UID = "uid";
        public static final String INDEX = "index_number";
        public static final String NAME = "name";
        public static final String PROFILEIMAGE = "profileimageurl";
        public static final String WELCOMED = "welcomed";
        public static final String USER_STATE = "userstate";


        public static final String MESSAGE_ID = "message_id";
        public static final String FROM_UID = "from_uid";
        public static final String TO_UID = "to_uid";
        public static final String CAPTION = "caption";
        public static final String DATE = "_date";
        public static final String TIME = "_time";
        public static final String MESSAGE = "message";
        public static final String TYPE = "type";
        public static final String STATE = "state";
        public static final String LOCAL_LOCATION = "local_location";
        public static final String SYNCHRONIZED = "synchronized";

        public static final String STORY_ID = "story_id";
        public static final String IMAGE_URL = "file_url";
        public static final String TIMEBEG = "time_beg";
        public static final String TIMEEND = "time_end";



        public static final String CREATE_USERS_TABLE_QUERY =
                "CREATE TABLE IF NOT EXISTS " + USERS_TABLE_NAME + " " +
                        "(" + UID + " TEXT PRIMARY KEY,"
                        + INDEX + " TEXT,"
                        + NAME + " TEXT,"
                        + PROFILEIMAGE + " TEXT,"
                        + USER_STATE + " TEXT,"
                        + WELCOMED + " TEXT);";

        public static final String DELETE_USERS_TABLE_QUERY=
                "DROP TABLE IF EXISTS "+USERS_TABLE_NAME;



        public static final String CREATE_FOLLOWERS_TABLE_QUERY =
                "CREATE TABLE IF NOT EXISTS " + FOLLOWERS_TABLE_NAME + "" +
                        " (" + UID + " TEXT PRIMARY KEY,"
                        + INDEX + " TEXT,"
                        + NAME + " TEXT,"
                        + PROFILEIMAGE + " TEXT,"
                        + USER_STATE + " TEXT);";

        public static final String DELETE_FOLLOWERS_TABLE_QUERY=
                "DROP TABLE IF EXISTS "+FOLLOWERS_TABLE_NAME;



        public static final String CREATE_FOLLOWING_TABLE_QUERY =
                "CREATE TABLE IF NOT EXISTS " + FOLLOWING_TABLE_NAME + "" +
                        " (" + UID + " TEXT PRIMARY KEY,"
                        + INDEX + " TEXT,"
                        + NAME + " TEXT,"
                        + PROFILEIMAGE + " TEXT,"
                        + USER_STATE + " TEXT);";


        public static final String DELETE_FOLLOWING_TABLE_QUERY=
                "DROP TABLE IF EXISTS "+FOLLOWING_TABLE_NAME;




        public static final String CREATE_FRIENDS_TABLE_QUERY =
                "CREATE TABLE IF NOT EXISTS " + FRIENDS_TABLE_NAME + "" +
                        " (" + UID + " TEXT PRIMARY KEY,"
                        + INDEX + " TEXT,"
                        + NAME + " TEXT,"
                        + PROFILEIMAGE + " TEXT,"
                        + USER_STATE + " TEXT);";

        public static final String DELETE_FRIENDS_TABLE_QUERY=
                "DROP TABLE IF EXISTS "+FRIENDS_TABLE_NAME;



        public static final String CREATE_MESSAGES_TABLE_QUERY =
                "CREATE TABLE IF NOT EXISTS " + MESSAGES_TABLE_NAME + "" +
                        " (" + MESSAGE_ID + " TEXT PRIMARY KEY,"
                        + FROM_UID + " TEXT,"
                        + TO_UID + " TEXT,"
                        + CAPTION + " TEXT,"
                        + DATE + " TEXT,"
                        + TIME + " TEXT,"
                        + MESSAGE + " TEXT,"
                        + TYPE + " TEXT,"
                        + STATE + " TEXT,"
                        + LOCAL_LOCATION + " TEXT,"
                        + SYNCHRONIZED + " TEXT);";

        public static final String DELETE_MESSAGES_TABLE_QUERY=
                "DROP TABLE IF EXISTS "+MESSAGES_TABLE_NAME;



        public static final String CREATE_MY_STORIES_TABLE_QUERY =
                "CREATE TABLE IF NOT EXISTS " + MY_STORIES_TABLE_NAME + "" +
                        " (" + STORY_ID + " TEXT PRIMARY KEY,"
                        + CAPTION + " TEXT,"
                        + IMAGE_URL + " TEXT,"
                        + DATE + " TEXT,"
                        + TIME + " TEXT,"
                        + TIMEBEG + " TEXT,"
                        + TIMEEND + " TEXT,"
                        + TYPE + " TEXT,"
                        + LOCAL_LOCATION + " TEXT,"
                        + SYNCHRONIZED + " TEXT);";

        public static final String DELETE_STORIES_TABLE_QUERY=
                "DROP TABLE IF EXISTS "+MY_STORIES_TABLE_NAME;



    }
}
