package com.snydu.icuvideo.icuvideoapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.snydu.icuvideo.icuvideoapp.R;
import com.snydu.icuvideo.icuvideoapp.model.ChatMessageNode;

import java.util.ArrayList;

/**
 * Created by Boria on 2016/4/27.
 */
public class ChatListAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater inflater;
    public ArrayList<ChatMessageNode> arr;
    private String userid;

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public void setArr(ArrayList<ChatMessageNode> arr) {
        this.arr = arr;
    }

    public ChatListAdapter(Context context, ArrayList<ChatMessageNode> arr) {
        this.context = context;
        this.arr = arr;
        inflater = LayoutInflater.from(context);
    }

    public ChatListAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        arr = new ArrayList<ChatMessageNode>();
    }

    @Override
    public int getCount() {
        return arr.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ChatMessageNode entity = arr.get(position);
        final String SENDER = entity.getSENDER();
        if (SENDER.equals(userid)){
            convertView = inflater.inflate(R.layout.chatting_item_msg_text_right, null);
        }else {
            convertView = inflater.inflate(R.layout.chatting_item_msg_text_left, null);
        }
        TextView tvSendTime = (TextView) convertView.findViewById(R.id.tv_sendtime);
        TextView  tvUserName = (TextView) convertView.findViewById(R.id.tv_username);
        TextView tvContent = (TextView) convertView.findViewById(R.id.tv_chatcontent);
        tvSendTime.setText(entity.getSENDTIME());
        tvUserName.setText(entity.getSENDER_NAME());
        tvContent.setText(entity.getTEXT());

        return convertView;
    }
}
