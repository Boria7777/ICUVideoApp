package com.snydu.icuvideo.icuvideoapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.snydu.icuvideo.icuvideoapp.R;
import com.snydu.icuvideo.icuvideoapp.activity.ChatroomActivity;
import com.snydu.icuvideo.icuvideoapp.model.UserNode;

import java.util.ArrayList;

/**
 * Created by Boria on 2016/4/26.
 */
public class UserListAdapter extends BaseAdapter {
    private Context context;
    public ArrayList<UserNode> arr;
    private LayoutInflater inflater;

    public UserListAdapter(Context context, ArrayList<UserNode> arr) {
        this.context = context;
        this.arr = arr;
        inflater = LayoutInflater.from(context);
    }

    public String roomid;
    public String userId;

    public void setUserId(String userid) {
        this.userId = userid;
    }

    public void setRoomId(String roomid) {
        this.roomid = roomid;
    }

    public void refresh(ArrayList<UserNode> list) {
        arr = list;
        notifyDataSetChanged();
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.user_item, null);

            holder = new ViewHolder();
            holder.userNameTextView = (TextView) convertView.findViewById(R.id.user_name_textview);
            holder.Online_textview = (TextView) convertView.findViewById(R.id.Online_textview);
//            holder.UserId_textview = (TextView) convertView.findViewById(R.id.UserId_textview);
            holder.UserIcon = (ImageView) convertView.findViewById(R.id.UserIcon);
            holder.chatButton = (ImageButton) convertView.findViewById(R.id.chatbutton);
            convertView.setTag(holder);   //将Holder存储到convertView中
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
//        final TextView userNameTextView = (TextView) convertView.findViewById(R.id.user_name_textview);
//        final TextView Online_textview = (TextView) convertView.findViewById(R.id.Online_textview);
//        final TextView UserId_textview = (TextView) convertView.findViewById(R.id.UserId_textview);
        holder.userNameTextView.setText(arr.get(position).getUserName());
        if (arr.get(position).getOnline().equals("ON")) {
            holder.Online_textview.setText("在线");
        } else if (arr.get(position).getOnline().equals("OFF")) {
            holder.Online_textview.setText("离线");
        }
        String role = arr.get(position).getRole();
        if (role.equals("0")) {
//            System.out.println("这是管理员");
            holder.UserIcon.setImageResource((R.mipmap.useradapter_adminuser));

        } else if (role.equals("1")) {
//            System.out.println("这是护士站");
            holder.UserIcon.setImageResource((R.mipmap.useradapter_nurse));
        } else if (role.equals("2")) {
//            System.out.println("这是探视员");
            holder.UserIcon.setImageResource((R.mipmap.useradapter_normaluser));
        }
//        holder.UserId_textview.setText(arr.get(position).getUserId());
//        System.out.println("这是适配器里面的chat" + arr.get(position).getChat());
//        System.out.println("这是适配器里面的platform"+arr.get(position).getPlatfrom());
//        设备Platfrom暂时不用
//        System.out.println("这是适配器里面的role" + arr.get(position).getRole());
        final String touser = arr.get(position).getUserId().toString();
        holder.chatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();// 创建 email 内容
                bundle.putString("roomid", roomid);
                bundle.putString("touser", touser);
                Intent intent = new Intent(context, ChatroomActivity.class);
                intent.putExtra("key", bundle);// 封装 email

//                Intent intent = new Intent(getActivity(), ChatroomActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);//打开新的activity
            }
        });
        if (arr.get(position).getUserId().equals(userId)) {
            holder.chatButton.setVisibility(View.INVISIBLE);
        }else{
            holder.chatButton.setVisibility(View.VISIBLE);

        }
        return convertView;
    }

    private static class ViewHolder {
        TextView userNameTextView;
        TextView Online_textview;
        TextView UserId_textview;
        ImageView UserIcon;
        ImageButton chatButton;
    }
}
