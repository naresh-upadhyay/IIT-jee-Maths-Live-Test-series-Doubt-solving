package com.naresh.kingupadhyay.mathsking.network;

import java.util.HashMap;
import java.util.Map;

public class LoadShortNotes {
    private int topicNum;
    private String title;
    private String conceptPdfUrl;
    private String time;

    public LoadShortNotes(Map<String,Object> loadConcept) {
        this.topicNum = (int)loadConcept.get("topicNum");
        this.title = (String)loadConcept.get("title");
        this.conceptPdfUrl = (String)loadConcept.get("conceptPdfUrl");
        this.time = (String)loadConcept.get("time");
    }

    public Map<String,Object> toMap(){
        Map<String,Object> uploadMap = new HashMap<>();
        uploadMap.put("topicNum",topicNum);
        uploadMap.put("title",title);
        uploadMap.put("conceptPdfUrl",conceptPdfUrl);
        uploadMap.put("time",time);
        return uploadMap;
    }

    public int getTopicNum() {
        return topicNum;
    }

    public void setTopicNum(int topicNum) {
        this.topicNum = topicNum;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getConceptPdfUrl() {
        return conceptPdfUrl;
    }

    public void setConceptPdfUrl(String conceptPdfUrl) {
        this.conceptPdfUrl = conceptPdfUrl;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public LoadShortNotes(){}


}