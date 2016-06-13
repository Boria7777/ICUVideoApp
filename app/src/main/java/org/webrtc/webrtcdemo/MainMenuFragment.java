package org.webrtc.webrtcdemo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.DrawableRes;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.snydu.icuvideo.icuvideoapp.R;
import com.snydu.icuvideo.icuvideoapp.activity.ChatroomActivity;
import com.snydu.icuvideo.icuvideoapp.activity.ChattingRoomListActivity;
import com.snydu.icuvideo.icuvideoapp.event.GetXmlEvent;
import com.snydu.icuvideo.icuvideoapp.event.SendXmlEvent;
import com.snydu.icuvideo.icuvideoapp.model.RoomNode;

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
 * Created by Boria on 2016/5/5.
 */
public class MainMenuFragment extends Fragment implements MediaEngineObserver {

    private String TAG;
    private MenuStateProvider stateProvider;
    private ImageButton chatroom_button;
    private ImageButton btStartStopCall;
    private ImageButton closeButton;
    private ImageButton nursepoint;
    private TextView tvStats;

    // Remote and local stream displays.
    private LinearLayout llRemoteSurface;
    private LinearLayout Vlayout1;
    private LinearLayout Vlayout2;
    private LinearLayout Vlayout3;
    private LinearLayout Vlayout4;
    public EditText hehe;
    private SharedPreferences userInfo = null;
    private SharedPreferences StmSetEditor = null;
    private int ssrc;
    private String roomId;


    @Override
    public void onStop() {
        super.onStop();
        handler.removeCallbacks(updateThread);
        if (isSendVideo) {
            stopAll();
//            engine.dispose();
        }
//        stopAll();
//        engine.dispose();
        engine.dispose();
        getActivity().finish();
        System.out.println("关闭所有MenuFragment");
    }

    //    private LinearLayout llLocalSurface;
//    private SurfaceView[] surf;
//    private List<SurfaceView> listViews=new ArrayList<SurfaceView>();
    private int VRTP;
    private int ATRP;
    private String VideoIP;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.mainmenu, container, false);
        EventBus.getDefault().register(this);
        userInfo = getActivity().getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        StmSetEditor = getActivity().getSharedPreferences("systemSetting", Activity.MODE_PRIVATE);
        VRTP = Integer.parseInt(StmSetEditor.getString("V_RTP", "20002"));
        ATRP = Integer.parseInt(StmSetEditor.getString("A_RTP", "20004"));
        VideoIP = StmSetEditor.getString("ipadds", "192.168.1.210");
        String src = userInfo.getString("userSSRC", "null");
        ssrc = Integer.parseInt(src);
//        System.out.println("这是获取的这是获取的这是获取的这是获取的这是获取的SSRC" + ssrc);
        TAG = "WebRTC";
        Bundle bundle = getArguments();
        roomId = bundle.getString("enterRoomId");
        System.out.println("这是获取的这是获取的这是获取的roomid" + roomId);
        llRemoteSurface = (LinearLayout) v.findViewById(R.id.llRemoteView);
        Vlayout1 = (LinearLayout) v.findViewById(R.id.VideoLayout1);
        Vlayout2 = (LinearLayout) v.findViewById(R.id.VideoLayout2);
        Vlayout3 = (LinearLayout) v.findViewById(R.id.VideoLayout3);
        Vlayout4 = (LinearLayout) v.findViewById(R.id.VideoLayout4);


        engine = getEngine();
        engine.initchannel(VideoIP, VRTP, ATRP);

        chatroom_button = (ImageButton) v.findViewById(R.id.chatroom_button);
        chatroom_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                stopAll();
//                engine.dispose();
                Bundle bundle = new Bundle();// 创建 email 内容
                bundle.putString("roomid", roomId);
                bundle.putString("touser", "");
                Intent intent = new Intent(getActivity(), ChatroomActivity.class);
                intent.putExtra("key", bundle);// 封装 email

