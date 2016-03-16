package com.snydu.icuvideo.icuvideoapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.snydu.icuvideo.icuvideoapp.presenter.MainPresenter;
import com.snydu.icuvideo.icuvideoapp.service.GatewayService;

public class MainActivity extends AppCompatActivity {
    private static MainPresenter presenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startService(new Intent(this, GatewayService.class));

        //EventBus.getDefault().register(this);
        if (presenter == null) {
            presenter = new MainPresenter();
            presenter.onGetView(this);
        }
    }


}
