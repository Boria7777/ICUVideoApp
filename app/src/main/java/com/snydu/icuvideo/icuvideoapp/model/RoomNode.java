package com.snydu.icuvideo.icuvideoapp.model;

import java.io.Serializable;

/**
 * Created by Boria on 2016/4/25.
 */
public class RoomNode implements Serializable {
    public RoomNode(int roomId, String roomName) {
        this.roomId = roomId;
        this.roomName = roomName;
    }

    private int roomId;

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    private String roomName;

}
