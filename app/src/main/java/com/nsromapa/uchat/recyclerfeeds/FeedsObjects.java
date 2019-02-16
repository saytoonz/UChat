package com.nsromapa.uchat.recyclerfeeds;

import java.util.List;

public class FeedsObjects {
    String background;
    String date;
    String from;
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
    List<String> comments;

    public FeedsObjects(String background, String date, String from, String hates,
                        String likes, String locLat, String locLong, String postId,
                        String privacy, String size, String state, String style,
                        String text, String time, String type, String url,
                        List<String> likers, List<String> haters) {

        this.background = background;
        this.date = date;
        this.from = from;
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
    }

    public String getBackground() {
        return background;
    }

    public String getDate() {
        return date;
    }

    public String getFrom() {
        return from;
    }

    public String getHates() {
        return hates;
    }

    public String getLikes() {
        return likes;
    }

    public String getLocLat() {
        return locLat;
    }

    public String getLocLong() {
        return locLong;
    }

    public String getPostId() {
        return postId;
    }

    public String getPrivacy() {
        return privacy;
    }

    public String getSize() {
        return size;
    }

    public String getState() {
        return state;
    }

    public String getStyle() {
        return style;
    }

    public String getText() {
        return text;
    }

    public String getTime() {
        return time;
    }

    public String getType() {
        return type;
    }

    public String getUrl() {
        return url;
    }

    public void setBackground(String background) {
        this.background = background;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public void setHates(String hates) {
        this.hates = hates;
    }

    public void setLikes(String likes) {
        this.likes = likes;
    }

    public void setLocLat(String locLat) {
        this.locLat = locLat;
    }

    public void setLocLong(String locLong) {
        this.locLong = locLong;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public void setPrivacy(String privacy) {
        this.privacy = privacy;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setType(String type) {
        this.type = type;
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

    public List<String> getComments() {
        return comments;
    }

    public void setComments(List<String> comments) {
        this.comments = comments;
    }
}
