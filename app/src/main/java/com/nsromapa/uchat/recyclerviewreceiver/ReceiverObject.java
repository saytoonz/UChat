package com.nsromapa.uchat.recyclerviewreceiver;

public class ReceiverObject {

    private Boolean receive;

    private String name;
    private String uid;
    private String index;
    private String profileImageUrl;

    public ReceiverObject(String name, String uid, String index, String profileImageUrl, Boolean receive){
        this.name = name;
        this.uid = uid;
        this.receive = receive;
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

    public Boolean getReceive(){
        return receive;
    }
    public void setReceive(Boolean receive){
        this.receive = receive;
    }

    public String getIndex() {
        return index;
    }
    public void setIndex(String index) {
        this.index = index;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }
    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }
}
