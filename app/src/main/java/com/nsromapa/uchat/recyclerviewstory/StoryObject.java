package com.nsromapa.uchat.recyclerviewstory;



public class StoryObject {
    private String name;
    private String uid;
    private String profileImageUrl;

    public StoryObject(String name, String uid, String profileImageUrl){
        this.name = name;
        this.uid = uid;
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

    public String getProfileImageUrl() {
        return profileImageUrl;
    }
    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }



    @Override
    public boolean equals(Object obj) {

        boolean same = false;
        if(obj != null && obj instanceof StoryObject){
            same = this.uid == ((StoryObject) obj ).uid;
        }
        return same;
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + (this.uid == null ? 0 : this.uid.hashCode());
        return result;
    }



}
