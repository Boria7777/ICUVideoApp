package com.snydu.icuvideo.icuvideoapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.snydu.icuvideo.icuvideoapp.activity.SettingActivity;
import com.snydu.icuvideo.icuvideoapp.presenter.MainPresenter;
import com.snydu.icuvideo.icuvideoapp.service.GatewayService;

public class MainActivity extends Activity {
    private static MainPresenter presenter;
    private static Intent intent;
    private ImageButton settingButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        intent = new Intent(this, GatewayService.class);
        startService(intent);

        //EventBus.getDefault().register(this);
        if (presenter == null) {
            presenter = new MainPresenter();
            presenter.onGetView(this);
        }
        settingButton = (ImageButton) findViewById(R.id.settingButton);
        settingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SettingActivity.class);

                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                this.startActivity(intent);//打开新的activity
                startActivity(intent);
            }
        });
    }


    @Override
    protected void onDestroy() {
        presenter.GetDestory();
        stopService(intent);
        super.onDestroy();


    }
}
