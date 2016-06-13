package com.snydu.icuvideo.icuvideoapp.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.snydu.icuvideo.icuvideoapp.R;
import com.snydu.icuvideo.icuvideoapp.adapter.UserListAdapter;
import com.snydu.icuvideo.icuvideoapp.model.UserNode;

import org.webrtc.webrtcdemo.RtcStartActivity;

import java.util.ArrayList;

public class UserListActivity extends Activity {
    private ArrayList<UserNode> List;
    private ListView UserListlistview;
    private UserListAdapter userListAdapter;
    private Button chatroom_button;
    private Button startVideo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);
        Bundle bundle = this.getIntent().getBundleExtra("key-userlist");
        List = (ArrayList<UserNode>) bundle.getSerializable("userlist");
        System.out.println("输出第一个" + List.get(0).getUserName());
        UserListlistview = (ListView) findViewById(R.id.userList_listview);
        userListAdapter = new UserListAdapter(this,List);
        UserListlistview.setAdapter(userListAdapter);
        chatroom_button = (Button) findViewById(R.id.chatroom_button);
        chatroom_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ChatroomActivity.class);
//                Bundle bundle = new Bundle();// 创建 email 内容
//                bundle.putSerializable("userlist", List);
//                intent.putExtra("key-userlist", bundle);// 封装 email
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(intent);//打开新的activity
            }
        });

        startVideo = (Button) findViewById(R.id.startVideo);
        startVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RtcStartActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(intent);//打开新的activity
            }
        });
    }
}
