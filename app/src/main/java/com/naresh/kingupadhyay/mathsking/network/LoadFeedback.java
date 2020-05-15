package com.naresh.kingupadhyay.mathsking.network;

import com.google.firebase.Timestamp;

import java.util.HashMap;
import java.util.Map;

public class LoadFeedback {
    private String userId;
    private String testlevel;
    private String textmsg;
    private Timestamp uploadingTime;

    public LoadFeedback(Map<String,Object> loadFeedback) {
        this.userId = (String)loadFeedback.get("userId");
        this.testlevel = (String)loadFeedback.get("testlevel");
        this.textmsg = (String)loadFeedback.get("textmsg");
        this.uploadingTime = (Timestamp)loadFeedback.get("uploadingTime");
    }

    public Map<String,Object> toMap(){
        Map<String,Object> uploadMap = new HashMap<>();
        uploadMap.put("userId", userId);
        uploadMap.put("testlevel", testlevel);
        uploadMap.put("textmsg", textmsg);
        uploadMap.put("uploadingTime",uploadingTime);
        return uploadMap;
    }


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTestlevel() {
        return testlevel;
    }

    public void setTestlevel(String testlevel) {
        this.testlevel = testlevel;
    }

    public String getTextmsg() {
        return textmsg;
    }

    public void setTextmsg(String textmsg) {
        this.textmsg = textmsg;
    }

    public Timestamp getUploadingTime() {
        return uploadingTime;
    }

    public void setUploadingTime(Timestamp uploadingTime) {
        this.uploadingTime = uploadingTime;
    }

    public LoadFeedback() {
    }
}
