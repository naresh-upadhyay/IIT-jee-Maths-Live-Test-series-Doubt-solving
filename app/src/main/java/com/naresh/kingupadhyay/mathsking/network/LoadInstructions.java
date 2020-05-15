package com.naresh.kingupadhyay.mathsking.network;

import java.util.Map;

public class LoadInstructions {
    private String textmsg;

    public LoadInstructions(Map<String,Object> loadinstucitons) {
        this.textmsg = (String)loadinstucitons.get("textmsg");
    }
    public LoadInstructions() {
    }

    public String getTextmsg() {
        return textmsg;
    }

    public void setTextmsg(String textmsg) {
        this.textmsg = textmsg;
    }

}
