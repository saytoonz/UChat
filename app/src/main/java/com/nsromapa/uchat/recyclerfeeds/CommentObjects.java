package com.nsromapa.uchat.recyclerfeeds;

public class CommentObjects {
    String commentId;
    String comment;
    String _date;
    String _time;
    String senderId;
    String commnterName;
    String commenterImage;
    String postId;

    public CommentObjects(String commentId, String comment, String _date,
                          String _time, String senderId, String commnterName,
                          String commenterImage, String postId) {
        this.commentId = commentId;
        this.comment = comment;
        this._date = _date;
        this._time = _time;
        this.senderId = senderId;
        this.commnterName = commnterName;
        this.commenterImage = commenterImage;
        this.postId = postId;
    }


    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String get_date() {
        return _date;
    }

    public void set_date(String _date) {
        this._date = _date;
    }

    public String get_time() {
        return _time;
    }

    public void set_time(String _time) {
        this._time = _time;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getCommnterName() {
        return commnterName;
    }

    public void setCommnterName(String commnterName) {
        this.commnterName = commnterName;
    }

    public String getCommenterImage() {
        return commenterImage;
    }

    public void setCommenterImage(String commenterImage) {
        this.commenterImage = commenterImage;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }
}
