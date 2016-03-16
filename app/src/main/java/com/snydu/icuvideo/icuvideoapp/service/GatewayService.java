package com.snydu.icuvideo.icuvideoapp.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Button;

import com.snydu.icuvideo.icuvideoapp.event.GetXmlEvent;
import com.snydu.icuvideo.icuvideoapp.event.SendXmlEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.locks.ReentrantLock;

public class GatewayService extends Service implements Runnable {
    private static final long maxTimeOut = 60000;
    private static Socket socket = null;
    private static Context context = null;
    private static SocketReceiverHandler CmdHandler = null;
    public final int aliveSplit = 10000;
    private final String TAG = "GetwayService";
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
    private boolean isThreadRunning = false;
    private BufferedInputStream in = null;
    private BufferedOutputStream out = null;
    private InetSocketAddress isa = null;
    private Thread thread = null;
    private String content = "";
    private Button loginButton;
    private String Tag = this.getClass().getSimpleName();
    private boolean isReConnceting = false;
    private int cmdCode = 0;
    private String InfoXml = null;
    private long isOverTime = System.currentTimeMillis();
    private String a = null;
    private String b = null;
    private String c = null;
    private String XmlMessage = null;
    private int msgCode = 0;
    private Handler OverTimeHandler = new Handler();
    private byte[] b4 = null;
    private byte[] _data = null;
    private ReentrantLock lock = null;
    private Runnable OverTimeRunnable = new Runnable() {

        @Override
        public void run() {
            Log.d("Gateway OverTime", "检查超时:" + (System.currentTimeMillis() - isOverTime));
            if (System.currentTimeMillis() - isOverTime > maxTimeOut) {
                Log.e("Gateway OverTime", "网关服务器连接超时！重新连接！");
                isThreadRunning = false;
                reConnect();
            } else {
                isOverTime = System.currentTimeMillis();
                OverTimeHandler.removeCallbacks(this);
                OverTimeHandler.postDelayed(this, maxTimeOut);
            }

        }
    };
    Runnable connectSocketRunnable = new Runnable() {

        @Override
        public void run() {
            do {
                Log.e(Tag, "准备socket 重连！");
                if (!isThreadRunning) {
                    try {
                        socket = new Socket();
                        isa = new InetSocketAddress("192.168.1.210", 20000);
                        socket.connect(isa, 30000);
                        out = new BufferedOutputStream(socket.getOutputStream());
                        in = new BufferedInputStream(socket.getInputStream());

                        try {
                            if (null != thread && thread.isAlive()) {
                                Log.e(Tag, " 中断 Thread 接收线程！");
                                thread.interrupt();
                            } else {
                                thread = new Thread(GatewayService.this);
                                thread.start();
                                Log.d(Tag, "起新的接收线程：" + thread.getName());
                            }
                        } catch (Exception ex) {
                            Log.d(Tag, "老线程关闭失败：" + thread.getName());
                            c = ex.toString();
                        }
                        isReConnceting = false;
//                        Message obj = CmdHandler.obtainMessage();
//                        obj.what = Order.DEVICE_LOGIN;
//                        CmdHandler.sendMessage(obj);

                        EventBus.getDefault().post(new SendXmlEvent("", 0x0000));
                        SendAliveHandler.postDelayed(AliveRunnable, aliveSplit);
                        OverTimeHandler.postDelayed(OverTimeRunnable, maxTimeOut);

                        break;
                    } catch (Exception ex) {
                        Log.i(Tag, Thread.currentThread().getName() + " InitSocket error " + ex.toString());
                        Log.i(Tag, "Socket 连接失败，稍候重试！");
                    }
                } else {
                    try {
                        if ((thread != null) && (thread.isAlive())) {
                            thread.interrupt();
                        }
                    } catch (Exception e) {
                        Log.e(Tag, e == null ? "" : e.toString());
                    }
                    Log.e(Tag, "isThreadRunning=" + isThreadRunning + " 稍后重连！");
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
        reConnect();
        EventBus.getDefault().register(this);
        super.onCreate();
    }

    @Subscribe
    public void onEventMainThread(SendXmlEvent event) throws IOException {
        String msg = "onEventMainThread收到了消息：" + event.getCmdCode() + event.getSendinfoXml();
        cmdCode = event.getCmdCode();
        InfoXml = event.getSendinfoXml();
        Log.e(TAG, msg);
        sendData(cmdCode, InfoXml);

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    public byte[] recBytes(int length) throws Exception {
        byte[] result = new byte[length];
        int hasRec = 0;
        int isRead = 0;
        do {
            Log.e("GetWay lock   ", "锁定");
            lock.lock();
            byte[] buffer = new byte[length - hasRec];
            isRead = in.read(buffer);
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
            lock.unlock();
            Log.e("GetWay lock   ", "解锁");
        } while (hasRec < length);
        return result;
    }

    public int ReadInt(byte[] buf, int start) {
        int b4 = (int) ((buf[start + 3] << 24));
        int b3 = (int) ((buf[start + 2] << 24) >>> 8);
        int b2 = (int) ((buf[start + 1] << 8) & 0xFF00);
        int b1 = (int) buf[start + 0] & 0xff;
        return (int) (b1 | b2 | b3 | b4);
    }

    private void sendHead(byte[] cmd, byte[] length) throws IOException {
        out.write(length);
        Log.d(Tag, "发送字节：" + length.length);
        out.write(cmd);
        Log.d(Tag, "发送命令字节：" + cmd.length);
    }

    public void sendData(int order, String body) throws IOException {


        Log.e(Tag, "发送命令：" + order);
        byte[] cmd = int2Byte(order);
        byte[] _bBody = body.getBytes("utf-8");
        byte[] _length = int2Byte(_bBody.length + 4);
        sendHead(cmd, _length);
        if (!body.equals("")) {
            out.write(_bBody);
            Log.d(Tag, "发送内容字节" + new String(_bBody, "utf-8"));
        } else {
            Log.d(Tag, "无内容");
        }

        out.flush();


    }

    public void reConnect() {

        Object obj = new Object();
        OverTimeHandler.removeCallbacks(OverTimeRunnable);
        SendAliveHandler.removeCallbacks(AliveRunnable);
        if (!isReConnceting) {
            Log.e(Tag, "开始重新连接网关！");
            lock = null;
//            sendBroadcast(new Intent(BroadCast.GATEWAY_DISCONNECTED_ACTION));
            isReConnceting = true;
            try {
                in.close();
            } catch (Exception ex) {

            }
            try {
                out.close();
            } catch (Exception ex) {

            }
            try {
                socket.close();
            } catch (Exception ex) {

            }
            try {
                if ((thread != null) && (thread.isAlive())) {
                    Log.d(Tag, "中断接收线程 ");
                    thread.interrupt();
                }
            } catch (Exception e) {

            }
            new Thread(connectSocketRunnable).start();

        } else {
            Log.i(Tag, "reConnect 开始重新连接,已经重连，不再重连");
        }

    }

    @Override
    public void run() {
        // TODO Auto-generated method stub

        lock = new ReentrantLock();
        while (!Thread.interrupted()) {
            Log.i(Tag, Thread.currentThread().getName() + " 接收网关消息");
            isThreadRunning = true;
            try {

                byte[] b4 = recBytes(4);
                // Thread.sleep(100);
                if (b4 == null) {
                    continue;
                }
                int length = byte2Int(b4);
                Log.d(Tag, "Get protocol length:" + length);

                Message msg = CmdHandler.obtainMessage();
                Log.i(Tag, "收到帧时间" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date(System.currentTimeMillis())) + "收到帧长度：" + length);

                byte[] _data = recBytes(length);

                // get cmd
                byte[] _bCmd = new byte[4];
                System.arraycopy(_data, 0, _bCmd, 0, 4);
                int _iCmd = byte2Int(_bCmd);
                msg.what = _iCmd;
                Log.d(Tag, "Get cmd :" + _iCmd);

                if (length > 4) {
                    // 协议长度大于40，说明有内容。
                    byte[] _content = new byte[length - 4];
                    Log.d(Tag, "get protocol's content:" + _content.length);
                    System.arraycopy(_data, 4, _content, 0, _content.length);
                    msg.obj = new String(_content, "utf-8");
                    Log.d(Tag, "get protocol content:" + new String(_content, "utf-8"));
                }
                CmdHandler.sendMessage(msg);
                Thread.sleep(200);
                isOverTime = System.currentTimeMillis();
                Log.d("GatewayService OverTime", "重置超时时间" + isOverTime);
            } catch (Exception ex) {
                Log.e(Tag, "监听接收时出错：" + (String) (null == ex ? "" : ex.getMessage()));
                isThreadRunning = false;
                reConnect();
                break;
            }
//            finally {
//                lock.unlock();//释放锁
//            }
        }
        isThreadRunning = false;
        Log.e(Tag, "接收线程中断！");
    }

    private class SocketReceiverHandler extends android.os.Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            msgCode = msg.what;//命令code
            XmlMessage = (String) msg.obj;//xml内容
            Log.e(Tag, "收到命令" + msgCode);
            Log.e(Tag, "收到数据" + XmlMessage);

//            if (msg.what >= 0) {
//                Toast.makeText(getApplicationContext(), XmlMessage, Toast.LENGTH_SHORT).show();
//            }

            if (msgCode != 0x8000) {
                EventBus.getDefault().post(new GetXmlEvent(XmlMessage, msgCode));
            }
//           EventBus.getDefault().post(new GetXmlEvent(XmlMessage,msgCode));

        }
    }


}