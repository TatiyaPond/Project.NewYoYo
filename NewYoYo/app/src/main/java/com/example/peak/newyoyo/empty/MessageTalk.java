package com.example.peak.newyoyo.empty;

public class MessageTalk {
    String message, from, time, urlProfile, idMessage;
    boolean seen;

    public MessageTalk() {

    }

    public MessageTalk(String idMessage){
        this.idMessage = idMessage;
    }

    public MessageTalk(String message, String from, String time, String urlProfile, String idMessage, boolean seen) {
        this.message = message;
        this.from = from;
        this.time = time;
        this.urlProfile = urlProfile;
        this.idMessage = idMessage;
        this.seen = seen;
    }

    public String getIdMessage() {
        return idMessage;
    }

    public void setIdMessage(String idMessage) {
        this.idMessage = idMessage;
    }

    public String getUrlProfile() {
        return urlProfile;
    }

    public void setUrlProfile(String urlProfile) {
        this.urlProfile = urlProfile;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

}
