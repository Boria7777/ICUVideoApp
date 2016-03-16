package com.snydu.icuvideo.icuvideoapp.event;

/**
 * Created by Boria on 2016/3/15.
 */
public class SendXmlEvent {
    private String SendinfoXml;
    private int cmdCode;

    public SendXmlEvent(String sendinfoXml, int cmdCode) {
        SendinfoXml = sendinfoXml;
        this.cmdCode = cmdCode;
    }

    public String getSendinfoXml() {
        return SendinfoXml;
    }

    public void setSendinfoXml(String sendinfoXml) {
        SendinfoXml = sendinfoXml;
    }

    public int getCmdCode() {
        return cmdCode;
    }

    public void setCmdCode(int cmdCode) {
        this.cmdCode = cmdCode;
    }
}
