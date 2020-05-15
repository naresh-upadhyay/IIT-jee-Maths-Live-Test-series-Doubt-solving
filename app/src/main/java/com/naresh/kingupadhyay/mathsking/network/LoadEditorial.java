package com.naresh.kingupadhyay.mathsking.network;

import java.util.HashMap;
import java.util.Map;

public class LoadEditorial {
    private String userId;
    private String text;
    private String answerImage;

    public LoadEditorial(){}
    public LoadEditorial(Map<String,Object> loadMapEdit) {
        this.userId = (String)loadMapEdit.get("userId");
        this.text = (String)loadMapEdit.get("text");
        this.answerImage = (String)loadMapEdit.get("answerImage");
    }

    public Map<String,Object> toMap(){
        Map<String,Object> uploadMap = new HashMap<>();
        uploadMap.put("userId",userId);
        uploadMap.put("text",text);
        uploadMap.put("answerImage",answerImage);
        return uploadMap;
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

    public String getAnswerImage() {
        return answerImage;
    }

    public void setAnswerImage(String answerImage) {
        this.answerImage = answerImage;
    }
}
