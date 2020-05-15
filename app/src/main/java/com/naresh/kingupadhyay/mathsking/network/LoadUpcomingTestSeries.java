package com.naresh.kingupadhyay.mathsking.network;

import com.google.firebase.Timestamp;

import java.util.HashMap;
import java.util.Map;

public class LoadUpcomingTestSeries {
    private String title;
    private int questions;
    private int marks;
    private Timestamp timestamp;
    private int timeSeconds;

    public LoadUpcomingTestSeries(Map<String,Object> loadPreviousTest) {
        this.title = (String)loadPreviousTest.get("title");
        this.questions = (int)loadPreviousTest.get("questions");
        this.marks = (int)loadPreviousTest.get("marks");
        this.timestamp = (Timestamp) loadPreviousTest.get("timestamp");
        this.timeSeconds = (int)loadPreviousTest.get("timeSeconds");
    }

    public Map<String,Object> toMap(){
        Map<String,Object> uploadMap = new HashMap<>();
        uploadMap.put("title",title);
        uploadMap.put("questions",questions);
        uploadMap.put("marks",marks);
        uploadMap.put("timestamp",timestamp);
        uploadMap.put("timeSeconds",timeSeconds);
        return uploadMap;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getQuestions() {
        return questions;
    }

    public void setQuestions(int questions) {
        this.questions = questions;
    }

    public int getMarks() {
        return marks;
    }

    public void setMarks(int marks) {
        this.marks = marks;
    }

    public int getTimeSeconds() {
        return timeSeconds;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public void setTimeSeconds(int timeSeconds) {
        this.timeSeconds = timeSeconds;
    }

    public LoadUpcomingTestSeries() {
    }

}

