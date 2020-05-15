package com.naresh.kingupadhyay.mathsking.network;

import com.google.firebase.Timestamp;

import java.util.HashMap;
import java.util.Map;

public class LoadExamples {
    private int level;// Hardness of question (o->very easy,1->easy,2->medium,3->Hard)
    private int solvepercent;
    private Timestamp uploadingTime;
    private String userId;
    private String tags;
    private String text;
    private String textImage;
    private boolean option;//true-> objective , false -> subjective questions
    private boolean optionA;
    private boolean optionB;
    private boolean optionC;
    private boolean optionD;

    public LoadExamples(){}

    public LoadExamples(Map<String,Object> loadExamples) {
        this.solvepercent = (int) loadExamples.get("solvepercent");
        this.level = (int)loadExamples.get("level");
        this.uploadingTime = (Timestamp)loadExamples.get("uploadingTime");
        this.userId = (String)loadExamples.get("userId");
        this.tags = (String)loadExamples.get("tags");
        this.text = (String)loadExamples.get("text");
        this.textImage = (String)loadExamples.get("textImage");
        this.option = (boolean)loadExamples.get("option");
        this.optionA = (boolean)loadExamples.get("optionA");
        this.optionB = (boolean)loadExamples.get("optionB");
        this.optionC = (boolean)loadExamples.get("optionC");
        this.optionD = (boolean)loadExamples.get("optionD");
    }

    public Map<String,Object> toMap(){
        Map<String,Object> uploadMap = new HashMap<>();
        uploadMap.put("solvepercent",solvepercent);
        uploadMap.put("level",level);
        uploadMap.put("uploadingTime",uploadingTime);
        uploadMap.put("userId",userId);
        uploadMap.put("tags",tags);
        uploadMap.put("text",text);
        uploadMap.put("textImage",textImage);
        uploadMap.put("option",option);
        uploadMap.put("optionA",optionA);
        uploadMap.put("optionB",optionB);
        uploadMap.put("optionC",optionC);
        uploadMap.put("optionD",optionD);
        return uploadMap;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getSolvepercent() {
        return solvepercent;
    }

    public void setSolvepercent(int solvepercent) {
        this.solvepercent = solvepercent;
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

    public boolean isOption() {
        return option;
    }

    public void setOption(boolean option) {
        this.option = option;
    }

    public boolean isOptionA() {
        return optionA;
    }

    public void setOptionA(boolean optionA) {
        this.optionA = optionA;
    }

    public boolean isOptionB() {
        return optionB;
    }

    public void setOptionB(boolean optionB) {
        this.optionB = optionB;
    }

    public boolean isOptionC() {
        return optionC;
    }

    public void setOptionC(boolean optionC) {
        this.optionC = optionC;
    }

    public boolean isOptionD() {
        return optionD;
    }

    public void setOptionD(boolean optionD) {
        this.optionD = optionD;
    }

}
