package com.snydu.icuvideo.icuvideoapp.thread;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Boria on 2016/3/29.
 */
public class RecevieThread extends Thread {
    private String Tag = this.getClass().getSimpleName();
    private ReentrantLock lock = null;
    private BufferedInputStream in = null;
    private BufferedOutputStream out = null;
    private boolean isThreadRunning = false;
    private  Handler CmdHandler = null;
    private long isOverTime = System.currentTimeMillis();

    public RecevieThread(BufferedInputStream in, BufferedOutputStream out, Handler cmdHandler) {
        this.in = in;
        this.out = out;
        CmdHandler = cmdHandler;
    }


    @Override
    public void run() {
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
                Message msg = CmdHandler.obtainMessage();
                msg.what = 000001;
                CmdHandler.sendMessage(msg);
//                reConnect();
                break;
            }
        }
        isThreadRunning = false;
        Log.e(Tag, "接收线程中断！");
    }


//    private class SocketReceiverHandler extends android.os.Handler {
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            msgCode = msg.what;//命令code
//            XmlMessage = (String) msg.obj;//xml内容
//            Log.e(Tag, "收到命令" + msgCode);
//            Log.e(Tag, "收到数据" + XmlMessage);
//
////            if (msg.what >= 0) {
////                Toast.makeText(getApplicationContext(), XmlMessage, Toast.LENGTH_SHORT).show();
////            }
//
//            if (msgCode != 0x8000) {
//                EventBus.getDefault().post(new GetXmlEvent(XmlMessage, msgCode));
//            }
////           EventBus.getDefault().post(new GetXmlEvent(XmlMessage,msgCode));
//
//        }
//    }

    public byte[] recBytes(int length) throws Exception {
        int ii = 0;
        byte[] result = new byte[length];
        int hasRec = 0;
        int isRead = 0;
        do {
            Log.e("GetWay lock   ", "锁定前");
            lock.lock();
            Log.e("GetWay lock   ", "锁定后");
            int lenength = in.available();
            if (lenength == 0) {
                ii++;
                Log.e("GetWay lock ii  ", String.valueOf(ii));
            } else {
                ii = 0;
            }
            Log.e("GetWay input   ", String.valueOf(lenength));
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
            if (ii == 10) {
                hasRec = 4;
                length = 0;
            }
            Log.e("GetWay lock   ", "解锁前");
            lock.unlock();
            Log.e("GetWay lock   ", "解锁后");
        }
        while (hasRec < length);
        return result;

    }

    public static int byte2Int(byte[] res) {
        int targets = (res[0] & 0xff) | ((res[1] << 8) & 0xff00) | ((res[2] << 24) >>> 8) | (res[3] << 24);
        return targets;
    }

}
