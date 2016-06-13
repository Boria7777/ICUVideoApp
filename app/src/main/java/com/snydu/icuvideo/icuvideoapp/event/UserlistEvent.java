package com.snydu.icuvideo.icuvideoapp.event;

import com.snydu.icuvideo.icuvideoapp.model.UserNode;

import java.util.ArrayList;

/**
 * Created by Boria on 2016/5/16.
 */
public class UserlistEvent {
    public ArrayList<UserNode> List;

    public UserlistEvent(ArrayList<UserNode> list) {
        List = list;
    }

    public ArrayList<UserNode> getList() {
        return List;
    }

    public void setList(ArrayList<UserNode> list) {
        List = list;
    }
}
