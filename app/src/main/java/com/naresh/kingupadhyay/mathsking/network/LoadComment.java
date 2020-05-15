package com.naresh.kingupadhyay.mathsking.network;

import java.util.HashMap;
import java.util.Map;

public class LoadComment {
    private int votes;
    private String text;
    private String userId;

    public LoadComment(){}
    public LoadComment(Map<String,Object> loadCommentMap) {
        this.votes = (int)loadCommentMap.get("votes");
        this.text = (String)loadCommentMap.get("text");
        this.userId = (String)loadCommentMap.get("userId");
    }

    public Map<String,Object> toMap(){
        Map<String,Object> uploadMap = new HashMap<>();
        uploadMap.put("votes",votes);
        uploadMap.put("text",text);
        uploadMap.put("userId",userId);
        return uploadMap;
    }


    public int getVotes() {
        return votes;
    }

    public void setVotes(int votes) {
        this.votes = votes;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
