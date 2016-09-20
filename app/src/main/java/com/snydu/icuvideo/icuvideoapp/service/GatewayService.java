package com.snydu.icuvideo.icuvideoapp.service;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.snydu.icuvideo.icuvideoapp.event.GetXmlEvent;
import com.snydu.icuvideo.icuvideoapp.event.SendXmlEvent;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class GatewayService extends Service {
    private static final long maxTimeOut = 60000;
    private static Socket socket1 = null;
    private static Socket socket2 = null;
    private static Context context = null;
    private static SocketReceiverHandler CmdHandler = null;
    public final int aliveSplit = 10000;
    private final String TAG = "GetwayService";

    private SharedPreferences userInfo = null;
    private String userid = null;
    private String userpassword = null;
    private SharedPreferences StmSetEditor = null;
    private String ip = null;
    private int port1;
    private int port2;
    Handler SendAliveHandler = new Handler();

    Runnable AliveRunnable = new Runnable() {

        @Override
        public void run() {
            try {
                EventBus.getDefault().post(new SendXmlEvent("", 0x0000));
                SendAliveHandler.postDelayed(this, aliveSplit);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    private boolean isThreadRunning1 = false;
    private boolean isThreadRunning2 = false;
    private BufferedInputStream in1 = null;
    private BufferedOutputStream out1 = null;
    private BufferedInputStream in2 = null;
    private BufferedReader in22 = null;
    private BufferedOutputStream out2 = null;
    private InetSocketAddress isa1 = null;
    private InetSocketAddress isa2 = null;
    private Thread thread1 = null;
    private Thread thread2 = null;
    private String content = "";
    private Button loginButton;
    private String Tag = this.getClass().getSimpleName();
    private boolean isReConnceting = false;
    private int cmdCode = 0;
    private String InfoXml = null;
    private long isOverTime = System.currentTimeMillis();
    private String XmlMessage = null;
    private int msgCode = 0;
    private Handler OverTimeHandler = new Handler();
    private byte[] b4 = null;
    private byte[] _data = null;
    private ReentrantLock lock = null;


    Runnable FirstSocketRunnable = new Runnable() {

        @Override
        public void run() {
            do {
                Log.e(Tag, "准备socket111 重连！");
                if (!isThreadRunning1) {
                    try {
                        socket1 = new Socket();
                        isa1 = new InetSocketAddress(ip, port1);
                        socket1.connect(isa1, 30000);
                        out1 = new BufferedOutputStream(socket1.getOutputStream());
                        in1 = new BufferedInputStream(socket1.getInputStream());

                        try {
                            if (null != thread1 && thread1.isAlive()) {
                                Log.e(Tag, " 中断 Thread1111 接收线程！");
                                thread1.interrupt();
                            } else {
                                thread1 = new FirstReceiveThread();
                                thread1.start();
                                Log.d(Tag, "起新的接收线程：" + thread1.getName());
                            }
                        } catch (Exception ex) {
                            Log.d(Tag, "老线程关闭失败：" + thread1.getName());
                        }
                        isReConnceting = false;

                        if (!userid.equals("wu")) {
                            //处理
                            sendData(out1, 0x0001, "<?xml version=\"1.0\" encoding=\"UTF-8\"?><MEETING><PLATFORM>2</PLATFORM><USERID>" + userid + "</USERID><PASSWORD>" + userpassword + "</PASSWORD></MEETING>");
                        }

                        break;
                    } catch (Exception ex) {
                        Log.i(Tag, Thread.currentThread().getName() + " InitSocket error " + ex.toString());
                        Log.i(Tag, "Socket 连接失败，稍候重试！");
                    }
                } else {
                    try {
                        if ((thread1 != null) && (thread1.isAlive())) {
                            thread1.interrupt();
                        }
                    } catch (Exception e) {
                        Log.e(Tag, e == null ? "" : e.toString());
                    }
                    Log.e(Tag, "isThreadRunning=" + isThreadRunning1 + " 稍后重连！");
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            } while (true);
        }
    };


    Runnable SecondSocketRunnable = new Runnable() {

        @
        public void run() {
            do {
                Log.e(Tag, "准备socket2222 重连！");
                if (!isThreadRunning2) {
                    try {
                        socket2 = new Socket();
                        isa2 = new InetSocketAddress(ip, port2);
                        socket2.connect(isa2, 30000);
                        out2 = new BufferedOutputStream(socket2.getOutputStream());
                        in2 = new BufferedInputStream(socket2.getInputStream());
                        try {
                            if (null != thread2 && thread2.isAlive()) {
                                Log.e(Tag, " 中断 Thread2222 接收线程！");
                                thread2.interrupt();
                            } else {
                                thread2 = new SecondReceiveThread();
                                thread2.start();
                                Log.d(Tag, "起新的Thread2接收线程：" + thread2.getName());
                            }
                        } catch (Exception ex) {
                            Log.d(Tag, "老线程Thread2关闭失败：" + thread2.getName());
                        }
                        isReConnceting = false;
//                        EventBus.getDefault().post(new SendXmlEvent("", 0x0000));
//                        SendAliveHandler.postDelayed(AliveRunnable, aliveSplit);
//                        OverTimeHandler.postDelayed(OverTimeRunnable, maxTimeOut);
                        sendData(out2, 0x0110, "<?xml version=\"1.0\" encoding=\"UTF-8\"?><MEETING><SESSIONID>" + SESSIONID + "</SESSIONID></MEETING>");

                        break;
                    } catch (Exception ex) {
                        Log.i(Tag, Thread.currentThread().getName() + " InitSocket error " + ex.toString());
                        Log.i(Tag, "Socket 连接失败，稍候重试！");
                    }
                } else {
                    try {
                        if ((thread2 != null) && (thread2.isAlive())) {
                            thread2.interrupt();
                        }
                    } catch (Exception e) {
                        Log.e(Tag, e == null ? "" : e.toString());
                    }
                    Log.e(Tag, "isThreadRunning222=" + isThreadRunning2 + " 稍后重连！");
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            } while (true);
        }
    };


    public GatewayService() {
    }

    public static int byte2Int(byte[] res) {
        int targets = (res[0] & 0xff) | ((res[1] << 8) & 0xff00) | ((res[2] << 24) >>> 8) | (res[3] << 24);
        return targets;
    }

    // int to byte[4]
    public static byte[] int2Byte(int res) {
        byte[] targets = new byte[4];

        targets[0] = (byte) (res & 0xff);
        targets[1] = (byte) ((res >> 8) & 0xff);
        targets[2] = (byte) ((res >> 16) & 0xff);
        targets[3] = (byte) (res >>> 24);
        return targets;
    }

    @Override
    public void onCreate() {
        Log.i(TAG, "onCreate方法被调用!");

        context = GatewayService.this;
        CmdHandler = new SocketReceiverHandler();
        userInfo = context.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        userid = userInfo.getString("userid", "wu");
        userpassword = userInfo.getString("userpassword", "wu");


//        reConnect();
        EventBus.getDefault().register(this);
        super.onCreate();
    }


    @Subscribe
    public void onEventMainThread(SendXmlEvent event) throws IOException {
        String msg = "onEventMainThread-GateWayService收到了消息：" + event.getCmdCode() + event.getSendinfoXml();
        cmdCode = event.getCmdCode();
        InfoXml = event.getSendinfoXml();
        Log.e(TAG, msg);
        if (cmdCode == 0x0110) {
//            sendData(out2, 0x0110, InfoXml);
        } else if (cmdCode == 0x6001) {
            System.out.println("收到断开命令");
            controlToclose = true;
            CloseAllConnect();
        } else if (cmdCode == 0x0001) {

            userInfo = context.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
            userid = userInfo.getString("userid", "wu");
            userpassword = userInfo.getString("userpassword", "wu");
            StmSetEditor = getSharedPreferences("systemSetting", Activity.MODE_PRIVATE);
            ip = StmSetEditor.getString("ipadds", "192.168.1.210");
            port1 = Integer.parseInt(StmSetEditor.getString("portadds", "29999"));
            if (ip.equals("192.168.1.210")) {
                Toast.makeText(getApplicationContext(), "此时IP为默认IP，如有需要请重新设置", Toast.LENGTH_SHORT).show();
            }

            controlToclose = false;
            reConnect();
        } else {
            sendData(out1, cmdCode, InfoXml);
        }

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }


    public byte[] recBytes(BufferedInputStream iiin, int length) throws Exception {
        int ii = 0;
        byte[] result = new byte[length];
        int hasRec = 0;
        int isRead = 0;
        do {
            Log.e("GetWay lock   ", "读取");

            int lenength = iiin.available();
            if (lenength == 0) {
                ii++;
                Log.e("GetWay lock ii  ", String.valueOf(ii));
            } else {
                ii = 0;
            }
            Log.e("GetWay input   ", String.valueOf(lenength));
            byte[] buffer = new byte[length - hasRec];
            isRead = iiin.read(buffer);
            if (isRead != -1) {
                System.arraycopy(buffer, 0, result, hasRec, isRead);
                hasRec += isRead;
                Log.e(Tag, "Socket read   " + isRead);
                if (isRead == 0)
                    Thread.sleep(100);
            } else {
                if (hasRec > 0) {
                    return null;
                }
            }
            if (ii == 10) {
                hasRec = 4;
                length = 0;
            }

        }
        while (hasRec < length);
        return result;

    }

    public int ReadInt(byte[] buf, int start) {
        int b4 = (int) ((buf[start + 3] << 24));
        int b3 = (int) ((buf[start + 2] << 24) >>> 8);
        int b2 = (int) ((buf[start + 1] << 8) & 0xFF00);
        int b1 = (int) buf[start + 0] & 0xff;
        return (int) (b1 | b2 | b3 | b4);
    }

    private void sendHead(BufferedOutputStream ooot, byte[] cmd, byte[] length) throws IOException {
        ooot.write(length);
        Log.d(Tag, "发送字节：" + length.length);
        ooot.write(cmd);
        Log.d(Tag, "发送命令字节：" + cmd.length);
    }

    public void sendData(BufferedOutputStream ooot, int order, String body) throws IOException {


        Log.e(Tag, "发送命令：" + order);
        byte[] cmd = int2Byte(order);
        byte[] _bBody = body.getBytes("utf-8");
        byte[] _length = int2Byte(_bBody.length + 4);
        sendHead(ooot, cmd, _length);
        if (!body.equals("")) {
            ooot.write(_bBody);
            Log.d(Tag, "发送内容字节" + new String(_bBody, "utf-8"));
        } else {
            Log.d(Tag, "无内容");
        }
        ooot.flush();
    }

    public void CloseAllConnect() throws IOException {


        Log.d(Tag, "中断接收线程111 ");
        thread1.interrupt();


        Log.d(Tag, "中断接收线程2222 ");
        thread2.interrupt();
        thdFirst.interrupt();
        thdSecond.interrupt();
        in1.close();
        in2.close();
        out1.close();
        out2.close();
        socket1.close();
        socket2.close();
        isThreadRunning2 = false;
        isThreadRunning1 = false;
//        controlToclose = false;
        SendAliveHandler.removeCallbacks(AliveRunnable);
    }

    public void reConnect() {
        controlToclose = false;
        SendAliveHandler.removeCallbacks(AliveRunnable);
        if (!isReConnceting) {
            Log.e(Tag, "开始重新连接网关！");
            lock = null;
//            sendBroadcast(new Intent(BroadCast.GATEWAY_DISCONNECTED_ACTION));
            isReConnceting = true;
            try {
                in1.close();
                in2.close();
            } catch (Exception ex) {

            }
            try {
                out1.close();
                out2.close();
            } catch (Exception ex) {

            }
            try {
                socket1.close();
                socket2.close();
            } catch (Exception ex) {

            }
            try {
                if ((thread1 != null) && (thread1.isAlive())) {
                    Log.d(Tag, "中断接收线程111 ");
                    thread1.interrupt();
                }
            } catch (Exception e) {

            }
            try {
                if ((thread2 != null) && (thread2.isAlive())) {
                    Log.d(Tag, "中断接收线程2222 ");
                    thread2.interrupt();
                }
            } catch (Exception e) {

            }
            isThreadRunning2 = false;
            isThreadRunning1 = false;
            thdFirst = new Thread(FirstSocketRunnable);
            thdFirst.start();
//            new Thread(FirstSocketRunnable).start();
//            new Thread(SecondSocketRunnable).start();
        } else {
            Log.i(Tag, "reConnect 开始重新连接,已经重连，不再重连");
        }

    }

    public Thread thdFirst = null;

    public class FirstReceiveThread extends Thread {
        @Override
        public void run() {
            while (!thread1.interrupted()) {
                Log.i(Tag, thread1.currentThread().getName() + " 接收网关消息1111");
                isThreadRunning1 = true;
                try {

                    byte[] b4 = recBytes(in1, 4);
                    if (b4 == null) {
                        continue;
                    }
                    int length = byte2Int(b4);
                    Log.d(Tag, "Get protocol length   111:" + length);
                    Message msg = CmdHandler.obtainMessage();
                    byte[] _data = recBytes(in1, length);

                    // get cmd
                    byte[] _bCmd = new byte[4];
                    System.arraycopy(_data, 0, _bCmd, 0, 4);
                    int _iCmd = byte2Int(_bCmd);
                    msg.what = _iCmd;
                    Log.d(Tag, "Get cmd 111:" + _iCmd);

                    if (length > 4) {
                        // 协议长度大于40，说明有内容。
                        byte[] _content = new byte[length - 4];
                        Log.d(Tag, "get protocol's content: 111" + _content.length);
                        System.arraycopy(_data, 4, _content, 0, _content.length);
                        msg.obj = new String(_content, "utf-8");
                        Log.d(Tag, "get protocol content:111" + new String(_content, "utf-8"));
                    }
                    CmdHandler.sendMessage(msg);
                    Thread.sleep(200);
                } catch (Exception ex) {
                    Log.e(Tag, "监听接收时出错1111：" + (String) (null == ex ? "" : ex.getMessage()));
                    if (!controlToclose) {
                        reConnect();
                    }
                    isThreadRunning1 = false;
//                    reConnect();
                    break;
                }
            }
            isThreadRunning1 = false;
            Log.e(Tag, "接收线程中断！");
        }
    }

    public Thread thdSecond = null;

    public class SecondReceiveThread extends Thread {

        @Override
        public void run() {
//            lock = new ReentrantLock();
            while (!thread2.interrupted()) {
                Log.i(Tag, thread2.currentThread().getName() + " 接收网关消息22222");
                isThreadRunning2 = true;
                try {

                    byte[] b4 = recBytes(in2, 4);
                    // Thread.sleep(100);
                    if (b4 == null) {
                        continue;
                    }
                    int length = byte2Int(b4);
                    Log.d(Tag, "Get protocol length:222" + length);
                    Message msg = CmdHandler.obtainMessage();
                    byte[] _data = recBytes(in2, length);

                    // get cmd
                    byte[] _bCmd = new byte[4];
                    System.arraycopy(_data, 0, _bCmd, 0, 4);
                    int _iCmd = byte2Int(_bCmd);
                    msg.what = _iCmd;
                    Log.d(Tag, "Get cmd222 :" + _iCmd);

                    if (length > 4) {
                        // 协议长度大于40，说明有内容。
                        byte[] _content = new byte[length - 4];
                        Log.d(Tag, "get protocol's content:222" + _content.length);
                        System.arraycopy(_data, 4, _content, 0, _content.length);
                        msg.obj = new String(_content, "utf-8");
                        Log.d(Tag, "get protocol content:222" + new String(_content, "utf-8"));
                    }
                    CmdHandler.sendMessage(msg);
                    Thread.sleep(200);
                } catch (Exception ex) {
                    Log.e(Tag, "监听接收时出错2222：" + (String) (null == ex ? "" : ex.getMessage()));
                    isThreadRunning2 = false;
//                isThreadRunning2 = false;
                    if (!controlToclose) {
                        reConnect();
                        System.out.println("2222222启动冲脸");
                    }
                    break;
                }
            }
            isThreadRunning2 = false;
//        isThreadRunning2 = false;
            Log.e(Tag, "接收线程中断！");
        }
    }

    SharedPreferences.Editor eeditor = null;
    private String errorcode = null;
    SharedPreferences.Editor seteditor = null;

    private class SocketReceiverHandler extends android.os.Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            msgCode = msg.what;//命令code
            XmlMessage = (String) msg.obj;//xml内容
            Log.e(Tag, "收到命令" + msgCode);
            Log.e(Tag, "收到数据" + XmlMessage);

            if (msgCode == 0x8001) {
                Document document = null;
                try {
                    document = DocumentHelper.parseText(XmlMessage);
                } catch (DocumentException e) {
                    e.printStackTrace();
                }
                Element root = document.getRootElement();
                errorcode = root.element("ERRORCODE").getText();
                if (errorcode.equals("0")) {
                    SESSIONID = root.element("SESSIONID").getText();
                    SSRC = root.element("SSRC").getText();
                    eeditor = userInfo.edit();
                    eeditor.putString("userSSRC", SSRC);
                    Element hehe = root.element("PORTLIST");
                    port2 = Integer.parseInt(hehe.element("RECEIVE").getText().toString());

//                    System.out.println("V_RTP" + hehe.element("V_RTP").getText());
//                    System.out.println("V_RTCP" + hehe.element("V_RTCP").getText());
//                    System.out.println("A_RTP" + hehe.element("A_RTP").getText());
//                    System.out.println("A_RTCP" + hehe.element("A_RTCP").getText());
                    seteditor = StmSetEditor.edit();
                    seteditor.putString("V_RTP", hehe.element("V_RTP").getText());
                    seteditor.putString("A_RTP", hehe.element("A_RTP").getText());

                    eeditor.commit();
                    seteditor.commit();
                    System.out.println("这是SSRCCCCCCCCCC：" + SSRC);

                    Element element = root.element("ROOMLIST");
                    if (element != null) {
                        listNodes(element);

                    }
                    //启动第二线程
                    thdSecond = new Thread(SecondSocketRunnable);
                    thdSecond.start();
//                new Thread(SecondSocketRunnable).start();
                    //发送消息
                    EventBus.getDefault().post(new SendXmlEvent("<?xml version=\"1.0\" encoding=\"UTF-8\"?><MEETING><SESSIONID>" + SESSIONID + "</SESSIONID></MEETING>", 0x0110));
                    //将消息传回mainPresenter
                    EventBus.getDefault().post(new GetXmlEvent(XmlMessage, msgCode));

                } else {
                    setErrorcodeAction(errorcode);//处理错误代码
                }
            } else if (msgCode == 0x8002) {
                checkErrorcode(XmlMessage);
            } else if (msgCode == 0x8003) {
                checkErrorcode(XmlMessage);
            } else if (msgCode == 0x8004) {
                checkErrorcode(XmlMessage);
            } else if (msgCode == 0x0007) {
                System.out.println("被提出会议室");
                EventBus.getDefault().post(new GetXmlEvent(XmlMessage, msgCode));
            } else if (msgCode == 0x8024) {
                checkErrorcode(XmlMessage);
            } else if (msgCode == 0x8110) {
                System.out.println("收到socket2：");
                //收到回应，开始发送心跳
                SendAliveHandler.postDelayed(AliveRunnable, aliveSplit);

            } else if (msgCode == 0x8000) {

            } else if (msgCode == 0x0008) {
                Toast.makeText(getApplicationContext(), "登录出错，请重新登陆", Toast.LENGTH_SHORT).show();
                System.exit(0);
            } else if (msgCode == 0x0100) {
                System.out.println("退出系统");
                Toast.makeText(getApplicationContext(), "登录出错，请重新登陆", Toast.LENGTH_SHORT).show();
                System.exit(0);
            } else {
                //向接收器发送消息
                System.out.println("向接收器发送消息");
                EventBus.getDefault().post(new GetXmlEvent(XmlMessage, msgCode));
            }
        }

    }

    public void checkErrorcode(String XmlMessage) {
        String ERRORCODE = null;
        Document document = null;
        try {
            document = DocumentHelper.parseText(XmlMessage);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        Element root = document.getRootElement();
        ERRORCODE = root.element("ERRORCODE").getText();

        if (ERRORCODE.equals("0")) {
            EventBus.getDefault().post(new GetXmlEvent(XmlMessage, msgCode));
        } else {
            setErrorcodeAction(ERRORCODE);
        }
    }

    public void setErrorcodeAction(String errorcode) {
        switch (errorcode) {
            case "1":
                Toast.makeText(getApplicationContext(), "消息格式错误", Toast.LENGTH_SHORT).show();
                break;
            case "2":
                Toast.makeText(getApplicationContext(), "非法连接", Toast.LENGTH_SHORT).show();
                break;
            case "3":
                Toast.makeText(getApplicationContext(), "平台类型非法", Toast.LENGTH_SHORT).show();
                break;
            case "4":
                Toast.makeText(getApplicationContext(), "用户名或密码错误", Toast.LENGTH_SHORT).show();
                break;
            case "5":
                Toast.makeText(getApplicationContext(), "禁止登录，探视室已被锁定", Toast.LENGTH_SHORT).show();
                break;
            case "6":
                Toast.makeText(getApplicationContext(), "探视室密码不正确", Toast.LENGTH_SHORT).show();
                break;
            case "7":
                Toast.makeText(getApplicationContext(), "您的权限不足", Toast.LENGTH_SHORT).show();
                break;
            case "8":
                Toast.makeText(getApplicationContext(), "探室不存或转播已结束", Toast.LENGTH_SHORT).show();
                break;
            case "9":
                Toast.makeText(getApplicationContext(), "探视室代码非法", Toast.LENGTH_SHORT).show();
                break;
            case "10":
                Toast.makeText(getApplicationContext(), "指令参数错误", Toast.LENGTH_SHORT).show();
                break;
            case "11":
                Toast.makeText(getApplicationContext(), "信息发送失败", Toast.LENGTH_SHORT).show();
                break;
            case "12":
                Toast.makeText(getApplicationContext(), "获取文件列表失败", Toast.LENGTH_SHORT).show();
                break;
            case "13":
                Toast.makeText(getApplicationContext(), "指令发送失败", Toast.LENGTH_SHORT).show();
                break;
            case "14":
                Toast.makeText(getApplicationContext(), "控制指令发送失败", Toast.LENGTH_SHORT).show();
                break;
            case "15":
                Toast.makeText(getApplicationContext(), "创建文件失败", Toast.LENGTH_SHORT).show();
                break;
            case "16":
                Toast.makeText(getApplicationContext(), "指定文件不存在", Toast.LENGTH_SHORT).show();
                break;
            case "17":
                Toast.makeText(getApplicationContext(), "起始位置大于文件长度，无需下载", Toast.LENGTH_SHORT).show();
                break;
            case "18":
                Toast.makeText(getApplicationContext(), "无效的会话标识", Toast.LENGTH_SHORT).show();
                break;
            case "19":
                Toast.makeText(getApplicationContext(), "指定用户不存在", Toast.LENGTH_SHORT).show();
                break;
            case "20":
                Toast.makeText(getApplicationContext(), "用户名或密码错误", Toast.LENGTH_SHORT).show();
                break;
            case "21":
                Toast.makeText(getApplicationContext(), "申请被拒绝", Toast.LENGTH_SHORT).show();
                break;

        }

    }

    private boolean controlToclose = false;

    public void listNodes(Element node) {
//        System.out.println("当前节点的名称：" + node.getName());
        //首先获取当前节点的所有属性节点
        List<Attribute> list = node.attributes();
        //遍历属性节点
        for (Attribute attribute : list) {
//            System.out.println("属性" + attribute.getName() + ":" + attribute.getValue());
        }
        //如果当前节点内容不为空，则输出
        if (!(node.getTextTrim().equals(""))) {
//            System.out.println("内容" + node.getName() + "：" + node.getText());
            if (node.getName() == "SSRC") {
                SSRC = node.getText();
                System.out.println("这是SSRC" + SSRC);
            }


            if (node.getName() == "SESSIONID") {
                SESSIONID = node.getText();
                thdSecond = new Thread(SecondSocketRunnable);
                thdSecond.start();

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

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            sendData(out2, 0x0100, "");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String SESSIONID = null;
    private String SSRC = null;

}