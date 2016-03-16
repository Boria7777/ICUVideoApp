package com.snydu.icuvideo.icuvideoapp.event;

/**
 * Created by Boria on 2016/3/15.
 */
public class GetXmlEvent {
    public int getCmdCode() {
        return cmdCode;
    }

    public void setCmdCode(int cmdCode) {
        this.cmdCode = cmdCode;
    }

    public String getGetinfoXml() {
        return GetinfoXml;
    }

    public void setGetinfoXml(String getinfoXml) {
        GetinfoXml = getinfoXml;
    }

    public GetXmlEvent(String getinfoXml, int cmdCode) {
        GetinfoXml = getinfoXml;
        this.cmdCode = cmdCode;
    }

    private String GetinfoXml;
    private int cmdCode;

}
