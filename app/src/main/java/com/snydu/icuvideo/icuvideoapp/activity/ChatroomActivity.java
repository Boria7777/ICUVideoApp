package com.snydu.icuvideo.icuvideoapp.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import com.snydu.icuvideo.icuvideoapp.R;
import com.snydu.icuvideo.icuvideoapp.adapter.ChatListAdapter;
import com.snydu.icuvideo.icuvideoapp.event.GetXmlEvent;
import com.snydu.icuvideo.icuvideoapp.event.SendXmlEvent;
import com.snydu.icuvideo.icuvideoapp.model.ChatMessageNode;
import com.snydu.icuvideo.icuvideoapp.model.UserNode;

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

public class ChatroomActivity extends Activity {

    public Button sendMessageButton;
    public EditText sendMessageEdittext;
    public String SendMessageXML;
    public ListView MessageListview;
    public ChatListAdapter chatListAdapter;
    public ArrayList<ChatMessageNode> arr;
    public String userid = null;
    private String roomId;
    private String touser;
    private ArrayList<UserNode> List;
    private ImageButton backbbutton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatroom);

        Bundle bundle = this.getIntent().getBundleExtra("key");
//        list = (ArrayList<RoomNode>) bundle.getSerializable("roomlist");
        roomId = bundle.getString("roomid");
        touser = bundle.getString("touser");
        EventBus.getDefault().register(this);
        MessageListview = (ListView) findViewById(R.id.MessageListview);
        sendMessageButton = (Button) findViewById(R.id.SendMessage_button);
        sendMessageEdittext = (EditText) findViewById(R.id.Sendmessage_edittext);
        chatListAdapter = new ChatListAdapter(this);
        SharedPreferences userInfo = getSharedPreferences("userInfo", Activity.MODE_PRIVATE);
//        userInfo.getString("userid","");
        System.out.println("这就是ID" + userInfo.getString("userid", ""));
        userid = userInfo.getString("userid", "");
//        MessageListview.setAdapter(chatListAdapter);
        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = sendMessageEdittext.getText().toString();
                SendMessageXML = getSendMessageXML("", "0", message);
                EventBus.getDefault().post(new SendXmlEvent(SendMessageXML, 0x0024));
                sendMessageEdittext.setFocusable(true);
                sendMessageEdittext.setFocusableInTouchMode(true);
                sendMessageEdittext.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(sendMessageEdittext.getWindowToken(), 0);
                sendMessageEdittext.setText(null);
            }
        });

        backbbutton = (ImageButton) findViewById(R.id.chatBackButton);
        backbbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private String EnterRoomXML = null;

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        System.out.println("聊天---返回---重开聊天室");
        System.out.println("聊天---返回---重开聊天室" + roomId);
        EnterRoomXML = getEnterRoomXML(roomId, "nanjing");
        System.out.println("这是chatroom" + EnterRoomXML);
        EventBus.getDefault().post(new SendXmlEvent(EnterRoomXML, 0x0002));
    }

    @Subscribe
    public void onEventMainThread(GetXmlEvent event) throws IOException, DocumentException {
        String msg = "in ChatRoomActvity：" + event.getCmdCode() + event.getGetinfoXml();
        String xmlinfo = event.getGetinfoXml();
        Log.e("hehehe", msg);
        if (event.getCmdCode() == 0x0025) {

            Document document = DocumentHelper.parseText(xmlinfo);
            Element root = document.getRootElement();
//                listNodes(root);
//                root.element("SESSIONID").getText();
            String SENDER = root.element("SENDER").getText();
            System.out.println("发送者SENDER" + SENDER);
            String SENDER_NAME = root.element("SENDER_NAME").getText();
            System.out.println("发送者SENDER_NAME" + SENDER_NAME);
            String SENDER_ROLE = root.element("SENDER_ROLE").getText();
            System.out.println("发送者SENDER_ROLE" + SENDER_ROLE);
            String SENDTIME = root.element("SENDTIME").getText();
            System.out.println("发送者SENDTIME" + SENDTIME);
            String TYPE = root.element("TYPE").getText();
            System.out.println("发送者TYPE" + TYPE);
            String TEXT = root.element("TEXT").getText();
            System.out.println("发送者TEXT" + TEXT);
            chatListAdapter.arr.add(new ChatMessageNode(SENDER_NAME, SENDER, SENDER_ROLE, SENDTIME, TYPE, TEXT));
            chatListAdapter.setUserid(userid);
            MessageListview.setAdapter(chatListAdapter);
            chatListAdapter.notifyDataSetChanged();
            MessageListview.setSelection(MessageListview.getBottom());
        } else if (event.getCmdCode() == 0x8002) {
            List = new ArrayList<UserNode>();
            Document document = DocumentHelper.parseText(xmlinfo);
            Element root = document.getRootElement();
            listNodes(root);
            Intent intent = new Intent(this, RtcStartActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Bundle bundle = new Bundle();// 创建 email 内容
            bundle.putSerializable("userlist", List);
            bundle.putString("enterRoomId", roomId);
            intent.putExtra("key-userlist", bundle);// 封装 email
            this.startActivity(intent);//打开新的activity
            this.finish();

        }

    }


    private String getSendMessageXML(String TOUSER, String TYPE, String TEXT) {
        Document document = DocumentHelper.createDocument();
        //添加节点信息
        Element rootElement = document.addElement("MEETING");
        //这里可以继续添加子节点，也可以指定内容
        //rootElement.setText("这个是module标签的文本信息");
        //Element element = rootElement.addElement("module");

        Element TOUSERElement = rootElement.addElement("TOUSER");
        Element TYPEElement = rootElement.addElement("TYPE");
        Element TEXTElement = rootElement.addElement("TEXT ");

        TOUSERElement.setText(TOUSER);
        //SERElement.addAttribute("language", "c#");
        TYPEElement.setText(TYPE);
        TEXTElement.setText(TEXT);
        TEXTElement.addAttribute("FONT", "");
        TEXTElement.addAttribute("SIZE", "");
        TEXTElement.addAttribute("COLOR", "");
        //PASSWORDElement.addAttribute("language", "sql server");
//        System.out.println(document.asXML());

        SendMessageXML = document.asXML();
        return SendMessageXML;
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
//            List.add(userNode);
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

}
