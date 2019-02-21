package com.nsromapa.uchat.recyclerfeeds;

import java.util.ArrayList;
import java.util.List;

public class FeedsObjects {
    String background;
    String date;
    String from;
    String posterName;
    String posterImage;
    String hates;
    String likes;
    String locLat;
    String locLong;
    String postId;
    String privacy;
    String size;
    String state;
    String style;
    String text;
    String time;
    String type;
    String url;
    List<String> likers;
    List<String> haters;
    ArrayList<Object> comments;
    String commentString;


    public FeedsObjects(String background, String date, String from,
                        String posterName, String posterImage,
                        String hates, String likes, String locLat,
                        String locLong, String postId, String privacy,
                        String size, String state, String style,
                        String text, String time, String type, String url,
                        List<String> likers, List<String> haters, ArrayList<Object> comments, String commentString) {
        this.background = background;
        this.date = date;
        this.from = from;
        this.posterName = posterName;
        this.posterImage = posterImage;
        this.hates = hates;
        this.likes = likes;
        this.locLat = locLat;
        this.locLong = locLong;
        this.postId = postId;
        this.privacy = privacy;
        this.size = size;
        this.state = state;
        this.style = style;
        this.text = text;
        this.time = time;
        this.type = type;
        this.url = url;
        this.likers = likers;
        this.haters = haters;
        this.comments = comments;
        this.commentString = commentString;
    }

    public String getBackground() {
        return background;
    }

    public void setBackground(String background) {
        this.background = background;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getPosterName() {
        return posterName;
    }

    public void setPosterName(String posterName) {
        this.posterName = posterName;
    }

    public String getPosterImage() {
        return posterImage;
    }

    public void setPosterImage(String posterImage) {
        this.posterImage = posterImage;
    }

    public String getHates() {
        return hates;
    }

    public void setHates(String hates) {
        this.hates = hates;
    }

    public String getLikes() {
        return likes;
    }

    public void setLikes(String likes) {
        this.likes = likes;
    }

    public String getLocLat() {
        return locLat;
    }

    public void setLocLat(String locLat) {
        this.locLat = locLat;
    }

    public String getLocLong() {
        return locLong;
    }

    public void setLocLong(String locLong) {
        this.locLong = locLong;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getPrivacy() {
        return privacy;
    }

    public void setPrivacy(String privacy) {
        this.privacy = privacy;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<String> getLikers() {
        return likers;
    }

    public void setLikers(List<String> likers) {
        this.likers = likers;
    }

    public List<String> getHaters() {
        return haters;
    }

    public void setHaters(List<String> haters) {
        this.haters = haters;
    }

    public ArrayList<Object> getComments() {
        return comments;
    }

    public void setComments(ArrayList<Object> comments) {
        this.comments = comments;
    }

    public String getCommentString() {
        return commentString;
    }

    public void setCommentString(String commentString) {
        this.commentString = commentString;
    }
}