//                Intent intent = new Intent(getActivity(), ChatroomActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getActivity().startActivity(intent);//打开新的activity

                getActivity().finish();
            }
        });

        btStartStopCall = (ImageButton) v.findViewById(R.id.btStartStopCall);

        btStartStopCall.setOnClickListener(new View.OnClickListener() {
            public void onClick(View button) {
                toggleStart();
            }
        });
        closeButton = (ImageButton) v.findViewById(R.id.closerrombutton);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                stopAll();
                EventBus.getDefault().post(new SendXmlEvent("", 0x0063));

            }
        });
        nursepoint = (ImageButton) v.findViewById(R.id.nursepoint);
        nursepoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final EditText texta = new EditText(getActivity());
                new AlertDialog.Builder(getActivity()).setTitle("请输入视频室密码").setView(texta).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String passwprd = texta.getText().toString();
                        System.out.println("这是获取的文本" + passwprd);
                        if (isnursePoint) {
                            EventBus.getDefault().post(new SendXmlEvent("<?xml version=\"1.0\" encoding=\"UTF-8\"?><MEETING><ACTION>-</ACTION><PASSWORD>" + passwprd + "</PASSWORD></MEETING>", 0x0003));
                        } else {
                            EventBus.getDefault().post(new SendXmlEvent("<?xml version=\"1.0\" encoding=\"UTF-8\"?><MEETING><ACTION>+</ACTION><PASSWORD>" + passwprd + "</PASSWORD></MEETING>", 0x0003));
                        }

                    }
                }).setNegativeButton("取消", null).show();
            }
        });

        return v;
    }


    @Override
    public void onResume() {
        super.onResume();

        handler.postDelayed(updateThread, 3000);
    }


    private boolean isnursePoint = false;

    //创建Handler对象
    Handler handler = new Handler();
    //新建一个线程对象
    Runnable updateThread = new Runnable() {
        //将要执行的操作写在线程对象的run方法当中
        public void run() {
            System.out.println("updateThread");

            toggleStart();
        }
    };

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception.
        try {
            stateProvider = (MenuStateProvider) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity +
                    " must implement MenuStateProvider");
        }
    }

    // tvStats need to be updated on the UI thread.
    public void newStats(final String stats) {
        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                tvStats.setText(stats);
            }
        });
    }

    private MediaEngine getEngine() {
        return stateProvider.getEngine();
    }

//    private void setViews() {
//        SurfaceView remoteSurfaceView = getEngine().getRemoteSurfaceView();
//        if (remoteSurfaceView != null) {
//            llRemoteSurface.addView(remoteSurfaceView);
//        }
//        SurfaceView svLocal = getEngine().getLocalSurfaceView();
//        if (svLocal != null) {
//            llLocalSurface.addView(svLocal);
//        }
//    }

