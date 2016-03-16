package com.snydu.icuvideo.icuvideoapp.presenter;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.snydu.icuvideo.icuvideoapp.MainActivity;
import com.snydu.icuvideo.icuvideoapp.R;
import com.snydu.icuvideo.icuvideoapp.event.GetXmlEvent;
import com.snydu.icuvideo.icuvideoapp.event.SendXmlEvent;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;


/**
 * Created by Boria on 2016/3/9.
 */
public class MainPresenter {
    private MainActivity view;
    private EditText loginIdEdittext;
    private EditText loginPasswordEdittext;
    private Button loginButton;
    private String tag = "Main-Presenter";
    private String LoginXML;
    private int i = 1;

    public void onGetView(MainActivity view) {
        this.view = view;
        EventBus.getDefault().register(this);
        loginIdEdittext = (EditText) view.findViewById(R.id.Login_Id_edittext_mainactivity);
        Button unregrister = (Button) view.findViewById(R.id.unregister);
        loginPasswordEdittext = (EditText) view.findViewById(R.id.Login_password_edittext_mainactivity);
        loginButton = (Button) view.findViewById(R.id.Login_button_mainactivity);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getText();
                EventBus.getDefault().post(new SendXmlEvent(LoginXML, 0x0001));

            }
        });
        unregrister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unregister();
            }
        });

    }

    @Subscribe
    public void onEventMainThread(GetXmlEvent event) throws IOException {
        String msg = "in MainPresenter：" + event.getCmdCode() + event.getGetinfoXml();
        Log.e(tag, msg);
        Toast.makeText(view.getApplicationContext(), event.getGetinfoXml(), Toast.LENGTH_SHORT).show();
    }


    private void getText() {
        String id = loginIdEdittext.getText().toString();
        String password = loginPasswordEdittext.getText().toString();
        LoginXML = getLoginXML(id, password);
    }


    private String getLoginXML(String id, String password) {
        //public static final String R_COMMON_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><MEETING><PLATFORM>2</PLATFORM><USER>USER005</USER><PASSWORD>123456</PASSWORD></MEETING>";
        Document document = DocumentHelper.createDocument();
        //添加节点信息
        Element rootElement = document.addElement("MEETING");
        //这里可以继续添加子节点，也可以指定内容
        //rootElement.setText("这个是module标签的文本信息");
        //Element element = rootElement.addElement("module");

        Element PLATFORMElement = rootElement.addElement("PLATFORM");
        Element USERElement = rootElement.addElement("USER");
        Element PASSWORDElement = rootElement.addElement("PASSWORD");

        PLATFORMElement.setText("2");
        //PLATFORMElement.addAttribute("language", "java");//为节点添加属性值

        USERElement.setText(id);
        //SERElement.addAttribute("language", "c#");

        PASSWORDElement.setText(password);
        //PASSWORDElement.addAttribute("language", "sql server");

        System.out.println(document.asXML());

        LoginXML = document.asXML();
        return LoginXML;
    }

    private void unregister() {
        EventBus.getDefault().unregister(this);
    }

}
