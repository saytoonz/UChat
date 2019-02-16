package com.nsromapa.uchat.usersInfos;

public class MyUserInfo {
    private String name;
    private String indexNo;
    private String profileImage;

    public MyUserInfo() {
    }

    public MyUserInfo(String name, String indexNo, String profileImage) {
        this.name = name;
        this.indexNo = indexNo;
        this.profileImage = profileImage;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIndexNo() {
        return indexNo;
    }

    public void setIndexNo(String indexNo) {
        this.indexNo = indexNo;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }
}