//    private void clearViews() {
//        SurfaceView remoteSurfaceView = getEngine().getRemoteSurfaceView();
//        if (remoteSurfaceView != null) {
//            llRemoteSurface.removeView(remoteSurfaceView);
//        }
//        SurfaceView svLocal = getEngine().getLocalSurfaceView();
//        if (svLocal != null) {
//            llLocalSurface.removeView(svLocal);
//        }
//    }

    private void enableStats(Button btStats, boolean enable) {
        if (enable) {
            getEngine().setObserver(this);
        } else {
            getEngine().setObserver(null);
            // Clear old stats text by posting empty stats.
            newStats("");
        }
        btStats.setText(enable ? R.string.statsOff : R.string.statsOn);
    }

    public void toggleStart() {
        if (isSendVideo) {
            stopAll();
        } else {
            startCall();

        }
//        btStartStopCall.setText(getEngine().isRunning() ?
//                R.string.stopCall :
//                R.string.startCall);
    }


    public void stopAll() {

        engine = getEngine();
//        engine.setIncomingVieRtpDump(false, outname);
        StopRecAllVideo();
        engine.stopsend(ssrc);
        engine.stopsend_audio(ssrc);
        Vlayout1.removeAllViews();
        Vlayout1.addView(getView(R.mipmap.videoicon, xx, yy));
        isSendVideo = false;
//        engine.dispose();


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        engine.dispose();
        EventBus.getDefault().unregister(this);
    }

    public int xx;
    public int yy;
    public RelativeLayout.LayoutParams params = null;
    public MediaEngine engine = null;
    private boolean isSendVideo = false;
    private boolean isSendAudio = false;
    private String outname = null;

    public void startCall() {
        outname = String.format("/vie_%d.rtp", System.currentTimeMillis());
        engine = getEngine();

        engine.setIncomingVieRtpDump(true, outname);//
//        开启记录
//        engine.initchannel("192.168.1.210", 20002, 20004);
        if (params == null) {
            xx = Vlayout1.getWidth();
            yy = Vlayout1.getHeight();
            Log.i("这是X", String.valueOf(xx));
            Log.i("这是Y", String.valueOf(yy));
            params = new RelativeLayout.LayoutParams(xx, yy);
        }
//        xx = Vlayout1.getWidth();
//        yy = Vlayout1.getHeight();
//        Log.i("这是X", String.valueOf(xx));
//        Log.i("这是Y", String.valueOf(yy));
//        params = new RelativeLayout.LayoutParams(xx, yy);

        SurfaceView kView = engine.startsend(ssrc);
        Vlayout1.removeAllViews();
        Vlayout1.addView(kView, params);
        engine.startsend_audio(ssrc);
        isSendVideo = true;
        isSendAudio = true;
        EventBus.getDefault().post(new SendXmlEvent("<?xml version=\"1.0\" encoding=\"UTF-8\"?><MEETING><SWITCH>ON</SWITCH></MEETING>", 0x0030));
        EventBus.getDefault().post(new SendXmlEvent("<?xml version=\"1.0\" encoding=\"UTF-8\"?><MEETING><SWITCH>ON</SWITCH></MEETING>", 0x0041));
        receiveVideo(viewList);


    }

    public View getView(@DrawableRes int resId, int xx, int yy) {
//        ImageView imageView = new ImageView(getActivity().getApplicationContext());
//        ImageView ada = new ImageView(getActivity().getApplicationContext()));
        // 设置当前图像的图像（position为当前图像列表的位置）

        ImageView mImageView = new ImageView(getActivity().getApplicationContext());
        mImageView.setImageResource(resId);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(xx, yy);
        params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        mImageView.setLayoutParams(params);


//        imageView.setImageResource(R.drawable.chat_error);
//        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
//        imageView.setLayoutParams(new Gallery.LayoutParams(163, 106));
        // 设置Gallery组件的背景风格
//        imageView.setBackgroundResource(mGalleryItemBackground);
        return mImageView;
    }

    private ArrayList<RoomNode> chatroomlist;

    @Subscribe
    public void onEventMainThread(GetXmlEvent event) throws IOException, DocumentException {
        String msg = "in MainMenuFragment：" + event.getCmdCode() + event.getGetinfoXml();
        String xmlinfo = event.getGetinfoXml();
//       Log.e("这是userlistfragment里的接", msg);
        if (event.getCmdCode() == 0x0080) {
            System.out.println("获取其他SSRC");
            if (isSendVideo) {
                StopRecAllVideo();
            }
            Document document = DocumentHelper.parseText(xmlinfo);
            Element root = document.getRootElement();
            listADDNodes(root);
            int size = viewList.size();
            for (int i = 0; i < size; i++) {
                System.out.println("这是添加添加添加添加添加添加后list的内容" + viewList.get(i).toString());
            }
            if (isSendVideo) {
                receiveVideo(viewList);
            }
            EventBus.getDefault().post(new SendXmlEvent("", 0x8080));
        } else if (event.getCmdCode() == 0x0081) {
            System.out.println("移除其他SSRC");
            if (isSendVideo) {
                StopRecAllVideo();
            }
            Document document = DocumentHelper.parseText(xmlinfo);
            Element root = document.getRootElement();
            listMOVENodes(root);
            int size = viewList.size();
            for (int i = 0; i < size; i++) {
                System.out.println("这是移除移除移除移除移除移除后list的内容" + viewList.get(i).toString());
            }
            if (isSendVideo) {
                receiveVideo(viewList);
            }
            EventBus.getDefault().post(new SendXmlEvent("", 0x8081));
        } else if (event.getCmdCode() == 0x0007) {
            //被提出会议室
            EventBus.getDefault().post(new SendXmlEvent("", 0x0063));

        } else if (event.getCmdCode() == 0x8063) {
            System.out.println("获取新的chatroomList" + xmlinfo);
            chatRoomList = new ArrayList();
            Document document = DocumentHelper.parseText(xmlinfo);
            Element root = document.getRootElement();
            Element element = root.element("ROOMLIST");
            if (element != null) {
                ChatlistNodes(element);
            }
            Intent intent = new Intent(getActivity(), ChattingRoomListActivity.class);
            Bundle bundle = new Bundle();// 创建 email 内容
            bundle.putSerializable("roomlist", chatRoomList);
            intent.putExtra("key", bundle);// 封装 email
            getActivity().startActivity(intent);//打开新的activity
//            stopAll();
//            engine.dispose();
            EventBus.getDefault().post(new SendXmlEvent("", 0x0012));
            getActivity().finish();
        } else if (event.getCmdCode() == 0x0032) {

            Document document = DocumentHelper.parseText(xmlinfo);
            Element root = document.getRootElement();
            Element element = root.element("SWITCH");
            System.out.println("这是收到的命令" + element.getText().toString());
            if (element.getText().toString().equals("ON")) {
                if (!isSendVideo) {
                    engine = getEngine();
                    SurfaceView kView = engine.startsend(ssrc);
                    Vlayout1.removeAllViews();
                    Vlayout1.addView(kView, params);
                    isSendVideo = true;
                }
            } else if (element.getText().toString().equals("OFF")) {
                if (isSendVideo) {
                    engine = getEngine();
                    engine.stopsend(ssrc);
                    Vlayout1.removeAllViews();
                    Vlayout1.addView(getView(R.mipmap.videoicon, xx, yy));
                    isSendVideo = false;
                }
            }
        } else if (event.getCmdCode() == 0x0043) {
            Document document = DocumentHelper.parseText(xmlinfo);
            Element root = document.getRootElement();
            Element element = root.element("SWITCH");
            if (element.getText().toString().equals("ON")) {
                if (!isSendAudio) {
                    engine = getEngine();
                    engine.startsend_audio(ssrc);
                    isSendAudio = true;
                }
            } else if (element.getText().toString().equals("OFF")) {
                if (isSendAudio) {
                    engine = getEngine();
                    engine.stopsend_audio(ssrc);
                    isSendAudio = false;
                }
            }
        } else if (event.getCmdCode() == 0x8003) {
            Document document = DocumentHelper.parseText(xmlinfo);
            Element root = document.getRootElement();
            Element element = root.element("ACTION");
            if (element.getText().toString().equals("+")) {
                isnursePoint = true;
                System.out.println("现在是护士站");
                Toast.makeText(getActivity(), "成为护士站", Toast.LENGTH_SHORT).show();
            } else if (element.getText().toString().equals("-")) {
                System.out.println("现在取消护士站");
                Toast.makeText(getActivity(), "取消护士站", Toast.LENGTH_SHORT).show();
                isnursePoint = false;
            }
        }

    }

    private ArrayList<RoomNode> chatRoomList;

    public void ChatlistNodes(Element node) {

        if (node.getName().equals("ROOM")) {
//            System.out.println("这是room的房间名字" + node.getText());
            List<Attribute> lllist = node.attributes();
            //遍历属性节点
            for (Attribute attribute : lllist) {
                chatRoomList.add(new RoomNode(Integer.valueOf(attribute.getValue()).intValue(), node.getText()));
            }
        }
        //同时迭代当前节点下面的所有子节点
        //使用递归
        Iterator<Element> iterator = node.elementIterator();
        while (iterator.hasNext()) {
            Element e = iterator.next();
            ChatlistNodes(e);
        }
    }

    public void receiveVideo(ArrayList viewList) {
        engine = getEngine();
        Vlayout2.removeAllViews();
        Vlayout3.removeAllViews();
        Vlayout4.removeAllViews();

        Vlayout2.addView(getView(R.mipmap.videoicon, xx, yy));
        Vlayout3.addView(getView(R.mipmap.videoicon, xx, yy));
        Vlayout4.addView(getView(R.mipmap.videoicon, xx, yy));

        int size = viewList.size();
        for (int i = 0; i < size; i++) {
            System.out.println("这是list的内容" + viewList.get(i).toString());
            if (i == 0) {
                int ssrc2 = Integer.parseInt(viewList.get(0).toString());
                Vlayout2.removeAllViews();
                engine.startrecv_audio(ssrc2);
                SurfaceView mView = engine.startrecv(ssrc2);
                Vlayout2.addView(mView, params);
            } else if (i == 1) {
                int ssrc3 = Integer.parseInt(viewList.get(1).toString());
                engine.startrecv_audio(ssrc3);
                Vlayout3.removeAllViews();
                SurfaceView mView = engine.startrecv(ssrc3);
                Vlayout3.addView(mView, params);
            } else if (i == 2) {
                int ssrc4 = Integer.parseInt(viewList.get(2).toString());
                engine.startrecv_audio(ssrc4);
                Vlayout4.removeAllViews();
                SurfaceView mView = engine.startrecv(ssrc4);
                Vlayout4.addView(mView, params);
            }

        }
    }

    public void StopRecAllVideo() {
        int alllength = viewList.size();
        for (int i = 0; i < alllength; i++) {
            System.out.println("原先的列表" + viewList.get(i).toString());
            engine = getEngine();
            engine.stoprecv(Integer.parseInt(viewList.get(i).toString()));
            engine.stoprecv_audio(Integer.parseInt(viewList.get(i).toString()));
            System.out.println("关闭");
        }

        Vlayout2.removeAllViews();
        Vlayout3.removeAllViews();
        Vlayout4.removeAllViews();
        Vlayout2.addView(getView(R.mipmap.videoicon, xx, yy));
        Vlayout3.addView(getView(R.mipmap.videoicon, xx, yy));
        Vlayout4.addView(getView(R.mipmap.videoicon, xx, yy));
    }

    private int ii = 0;
    ArrayList viewList = new ArrayList();

    public void listADDNodes(Element node) {
        List<Attribute> list = node.attributes();
        for (Attribute attribute : list) {
        }
        if (!(node.getTextTrim().equals(""))) {
            int addlength = viewList.size();
            for (int i = 0; i < addlength; i++) {
                if (viewList.get(i).toString().equals(node.getText())) {
                    ii = 1;
                    break;
                } else {
                    ii = 0;
                }
            }
            if (ii == 0) {
                viewList.add(Integer.parseInt(node.getText()));
            }
        }
        Iterator<Element> iterator = node.elementIterator();
        while (iterator.hasNext()) {
            Element e = iterator.next();
            listADDNodes(e);
        }
    }


    public void listMOVENodes(Element node) {

        List<Attribute> list = node.attributes();
        for (Attribute attribute : list) {
        }
        if (!(node.getTextTrim().equals(""))) {
            if (node.getName().equals("SSRC")) {
                int length = viewList.size();
                for (int i = 0; i < length; i++) {
                    if (viewList.get(i).toString().equals(node.getText().toString())) {
                        System.out.println("这是停止的循环内部" + viewList.get(i).toString());
                        viewList.remove(i);

                    }
                }
            }
        }
        Iterator<Element> iterator = node.elementIterator();
        while (iterator.hasNext()) {
            Element e = iterator.next();
            listMOVENodes(e);
        }
    }
}
