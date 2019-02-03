package com.nsromapa.uchat.recyclerviewfollow;


public class FollowObject {

    private String name;
    private String uid;
    private String index;
    private String profileImageUrl;


    public FollowObject(String name, String uid, String index, String profileImageUrl) {
        this.name = name;
        this.uid = uid;
        this.index = index;
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


    public String getIndex(){
        return index;
    }
    public void setIndex(String index){
        this.index = index;
    }


    public String getProfileImageUrl(){
        return profileImageUrl;
    }
    public void setProfileImageUrl(String profileImageUrl){
        this.profileImageUrl = profileImageUrl;
    }
}
