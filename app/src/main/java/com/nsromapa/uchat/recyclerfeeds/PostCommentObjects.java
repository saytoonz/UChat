package com.nsromapa.uchat.recyclerfeeds;

public class PostCommentObjects {
    String commentId;
    String senderImage;
    String senderName;
    String date;
    String time;
    String comment;

    public PostCommentObjects(String commentId, String senderImage, String senderName,
                              String date, String time, String comment) {
        this.commentId = commentId;
        this.senderImage = senderImage;
        this.senderName = senderName;
        this.date = date;
        this.time = time;
        this.comment = comment;
    }


    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public String getSenderImage() {
        return senderImage;
    }

    public void setSenderImage(String senderImage) {
        this.senderImage = senderImage;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
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

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}

