package com.nsromapa.uchat.recyclerchatactivity;

public class ChatsObjects {
    String messageID,from, message, type, caption, date, time, state, local_location, sync_ed;

//    public ChatsObjects(){
//
//    }

    public ChatsObjects(String messageID,
                        String from,
                        String message,
                        String type,
                        String caption,
                        String date,
                        String time,
                        String state,
                        String local_location,
                        String sync_ed) {
        this.messageID = messageID;
        this.from = from;
        this.message = message;
        this.type = type;
        this.caption = caption;
        this.date = date;
        this.time = time;
        this.state = state;
        this.local_location = local_location;
        this.sync_ed = sync_ed;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getMessageID() {
        return messageID;
    }

    public void setMessageID(String messageID) {
        this.messageID = messageID;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getLocal_location() {
        return local_location;
    }

    public void setLocal_location(String local_location) {
        this.local_location = local_location;
    }

    public String getSync_ed() {
        return sync_ed;
    }

    public void setSync_ed(String sync_ed) {
        this.sync_ed = sync_ed;
    }
}
