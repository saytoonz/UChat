package com.nsromapa.uchat.recyclerchatactivity;

import java.util.ArrayList;

public class MessagesArrayList {

   static ArrayList<ChatsObjects> chatsObjects = new ArrayList<>();

    public static void setChatsObjects(ArrayList<ChatsObjects> chatsObjectsIn) {
        chatsObjects = chatsObjectsIn;
    }

}
