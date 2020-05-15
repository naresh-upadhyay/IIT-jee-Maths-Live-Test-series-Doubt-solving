package com.naresh.kingupadhyay.mathsking.network;

import com.google.firebase.Timestamp;

import java.util.HashMap;
import java.util.Map;

public class UserDetails {
    private String userImage;
    private String name;
    private String uid;
    private int rating;
    private int follow;
    private int following;
    private boolean ppStatus;// public/private status of user(private user nobody knows you)
    private int solved;//no. of questions solved to gave rating

    public UserDetails(Map<String,Object> userDetails) {
        this.userImage = (String)userDetails.get("userImage");
        this.name = (String)userDetails.get("name");
        this.uid = (String)userDetails.get("uid");
        this.rating = (int)userDetails.get("rating");
        this.follow = (int)userDetails.get("follow");
        this.following = (int)userDetails.get("following");
        this.ppStatus = (boolean)userDetails.get("ppStatus");
        this.solved = (int)userDetails.get("solved");
    }

    public Map<String,Object> toMap(){
        Map<String,Object> uploadMap = new HashMap<>();
        uploadMap.put("userImage",userImage);
        uploadMap.put("name",name);
        uploadMap.put("uid",uid);
        uploadMap.put("rating",rating);
        uploadMap.put("follow",follow);
        uploadMap.put("following",following);
        uploadMap.put("ppStatus",ppStatus);
        uploadMap.put("solved",solved);
        return uploadMap;
    }


    public UserDetails(){}

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public int getFollow() {
        return follow;
    }

    public void setFollow(int follow) {
        this.follow = follow;
    }

    public int getFollowing() {
        return following;
    }

    public void setFollowing(int following) {
        this.following = following;
    }

    public boolean isPpStatus() {
        return ppStatus;
    }

    public void setPpStatus(boolean ppStatus) {
        this.ppStatus = ppStatus;
    }

    public int getSolved() {
        return solved;
    }

    public void setSolved(int solved) {
        this.solved = solved;
    }


}
