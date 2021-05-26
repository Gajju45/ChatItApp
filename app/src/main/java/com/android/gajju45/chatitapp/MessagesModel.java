package com.android.gajju45.chatitapp;

public class MessagesModel {
    String message;
    String senderId;
    long timeStamp;
    String currentTime;

    public MessagesModel() {
    }



    public MessagesModel(String message, String senderId, long timeStamp, String currentTime) {
        this.message = message;
        this.senderId = senderId;
        this.timeStamp = timeStamp;
        this.currentTime = currentTime;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(String currentTime) {
        this.currentTime = currentTime;
    }
}
