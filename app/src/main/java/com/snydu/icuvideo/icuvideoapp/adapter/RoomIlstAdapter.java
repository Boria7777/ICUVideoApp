package com.snydu.icuvideo.icuvideoapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.snydu.icuvideo.icuvideoapp.R;
import com.snydu.icuvideo.icuvideoapp.model.RoomNode;

import java.util.ArrayList;

/**
 * Created by Boria on 2016/4/25.
 */
public class RoomIlstAdapter extends BaseAdapter {
    public ArrayList<RoomNode> getArr() {
        return arr;
    }

    public void setArr(ArrayList<RoomNode> arr) {
        this.arr = arr;
    }

    public RoomIlstAdapter(Context context, ArrayList<RoomNode> arr) {
        this.context = context;
        this.arr = arr;
        inflater = LayoutInflater.from(context);
    }

    private Context context;
    public ArrayList<RoomNode> arr;
    private LayoutInflater inflater;

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
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.chatroom_item, null);
        }
        final TextView RoomNameTextview = (TextView) convertView.findViewById(R.id.RoomName_text);
        RoomNameTextview.setText(arr.get(position).getRoomName());
        final TextView RoomidTextview = (TextView) convertView.findViewById(R.id.RoomId_Text);
        RoomidTextview.setText(String.valueOf(arr.get(position).getRoomId()));
        return convertView;
    }
}
