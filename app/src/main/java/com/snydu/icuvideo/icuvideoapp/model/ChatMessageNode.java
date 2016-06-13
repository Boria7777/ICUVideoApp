package com.snydu.icuvideo.icuvideoapp.model;

/**
 * Created by Boria on 2016/4/27.
 */
public class ChatMessageNode {
    private String SENDER_NAME;
    private String SENDER;
    private String SENDER_ROLE;
    private String SENDTIME;
    private String TYPE;
    private String TEXT;

    public ChatMessageNode(String SENDER_NAME, String SENDER, String SENDER_ROLE, String SENDTIME, String TYPE, String TEXT) {
        this.SENDER_NAME = SENDER_NAME;
        this.SENDER = SENDER;
        this.SENDER_ROLE = SENDER_ROLE;
        this.SENDTIME = SENDTIME;
        this.TYPE = TYPE;
        this.TEXT = TEXT;
    }

    public ChatMessageNode() {
    }

    public String getSENDER_NAME() {
        return SENDER_NAME;
    }

    public void setSENDER_NAME(String SENDER_NAME) {
        this.SENDER_NAME = SENDER_NAME;
    }

    public String getSENDER() {
        return SENDER;
    }

    public void setSENDER(String SENDER) {
        this.SENDER = SENDER;
    }

    public String getSENDER_ROLE() {
        return SENDER_ROLE;
    }

    public void setSENDER_ROLE(String SENDER_ROLE) {
        this.SENDER_ROLE = SENDER_ROLE;
    }

    public String getSENDTIME() {
        return SENDTIME;
    }

    public void setSENDTIME(String SENDTIME) {
        this.SENDTIME = SENDTIME;
    }

    public String getTEXT() {
        return TEXT;
    }

    public void setTEXT(String TEXT) {
        this.TEXT = TEXT;
    }

    public String getTYPE() {
        return TYPE;
    }

    public void setTYPE(String TYPE) {
        this.TYPE = TYPE;
    }
}
