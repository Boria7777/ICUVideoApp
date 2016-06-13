package com.snydu.icuvideo.icuvideoapp.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.snydu.icuvideo.icuvideoapp.R;
import com.snydu.icuvideo.icuvideoapp.adapter.RoomIlstAdapter;
import com.snydu.icuvideo.icuvideoapp.event.GetXmlEvent;
import com.snydu.icuvideo.icuvideoapp.event.SendXmlEvent;
import com.snydu.icuvideo.icuvideoapp.model.RoomNode;
import com.snydu.icuvideo.icuvideoapp.model.UserNode;
import com.snydu.icuvideo.icuvideoapp.presenter.ChattingRoomPresenter;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.webrtc.webrtcdemo.RtcStartActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ChattingRoomListActivity extends Activity {
    private static ChattingRoomPresenter presenter;

    private RoomIlstAdapter roomAdapter;
    private ArrayList<RoomNode> list;
    private String EnterRoomXML;
    private ArrayList<UserNode> List;
    private ImageButton chatListBackButton;


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onBackPressed() {

        EventBus.getDefault().post(new SendXmlEvent("", 0x6001));//关闭所有链接
        list.clear();
//                onDestroy();;
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences userInfo = getSharedPreferences("userInfo", Activity.MODE_PRIVATE);
        System.out.println("这就是ID" + userInfo.getString("userid", ""));
        EventBus.getDefault().register(this);

        setContentView(R.layout.activity_chatting_room);
        ListView RoomlistView = (ListView) findViewById(R.id.ChattingRoomlistView);
        chatListBackButton = (ImageButton) findViewById(R.id.chatListBackButton);
        chatListBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onBackPressed();
            }
        });
        Bundle bundle = this.getIntent().getBundleExtra("key");
        list = (ArrayList<RoomNode>) bundle.getSerializable("roomlist");
//        ArrayList<group> groups = (ArrayList<group>) b.getSerializable("list");
//        list.add(new RoomNode(2,"room2"));
        roomAdapter = new RoomIlstAdapter(this, list);
        RoomlistView.setAdapter(roomAdapter);
        RoomlistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                TextView roomid = (TextView) view.findViewById(R.id.RoomId_Text);
                enterRoomId = roomid.getText().toString();
                EnterRoomXML = getEnterRoomXML(enterRoomId, "nanjing");
                System.out.println("这是chatroom" + EnterRoomXML);
                EventBus.getDefault().post(new SendXmlEvent(EnterRoomXML, 0x0002));
            }
        });
    }

    public String enterRoomId;

    @Subscribe
    public void onEventMainThread(GetXmlEvent event) throws IOException, DocumentException {
        String msg = "in MainPresenter：" + event.getCmdCode() + event.getGetinfoXml();
        String xmlinfo = event.getGetinfoXml();
//        Log.e("hehehe", msg);
        if (event.getCmdCode() == 0x8002) {
            List = new ArrayList<UserNode>();
            List.clear();
            Document document = DocumentHelper.parseText(xmlinfo);
            Element root = document.getRootElement();
            listNodes(root);
//            System.out.println("输出第一个" + List.get(0).getUserName());
//            ComponentName comp = new ComponentName(this,RtcStartActivity.class);

            Intent intent = new Intent(this, RtcStartActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Bundle bundle = new Bundle();// 创建 email 内容
            bundle.putSerializable("userlist", List);
            bundle.putString("enterRoomId", enterRoomId);
            intent.putExtra("key-userlist", bundle);// 封装 email
            this.startActivity(intent);//打开新的activity
            this.finish();//关闭自身

        }

    }


    public void listNodes(Element node) {

        if (node.getName().equals("USER")) {
            UserNode userNode = new UserNode();
            userNode.setUserName(node.getText());

//            System.out.println("这是节点值" + node.getName() + node.getText());
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
//            System.out.println(userNode.getUserName());
//            System.out.println(userNode.getChat());
//            System.out.println(userNode.getUserId());
            if (userNode.getOnline().equals("ON")) {
                List.add(userNode);
            }

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
