package com.snydu.icuvideo.icuvideoapp.utils;

public interface Order {

	//心跳
	final int ALIVE = 0x0000;
	final int R_ALIVE = 0x0800;

	//登陆
	final int DEVICE_LOGIN = 0x0001;
	final int R_DEVICE_LOGIN = 0x0801;

	//进入会议室
	final int LOGIN_MEETTINGROOM = 0x0002;
	final int R_LOGIN_MEETTINGROOM = 0x0802;

	//申请成为主持人
	final int APPLY_FOR_PRESENTER = 0x0003;
	final int R_APPLY_FOR_PRESENTER = 0x0803;

	//取消成为主持人
	final int CANCEL_FOR_PRESENTER= 0x0004;
	final int R_CANCEL_FOR_PRESENTER = 0x0804;

	//暂时写到这里

	final int UPLOAD_TASK_RESULT = 0x0005;
	final int R_UPLOAD_TASK_RESULT = 0x0805;

	final int SEARCH_REQUEST = 0x0006;
	final int R_SEARCH_REQUEST = 0x0806;

	final int SEARCH_RESULT = 0x0007;
	final int R_SEARCH_RESULT = 0x0807;

	final int REQUEST_VIDEO_CHAT = 0x0008;
	final int R_REQUEST_VIDEO_CHAT = 0x0808;

	final int RING = 0x0009;
	final int R_RING = 0x0809;

	final int ACCEPT_RING = 0x0000A;
	final int R_ACCEPT_RING = 0x080A;

	final int STOP_VIDEO_CHAT = 0x000B;
	final int R_STOP_VIDEO_CHAT = 0x080B;

	final int HUNG_UP = 0x000C;
	final int R_HUNG_UP = 0x080C;

	final int REQUEST_USERLIST = 0x000D;
	final int R_REQUEST_USERLIST = 0x080D;
	
	final int REQUEST_POSITION = 0x000F;
	final int R_REQUEST_POSITION = 0x080F;

	final int SELF_DESTROY = 0x000E;
	final int R_SELF_DESTROY = 0x080E;
	
	final int REQUEST_SEARCH_SERVICE_ADDR = 0x0030;
	final int R_REQUEST_SEARCH_SERVICE_ADDR = 0x0830;
	
	final String DEVICE_LOGIN_ACTION = "cn.yapon.epguard.DEVICE_LOGIN";
	final String ACCEPT_ACTION = "cn.yapon.videochat.ACCEPT_ACTION";
	final String RING_ACTION = "cn.yapon.videochat.RING_ACTION";
	final String STOP_RING_ACTION = "cn.yapon.videochat.STOP_RING_ACTION";
	final String HUNG_UP_ACTION = "cn.yapon.videochat.HUNG_UP";
	final String PASSWORD_CHANGED = "cn.yapon.PASSWORD_CHANGED";
	final String EXPRESS_LIST_CHANGED_ACTION = "EXPRESS_LIST_CHANGED_ACTION";

}
