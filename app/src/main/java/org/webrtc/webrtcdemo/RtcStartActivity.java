package org.webrtc.webrtcdemo;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.snydu.icuvideo.icuvideoapp.BlankFragment;
import com.snydu.icuvideo.icuvideoapp.Fragment.UserListFragment;
import com.snydu.icuvideo.icuvideoapp.R;
import com.snydu.icuvideo.icuvideoapp.event.GetXmlEvent;
import com.snydu.icuvideo.icuvideoapp.event.SendXmlEvent;
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


public class RtcStartActivity extends Activity implements MenuStateProvider {
    private SharedPreferences userInfo = null;
    private ArrayList<UserNode> List;
    private FrameLayout UserListFrgment;
    private NativeWebRtcContextRegistry contextRegistry = null;
    private MediaEngine mediaEngine = null;
    private MainMenuFragment f1;
    private BlankFragment f2;
    private FragmentManager fragmentManager;
    private FrameLayout UserListFragment;
    private com.snydu.icuvideo.icuvideoapp.Fragment.UserListFragment leftFragment;
    private RelativeLayout RtcactivityReLayout;
    private Button closebutton;

//    @Override
//    protected void onRestart() {
//        super.onRestart();
//        contextRegistry = new NativeWebRtcContextRegistry();
////        contextRegistry.unRegister();
//        contextRegistry.register(this);
//
//        mediaEngine = null;
//        // Load all settings dictated in xml.
//        mediaEngine = new MediaEngine(this);
//
//        mediaEngine.setRemoteIp(getResources().getString(R.string.loopbackIp));
//        // mediaEngine.setTrace(getResources().getBoolean(
//        //   R.bool.trace_enabled_default));
//        mediaEngine.setTrace(false);
//        mediaEngine.setAudio(getResources().getBoolean(
//                R.bool.audio_enabled_default));
//        mediaEngine.setAudioCodec(mediaEngine.getIsacIndex());
//        mediaEngine.setAudioRxPort(getResources().getInteger(
//                R.integer.aRxPortDefault));
//        mediaEngine.setAudioTxPort(getResources().getInteger(
//                R.integer.aTxPortDefault));
//        mediaEngine.setSpeaker(getResources().getBoolean(
//                R.bool.speaker_enabled_default));
//        mediaEngine.setDebuging(getResources().getBoolean(
//                R.bool.apm_debug_enabled_default));
//
//        mediaEngine.setReceiveVideo(getResources().getBoolean(
//                R.bool.video_receive_enabled_default));
//        mediaEngine.setSendVideo(getResources().getBoolean(
//                R.bool.video_send_enabled_default));
//        mediaEngine.setVideoCodec(getResources().getInteger(
//                R.integer.video_codec_default));
//        // TODO(hellner): resolutions should probably be in the xml as well.
//        mediaEngine.setResolutionIndex(MediaEngine.numberOfResolutions() - 2);
//        mediaEngine.setVideoTxPort(getResources().getInteger(
//                R.integer.vTxPortDefault));
//        mediaEngine.setVideoRxPort(getResources().getInteger(
//                R.integer.vRxPortDefault));
//        mediaEngine.setNack(getResources().getBoolean(R.bool.nack_enabled_default));
//        mediaEngine.setViewSelection(getResources().getInteger(
//                R.integer.defaultView));
//    }

    public String roomId ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rtcstartactivity);
        EventBus.getDefault().register(this);
        Bundle bundle = this.getIntent().getBundleExtra("key-userlist");
        List = (ArrayList<UserNode>) bundle.getSerializable("userlist");
        roomId = bundle.getString("enterRoomId");
//        System.out.println("输出第一个" + List.get(0).getUserName());
        RtcactivityReLayout = (RelativeLayout) findViewById(R.id.RtcactivityReLayout);
        UserListFragment = (FrameLayout) findViewById(R.id.UserListFrgment);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // State.
        // Must be instantiated before MediaEngine.
        contextRegistry = new NativeWebRtcContextRegistry();
