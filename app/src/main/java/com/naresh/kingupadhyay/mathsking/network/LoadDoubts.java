package com.naresh.kingupadhyay.mathsking.network;

import com.google.firebase.Timestamp;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class LoadDoubts {
    private int votes;
    private Timestamp uploadingTime =  new Timestamp(new Date());
    private String userId;
    private String tags;
    private String text;
    private String textImage;

    public LoadDoubts(){}
    public LoadDoubts(Map<String,Object> loadDoubts) {
        this.votes = (int)loadDoubts.get("votes");
        this.uploadingTime = (Timestamp) loadDoubts.get("uploadingTime");
        this.userId = (String) loadDoubts.get("userId");
        this.tags = (String) loadDoubts.get("tags");
        this.text = (String) loadDoubts.get("text");
        this.textImage = (String) loadDoubts.get("textImage");
    }

    public Map<String,Object> toMap(){
        Map<String,Object> uploadMap = new HashMap<>();
        uploadMap.put("votes",votes);
        uploadMap.put("uploadingTime",uploadingTime);
        uploadMap.put("userId",userId);
        uploadMap.put("tags",tags);
        uploadMap.put("text",text);
        uploadMap.put("textImage",textImage);
        return uploadMap;
    }

    public int getVotes() {
        return votes;
    }

    public void setVotes(int votes) {
        this.votes = votes;
    }

    public Timestamp getUploadingTime() {
        return uploadingTime;
    }

    public void setUploadingTime(Timestamp uploadingTime) {
        this.uploadingTime = uploadingTime;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTextImage() {
        return textImage;
    }

    public void setTextImage(String textImage) {
        this.textImage = textImage;
    }
}
