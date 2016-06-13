package com.snydu.icuvideo.icuvideoapp.Fragment;

import android.animation.ObjectAnimator;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.snydu.icuvideo.icuvideoapp.R;
import com.snydu.icuvideo.icuvideoapp.adapter.UserListAdapter;
import com.snydu.icuvideo.icuvideoapp.event.GetXmlEvent;
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

/**
 * Created by Boria on 2016/5/16.
 */
public class UserListFragment extends Fragment {
    private ListView userlistview;
    private UserListAdapter userListAdapter;
    private ArrayList<UserNode> List;
    private ArrayList<UserNode> NewList;
    private FrameLayout userlistlayout;
    private ImageButton movebutton;
    private FrameLayout userlistReLayout;
    private RelativeLayout seLayout;
    private int point = 1;
    public int buttonWidth;
    public String roomID;
    private SharedPreferences userInfo = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_userlist, container, false);
        userInfo = getActivity().getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        String userid = userInfo.getString("userid", "");
        Bundle bundle = getArguments();
        List = (ArrayList<UserNode>) bundle.getSerializable("userlist");
        roomID = bundle.getString("enterRoomId");
        userlistview = (ListView) v.findViewById(R.id.userList_listview);
        movebutton = (ImageButton) v.findViewById(R.id.movebutton);
        seLayout = (RelativeLayout) v.findViewById(R.id.userlistReLayout);

        movebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float curTranslationX = userlistReLayout.getTranslationX();
                float XX = seLayout.getWidth();
                ObjectAnimator animator = null;
                if (point == 1) {
                    animator = ObjectAnimator.ofFloat(userlistReLayout, "translationX", curTranslationX, buttonWidth - XX);
                    point = 0;
                } else if (point == 0) {
                    animator = ObjectAnimator.ofFloat(userlistReLayout, "translationX", buttonWidth - XX, 0);
                    point = 1;
                }

                animator.setDuration(700);
                animator.start();
            }
        });
        userListAdapter = new UserListAdapter(getActivity(), List);
        userListAdapter.setUserId(userid);
        userListAdapter.setRoomId(roomID);
        userlistview.setAdapter(userListAdapter);
        return v;
    }

    public void setoutLayout(FrameLayout UserListFragment) {
        userlistReLayout = UserListFragment;
    }

    public int getButtonWidth() {
        buttonWidth = movebutton.getWidth();
        return buttonWidth;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);

    }

    public static void setLayout(View view, int x, int y) {
        ViewGroup.MarginLayoutParams margin = new ViewGroup.MarginLayoutParams(view.getLayoutParams());
        margin.setMargins(x, y, x + margin.width, y + margin.height);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(margin);
        view.setLayoutParams(layoutParams);
    }

    //    98
    @Subscribe
    public void onEventMainThread(GetXmlEvent event) throws IOException, DocumentException {
        String msg = "in MainPresenter：" + event.getCmdCode() + event.getGetinfoXml();
        String xmlinfo = event.getGetinfoXml();
        if (event.getCmdCode() == 0x0062) {
            NewList = new ArrayList<UserNode>();
            Document document = DocumentHelper.parseText(xmlinfo);
            Element root = document.getRootElement();
            listNodes(root);
            userListAdapter.refresh(NewList);
        }

    }

    public void listNodes(Element node) {

        if (node.getName().equals("USER")) {
            UserNode userNode = new UserNode();
            userNode.setUserName(node.getText());
//            System.out.println("这是节点值" + node.getName() + node.getText());
            java.util.List<Attribute> lllist = node.attributes();
            //遍历属性节点
            for (Attribute attribute : lllist) {
                if (attribute.getName().equals("PLATFORM")) {
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
//            userNode.getOnline()
            if (userNode.getOnline().equals("ON")) {
                NewList.add(userNode);
            }
//            NewList.add(userNode);
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
