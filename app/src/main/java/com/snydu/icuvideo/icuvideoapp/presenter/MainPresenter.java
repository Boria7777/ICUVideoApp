package com.snydu.icuvideo.icuvideoapp.presenter;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.snydu.icuvideo.icuvideoapp.MainActivity;
import com.snydu.icuvideo.icuvideoapp.R;
import com.snydu.icuvideo.icuvideoapp.activity.ChattingRoomListActivity;
import com.snydu.icuvideo.icuvideoapp.event.GetXmlEvent;
import com.snydu.icuvideo.icuvideoapp.event.SendXmlEvent;
import com.snydu.icuvideo.icuvideoapp.model.RoomNode;
import com.snydu.icuvideo.icuvideoapp.utils.Order;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * Created by Boria on 2016/3/9.
 */
public class MainPresenter {
    private MainActivity view;
    private EditText loginIdEdittext;
    private EditText loginPasswordEdittext;
    private ImageButton loginButton;
    private String tag = "Main-Presenter";
    private String LoginXML;
    private int i = 1;
    private String SESSIONID = null;
    private ArrayList<RoomNode> List;
    SharedPreferences.Editor editor = null;

    public void GetDestory() {
        System.out.println("退出系统");
        EventBus.getDefault().post(new SendXmlEvent("", 0x0100));
    }

    public void onGetView(final MainActivity view) {
        this.view = view;
        EventBus.getDefault().register(this);
        SharedPreferences userInfo = view.getSharedPreferences("userInfo", Activity.MODE_PRIVATE);
        editor = userInfo.edit();
        editor.clear();
        editor.commit();

        loginIdEdittext = (EditText) view.findViewById(R.id.Login_Id_edittext_mainactivity);
        loginPasswordEdittext = (EditText) view.findViewById(R.id.Login_password_edittext_mainactivity);
        loginButton = (ImageButton) view.findViewById(R.id.Login_button_mainactivity);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getText();
                EventBus.getDefault().post(new SendXmlEvent(LoginXML, 0x0001));
                //发送登录消息
            }
        });

        List = new ArrayList();
//        unregrister.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//              unregister();
//               RoomNode hehe = (RoomNode) List.get(1);
//                System.out.println("这是hehehe的名字" + (List.get(0)).getRoomName());
//                System.out.println("这是hehehe的值" + (List.get(0)).getRoomId());
//            }
//
//        });

    }

    @Subscribe
    public void onEventMainThread(GetXmlEvent event) throws IOException, DocumentException {
        String msg = "in MainPresenter：" + event.getCmdCode() + event.getGetinfoXml();
        String xmlinfo = event.getGetinfoXml();
//        Log.e(tag, msg);
        if (event.getCmdCode() == Order.R_DEVICE_LOGIN) {
            List.clear();
            Document document = DocumentHelper.parseText(xmlinfo);
            Element root = document.getRootElement();
            Element element = root.element("ROOMLIST");
            if (element != null) {
                listNodes(element);
            }
            Intent intent = new Intent(view, ChattingRoomListActivity.class);
            Bundle bundle = new Bundle();// 创建 email 内容
            bundle.putSerializable("roomlist", List);
            intent.putExtra("key", bundle);// 封装 email
            view.startActivity(intent);//打开新的activity
            unregister();

        }

    }

    public void listNodes(Element node) {

        if (node.getName().equals("ROOM")) {
//            System.out.println("这是room的房间名字" + node.getText());
            List<Attribute> lllist = node.attributes();
            //遍历属性节点
            for (Attribute attribute : lllist) {
//                System.out.println("属性" + attribute.getName() + ":" + attribute.getValue());
//                System.out.println("这是room的属性名" + attribute.getName());
//                System.out.println("这是room的ID号码" + attribute.getValue());
                List.add(new RoomNode(Integer.valueOf(attribute.getValue()).intValue(), node.getText()));
            }
        }
        //同时迭代当前节点下面的所有子节点
        //使用递归
        Iterator<Element> iterator = node.elementIterator();
        while (iterator.hasNext()) {
            Element e = iterator.next();
            listNodes(e);
        }
    }


    private void getText() {
        String id = loginIdEdittext.getText().toString();
        String password = loginPasswordEdittext.getText().toString();
//        SharedPreferences userInfo = view.getSharedPreferences("userInfo", Activity.MODE_PRIVATE);
//        SharedPreferences.Editor editor = userInfo.edit();
//        editor.clear();
        editor.putString("userid", id);
        editor.putString("userpassword", password);
        editor.commit();



//        String hehe = userInfo.getString(id, "");
//        System.out.println("这就是ID"+hehe);
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
        Element USERElement = rootElement.addElement("USERID");
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
//        EventBus.getDefault().unregister(this);
    }

}
