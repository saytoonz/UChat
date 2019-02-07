package com.nsromapa.uchat.recyclerviewchatfragment;

public class ChatFragmentObject {

    private String name;
    private String uid;
    private String state;
    private String profileImageUrl;

    public ChatFragmentObject(String name, String uid, String state, String profileImageUrl){
        this.name = name;
        this.uid = uid;
        this.state = state;
        this.profileImageUrl = profileImageUrl;
    }

    public String getUid(){
        return uid;
    }
    public void setUid(String uid){
        this.uid = uid;
    }

    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name = name;
    }


    public String getState() {
        return state;
    }
    public void setState(String state) {
        this.state = state;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }
    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }
}
