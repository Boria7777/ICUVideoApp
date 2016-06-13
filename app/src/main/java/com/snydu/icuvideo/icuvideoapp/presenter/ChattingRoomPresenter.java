package com.snydu.icuvideo.icuvideoapp.presenter;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.snydu.icuvideo.icuvideoapp.R;
import com.snydu.icuvideo.icuvideoapp.activity.ChattingRoomListActivity;
import com.snydu.icuvideo.icuvideoapp.activity.UserListActivity;
import com.snydu.icuvideo.icuvideoapp.adapter.RoomIlstAdapter;
import com.snydu.icuvideo.icuvideoapp.event.GetXmlEvent;
import com.snydu.icuvideo.icuvideoapp.event.SendXmlEvent;
import com.snydu.icuvideo.icuvideoapp.model.RoomNode;
import com.snydu.icuvideo.icuvideoapp.model.UserNode;

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
 * Created by Boria on 2016/4/26.
 */
public class ChattingRoomPresenter {
    private ChattingRoomListActivity view;
    private RoomIlstAdapter roomAdapter;
    private ArrayList<RoomNode> list;
    private String EnterRoomXML;
    private ArrayList<UserNode> List;
    private ChattingRoomListActivity vview;
    public void onGetView(ChattingRoomListActivity view) {
        this.view = view;
        EventBus.getDefault().register(this);

        ListView RoomlistView = (ListView) this.view.findViewById(R.id.ChattingRoomlistView);
        Bundle bundle = view.getIntent().getBundleExtra("key");
        list = (ArrayList<RoomNode>) bundle.getSerializable("roomlist");
//        ArrayList<group> groups = (ArrayList<group>) b.getSerializable("list");
//        list.add(new RoomNode(2,"room2"));
        roomAdapter = new RoomIlstAdapter(view.getApplicationContext(), list);
        RoomlistView.setAdapter(roomAdapter);
        RoomlistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                TextView roomid = (TextView) view.findViewById(R.id.RoomId_Text);
                EnterRoomXML = getEnterRoomXML(roomid.getText().toString(), "nanjing");
                System.out.println("这是chtroom" + EnterRoomXML);
                EventBus.getDefault().post(new SendXmlEvent(EnterRoomXML, 0x0002));
            }
        });
    }

    @Subscribe
    public void onEventMainThread(GetXmlEvent event) throws IOException, DocumentException {
        String msg = "in MainPresenter：" + event.getCmdCode() + event.getGetinfoXml();
        String xmlinfo = event.getGetinfoXml();
//        Log.e("hehehe", msg);
        if (event.getCmdCode() == 0x0062) {
            List = new ArrayList<UserNode>();
            Document document = DocumentHelper.parseText(xmlinfo);
            Element root = document.getRootElement();
            listNodes(root);
//            System.out.println("输出第一个" + List.get(0).getUserName());


            Intent intent = new Intent(view, UserListActivity.class);
            Bundle bundle = new Bundle();// 创建 email 内容
            bundle.putSerializable("userlist", List);
            intent.putExtra("key-userlist", bundle);// 封装 email
            view.startActivity(intent);//打开新的activity

        }

    }

    public void listNodes(Element node) {

        if (node.getName().equals("USER")) {
            UserNode userNode = new UserNode();
            userNode.setUserName(node.getText());

            System.out.println("这是节点值" + node.getName() + node.getText());
            List<Attribute> lllist = node.attributes();
            //遍历属性节点
            for (Attribute attribute : lllist) {
                if (attribute.getName().equals("PLATFORM")) {
//                System.out.println("这是PLATFORM" + attribute.getName() + ":" + attribute.getValue());
                    userNode.setPlatfrom(attribute.getValue());
                } else if (attribute.getName().equals("ROLE")) {
                    userNode.setRole(attribute.getValue());
                } else if (attribute.getName().equals("USERID")) {
                    userNode.setUserId(attribute.getValue());
                } else if (attribute.getName().equals("ONLINE")) {
                    userNode.setOnline(attribute.getValue());
                } else if (attribute.getName().equals("CHAT")) {
                    userNode.setChat(attribute.getValue());
                }
            }
            List.add(userNode);
        }
//        }
        //同时迭代当前节点下面的所有子节点
        //使用递归
        Iterator<Element> iterator = node.elementIterator();
        while (iterator.hasNext()) {
            Element e = iterator.next();
            listNodes(e);
        }
    }

    private String getEnterRoomXML(String id, String city) {
        Document document = DocumentHelper.createDocument();
        //添加节点信息
        Element rootElement = document.addElement("MEETING");
        //这里可以继续添加子节点，也可以指定内容
        //rootElement.setText("这个是module标签的文本信息");
        //Element element = rootElement.addElement("module");

        Element ROOMIDElement = rootElement.addElement("ROOMID");
        Element CITYElement = rootElement.addElement("CITY");
        ROOMIDElement.setText(id);
        //SERElement.addAttribute("language", "c#");

        CITYElement.setText(city);
        //PASSWORDElement.addAttribute("language", "sql server");
//        System.out.println(document.asXML());

        EnterRoomXML = document.asXML();
        return EnterRoomXML;
    }

}