//        contextRegistry.unRegister();
        contextRegistry.register(this);

        mediaEngine = null;
        // Load all settings dictated in xml.
        mediaEngine = new MediaEngine(this);

        mediaEngine.setRemoteIp(getResources().getString(R.string.loopbackIp));
        // mediaEngine.setTrace(getResources().getBoolean(
        //   R.bool.trace_enabled_default));
        mediaEngine.setTrace(false);
        mediaEngine.setAudio(getResources().getBoolean(
                R.bool.audio_enabled_default));
        mediaEngine.setAudioCodec(mediaEngine.getIsacIndex());
        mediaEngine.setAudioRxPort(getResources().getInteger(
                R.integer.aRxPortDefault));
        mediaEngine.setAudioTxPort(getResources().getInteger(
                R.integer.aTxPortDefault));
        mediaEngine.setSpeaker(getResources().getBoolean(
                R.bool.speaker_enabled_default));
        mediaEngine.setDebuging(getResources().getBoolean(
                R.bool.apm_debug_enabled_default));

        mediaEngine.setReceiveVideo(getResources().getBoolean(
                R.bool.video_receive_enabled_default));
        mediaEngine.setSendVideo(getResources().getBoolean(
                R.bool.video_send_enabled_default));
        mediaEngine.setVideoCodec(getResources().getInteger(
                R.integer.video_codec_default));
        // TODO(hellner): resolutions should probably be in the xml as well.
        mediaEngine.setResolutionIndex(MediaEngine.numberOfResolutions() - 2);
        mediaEngine.setVideoTxPort(getResources().getInteger(
                R.integer.vTxPortDefault));
        mediaEngine.setVideoRxPort(getResources().getInteger(
                R.integer.vRxPortDefault));
        mediaEngine.setNack(getResources().getBoolean(R.bool.nack_enabled_default));
        mediaEngine.setViewSelection(getResources().getInteger(
                R.integer.defaultView));


        f1 = new MainMenuFragment();

        Bundle bundle22 = new Bundle();
//        bundle.putInt("id", 10010);
        bundle22.putSerializable("enterRoomId", roomId);
        f1.setArguments(bundle22);
        fragmentManager = getFragmentManager();
        openFragment(f1, "f1");


//        f2 = new BlankFragment();
//        openssFragment(f2, "f2");
//        setLayout(textlayout, 500, 0);

        leftFragment = new UserListFragment();
        leftFragment.setoutLayout(UserListFragment);
        Bundle bundle11 = new Bundle();
//        bundle.putInt("id", 10010);
        bundle11.putSerializable("userlist", List);
        bundle11.putString("enterRoomId", roomId);
        leftFragment.setArguments(bundle11);



        FragmentTransaction TF2 = fragmentManager.beginTransaction();
        TF2.replace(R.id.UserListFrgment, leftFragment, "tf2");
        TF2.addToBackStack(null);
        TF2.commit();

//        EventBus.getDefault().post(new UserlistEvent(List));


        ViewTreeObserver vto2 = RtcactivityReLayout.getViewTreeObserver();
        vto2.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                RtcactivityReLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                RtcactivityReLayout.getHeight();
                width = RtcactivityReLayout.getWidth();
                int width2 = UserListFragment.getWidth();
                System.out.println("这是width22222222222222222" + width2);
                int ButtonWidth = leftFragment.getButtonWidth();
                setLayout(UserListFragment, width - ButtonWidth, 0);
            }
        });
    }

    public int width = 0;

    public static void setLayout(View view, int x, int y) {
        ViewGroup.MarginLayoutParams margin = new ViewGroup.MarginLayoutParams(view.getLayoutParams());
        margin.setMargins(x, y, x + margin.width, y + margin.height);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(margin);
        view.setLayoutParams(layoutParams);
    }

    private void openFragment(Fragment fragment, String tag) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.linelayout1, fragment, tag);
        //添加到返回按键堆栈，这样就可以执行碎片的第二条周期，也就是不会每次replace的时候都去执行onAttach
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }


    @Override
    public MediaEngine getEngine() {
        return mediaEngine;
    }

    @Subscribe
    public void onEventMainThread(GetXmlEvent event) throws IOException, DocumentException {
        String msg = "in MainPresenter：" + event.getCmdCode() + event.getGetinfoXml();
        String xmlinfo = event.getGetinfoXml();

        if (event.getCmdCode() == 0x0080) {
            Document document = DocumentHelper.parseText(xmlinfo);
            Element root = document.getRootElement();
            listNodes(root);


        }
    }

    @Override
    protected void onStop() {
        contextRegistry.unRegister();
//        mediaEngine.dispose();
//        mediaEngine.stop();
        contextRegistry = null;
        mediaEngine = null;
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        EventBus.getDefault().post(new SendXmlEvent("", 0x0063));

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.out.println("RtcActivity被关闭");
        EventBus.getDefault().post(new SendXmlEvent("<?xml version=\"1.0\" encoding=\"UTF-8\"?><MEETING><SWITCH>OFF</SWITCH></MEETING>", 0x0030));
//        EventBus.getDefault().post(new SendXmlEvent("", 0x0012));
        EventBus.getDefault().unregister(this);
    }

    public void listNodes(Element node) {
        System.out.println("当前节点的名称：" + node.getName());
        //首先获取当前节点的所有属性节点
        List<Attribute> list = node.attributes();
        //遍历属性节点
        for (Attribute attribute : list) {
            System.out.println("属性" + attribute.getName() + ":" + attribute.getValue());
        }
        //如果当前节点内容不为空，则输出
        if (!(node.getTextTrim().equals(""))) {
            System.out.println("内容" + node.getName() + "：" + node.getText());
        }
        //同时迭代当前节点下面的所有子节点
        //使用递归
        Iterator<Element> iterator = node.elementIterator();
        while (iterator.hasNext()) {
            Element e = iterator.next();
            listNodes(e);
        }
    }
}
