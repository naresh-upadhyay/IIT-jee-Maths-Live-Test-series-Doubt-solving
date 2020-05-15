package com.naresh.kingupadhyay.mathsking.network;

import java.util.HashMap;
import java.util.Map;

public class LoadSubmission {
    private String userId;
    private String text;
    private String textImage;
    private int votes;


    public LoadSubmission(){}

    public LoadSubmission(Map<String,Object> mapSubmission) {
        this.userId = (String)mapSubmission.get("userId");
        this.text = (String)mapSubmission.get("text");
        this.textImage = (String)mapSubmission.get("textImage");
        this.votes = (int)mapSubmission.get("votes");
    }

    public Map<String,Object> toMap(){
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        map.put("text", text);
        map.put("textImage", textImage);
        map.put("votes", votes);
        return map ;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    public int getVotes() {
        return votes;
    }

    public void setVotes(int votes) {
        this.votes = votes;
    }

    public LoadSubmission(String userId, String text, String textImage, int votes) {
        this.userId = userId;
        this.text = text;
        this.textImage = textImage;
        this.votes = votes;
    }

}
