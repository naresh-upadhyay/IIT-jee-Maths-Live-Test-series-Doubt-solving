package com.naresh.kingupadhyay.mathsking.network;

import com.google.firebase.Timestamp;

import java.util.HashMap;
import java.util.Map;

public class LoadLeaders {
    private String userId;
    private int marks;
    private Timestamp timestamp;

    public LoadLeaders(){}
    public LoadLeaders(Map<String,Object> loadLeaders) {
        this.userId = (String)loadLeaders.get("userId");
        this.marks = (int)loadLeaders.get("marks");
        this.timestamp = (Timestamp)loadLeaders.get("timestamp");
    }

    public Map<String,Object> toMap(){
        Map<String,Object> uploadMap = new HashMap<>();
        uploadMap.put("userId",userId);
        uploadMap.put("marks",marks);
        uploadMap.put("timestamp",timestamp);
        return uploadMap;
    }


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getMarks() {
        return marks;
    }

    public void setMarks(int marks) {
        this.marks = marks;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
