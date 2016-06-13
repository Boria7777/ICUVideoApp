package com.snydu.icuvideo.icuvideoapp.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.snydu.icuvideo.icuvideoapp.R;
import com.snydu.icuvideo.icuvideoapp.model.UserNode;

import org.webrtc.webrtcdemo.RtcStartActivity;

import java.util.ArrayList;

public class newActivity extends Activity {
    private ArrayList<UserNode> List;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new);
        Button hhe = (Button) findViewById(R.id.hehebyn);
        hhe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List = new ArrayList<UserNode>();

//            System.out.println("输出第一个" + List.get(0).getUserName());


                Intent intent = new Intent(getApplicationContext(), RtcStartActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Bundle bundle = new Bundle();// 创建 email 内容
                List.add(new UserNode("qweqwe","123123","123123","22222","dasd","2222"));
                bundle.putSerializable("userlist", List);
                intent.putExtra("key-userlist", bundle);// 封装 email
                getApplication().startActivity(intent);//打开新的activity
            }
        });
    }
}
