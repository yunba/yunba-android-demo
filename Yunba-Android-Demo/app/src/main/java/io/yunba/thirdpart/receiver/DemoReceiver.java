package io.yunba.thirdpart.receiver;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import io.yunba.android.manager.YunBaManager;
import io.yunba.thirdpart.activity.YunBaApplication;
import io.yunba.thirdpart.util.DemoUtil;
import io.yunba.thirdpart.util.SharePrefsHelper;


public class DemoReceiver extends BroadcastReceiver {
	private final static String REPORT_MSG_SHOW_NOTIFICARION = "1000";
	private final static String REPORT_MSG_SHOW_NOTIFICARION_FAILED = "1001";

	@Override
	public void onReceive(Context context, Intent intent) {
		if (YunBaManager.MESSAGE_RECEIVED_ACTION.equals(intent.getAction())) {
			DemoUtil.stopTimer();
			String status = "Yunba - Connected";
			DemoUtil.setTitleOfApp(YunBaApplication.mainActivity,status,true);
			SharePrefsHelper.setString(context, DemoUtil.CONNECT_STATUS, status);

			String topicORalias = intent.getStringExtra(YunBaManager.MQTT_TOPIC);
			String message = intent.getStringExtra(YunBaManager.MQTT_MSG);
			boolean flag = DemoUtil.showNotification(context, topicORalias, message);
			//上报显示通知栏状态， 以方便后台统计
			if (flag)
				YunBaManager.report(context, REPORT_MSG_SHOW_NOTIFICARION, topicORalias);
			else
				YunBaManager.report(context, REPORT_MSG_SHOW_NOTIFICARION_FAILED, topicORalias);

			StringBuilder msgsb = new StringBuilder();
			msgsb.append("receive message from server: ")
					.append("topic/alias").append(" = ").append(topicORalias).append(" ,")
					.append(YunBaManager.MQTT_MSG).append(" = ").append(message);
			DemoUtil.printOnScree(msgsb.toString(), DemoUtil.PUBLISHACK);

		} else if (YunBaManager.PRESENCE_RECEIVED_ACTION.equals(intent.getAction())) {
			String status = "Yunba - Connected";
			DemoUtil.setTitleOfApp(YunBaApplication.mainActivity,status,true);
			SharePrefsHelper.setString(context, DemoUtil.CONNECT_STATUS, status);

			String topic = intent.getStringExtra(YunBaManager.MQTT_TOPIC);
			String message = intent.getStringExtra(YunBaManager.MQTT_MSG);
			StringBuilder msgsb = new StringBuilder();
			msgsb.append("receive message from presence: ")
					.append(YunBaManager.MQTT_TOPIC).append(" = ").append(topic).append(" ,")
					.append(YunBaManager.MQTT_MSG).append(" = ").append(message);
			DemoUtil.printOnScree(msgsb.toString(), DemoUtil.PUBLISHACK);

		} else if (YunBaManager.MESSAGE_CONNECTED_ACTION.equals(intent.getAction())) {
			String status = "Yunba - Connected";
			DemoUtil.setTitleOfApp(YunBaApplication.mainActivity,status,true);
			SharePrefsHelper.setString(context, DemoUtil.CONNECT_STATUS, status);
			DemoUtil.printOnScree("connection success.", DemoUtil.CONNECT);

		} else if (YunBaManager.MESSAGE_DISCONNECTED_ACTION.equals(intent.getAction())) {
			String status = "Yunba - DisConnected";
			DemoUtil.setTitleOfApp(YunBaApplication.mainActivity,status,false);
			SharePrefsHelper.setString(context, DemoUtil.CONNECT_STATUS, status);
			DemoUtil.printOnScree("connection fail.", DemoUtil.DISCONNET);
		}
	}

}
