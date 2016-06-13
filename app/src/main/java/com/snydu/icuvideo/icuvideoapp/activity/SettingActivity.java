package com.snydu.icuvideo.icuvideoapp.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.snydu.icuvideo.icuvideoapp.R;

public class SettingActivity extends Activity {
    private EditText ipaddress;
    private EditText portaddress;
    SharedPreferences.Editor editor = null;
    private ImageButton commitSettingButton;
    private String ipip;
    private String portport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ipaddress = (EditText) findViewById(R.id.IPAddress);
        portaddress = (EditText) findViewById(R.id.portAddress);

        SharedPreferences StmSetEditor = getSharedPreferences("systemSetting", Activity.MODE_PRIVATE);
        editor = StmSetEditor.edit();
        ipip= StmSetEditor.getString("ipadds", "输入IP");
        portport = StmSetEditor.getString("portadds","输入端口");
        ipaddress.setText(ipip);
        portaddress.setText(portport);
//        editor.clear();
//        editor.commit();

//        String id = loginIdEdittext.getText().toString();
//        String password = loginPasswordEdittext.getText().toString();
////        SharedPreferences userInfo = view.getSharedPreferences("userInfo", Activity.MODE_PRIVATE);
////        SharedPreferences.Editor editor = userInfo.edit();
////        editor.clear();
//        editor.putString("userid", id);
//        editor.putString("userpassword",password);
//        editor.commit();
        commitSettingButton = (ImageButton) findViewById(R.id.commitIpButton);
        commitSettingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SetsystemSetting();

            }
        });


    }

    private boolean success = true;

    public void SetsystemSetting() {
        String ipadds = ipaddress.getText().toString();
        String portadds = portaddress.getText().toString();
        success = true;
        if (ipadds.length() < 1) {
            Toast.makeText(this, "请输入ip地址", Toast.LENGTH_SHORT).show();
            success = false;
        }
        if (portadds.length() < 1) {
            Toast.makeText(this, "请输入端口地址", Toast.LENGTH_SHORT).show();
            success = false;
        }
        if (success) {
            editor.clear();
            editor.putString("ipadds", ipadds);
            editor.putString("portadds", portadds);
            editor.commit();
            Toast.makeText(this, "更新成功", Toast.LENGTH_SHORT).show();
            finish();
        }

    }
}
