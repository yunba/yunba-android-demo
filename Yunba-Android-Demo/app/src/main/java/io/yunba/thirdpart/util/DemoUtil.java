package io.yunba.thirdpart.util;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.view.View;
import android.widget.Toast;

import java.io.IOException;
import java.net.InetAddress;
import java.util.List;
import java.util.Random;

import io.yunba.android.manager.YunBaManager;
import io.yunba.thirdpart.R;
import io.yunba.thirdpart.activity.MainActivity;


public class DemoUtil {
	/*定义不同类型的消息，只是为了在APP的“屏幕”上显示不同的颜色加以区分
    * 下面的消息类型均是按发送数据包的类型来划分，ACK对应包的应答*/
	public final static int CONNECT = 0;
	public final static int DISCONNET = 1;
	public final static int SUBSCRIBE = 2;
	public final static int SUBSCRIBEACk = 3;
	public final static int PUBLISH = 4;
	public final static int PUBLISHACK = 5;
	public final static int EXPAND = 6;
	public final static int EXPANDACK = 7;
	//特殊的类型，仅记录用户的操作并以日志在屏幕上输出
	public final static int USERLOG = 8;

	public final static String CONNECT_STATUS = "connect_status";

	public static boolean isEmpty(String s) {
		if (s==null)
			return true;
		if (s.length() == 0)
			return true;
		if (s.trim().length() == 0)
			return true;
		return false;
	}

	//判断应用是否还活着，是否在前台
	public static boolean isAppOnForeground(Context context) {
		ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		// Returns a list of application processes that are running on the device
		List<RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
		if (appProcesses == null)
			return false;
		for (RunningAppProcessInfo appProcess : appProcesses) {
			/* importance:
			 The relative importance level that the system places on this process.
			 May be one of IMPORTANCE_FOREGROUND,IMPORTANCE_BACKGROUND,
			 IMPORTANCE_VISIBLE,IMPORTANCE_SERVICE, or IMPORTANCE_EMPTY.
			 These constants are numbered so that "more important" values
			 are always smaller than "less important" values.
			 processName:
			 The name of the process that this object is associated with.*/
			if (appProcess.processName.equals(context.getPackageName())
					&& appProcess.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
				return true;
			}
		}
		return false;
	}

	public static boolean isNetworkConnected(Context context) {
		ConnectivityManager conn = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = conn.getActiveNetworkInfo();
		if (info != null && info.isConnected()){
			if(isNetworkAvailable())
				return true;
			else{
				showToast("Network is not available ",context);
				return false;
			}
		}else{
			showToast("Network is not connected ",context);
			return false;
		}
	}

	private static boolean isNetworkAvailable() {
		Runtime runtime = Runtime.getRuntime();
		try {
			Process ipProcess = runtime.exec("/system/bin/ping -c 1 www.baidu.com");
			int     exitValue = ipProcess.waitFor();
			return (exitValue == 0);
		} catch (IOException e){ e.printStackTrace(); }
		catch (InterruptedException e) { e.printStackTrace(); }
		return false;
	}

	public static void printOnScree(String str,int msgType) {
		Handler handler = MainActivity.handler;
		if (handler != null) {
			Message message = handler.obtainMessage();
			Bundle bundle=new Bundle();
			bundle.putString("str",str);
			bundle.putInt("msgType",msgType);
			message.obj = bundle;
			handler.sendMessage(message);
		}
	}

	public static boolean showNotification(Context context, String topic, String msg) {
		try {
			Uri alarmSound = RingtoneManager
					.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
			long[] pattern = { 500, 500, 500 };

			NotificationCompat.Builder mBuilder = new
			 NotificationCompat.Builder(context)
			.setSmallIcon(R.mipmap.ic_launcher)
			.setContentTitle(topic)
			.setContentText(msg)
			.setSound(alarmSound)
			.setVibrate(pattern)
			.setAutoCancel(true);

			 /*Creates an explicit intent for an Activity in your app*/
			//要进入的app的activity
			Intent resultIntent = new Intent(context,MainActivity.class);
			if (!DemoUtil.isEmpty(topic))
				resultIntent.putExtra(YunBaManager.MQTT_TOPIC, topic);
			if (!DemoUtil.isEmpty(msg))
				resultIntent.putExtra(YunBaManager.MQTT_MSG, msg);

			 /*The stack builder object will contain an artificial back stack
			 for the started Activity.This ensures that navigating backward from the Activity leads
			 of your application to the Home screen.*/

			TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
			//从通知页面按把back键回退到主页面
			stackBuilder.addParentStack(MainActivity.class);
			stackBuilder.addNextIntent(resultIntent);
			PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,
					PendingIntent.FLAG_UPDATE_CURRENT);

			mBuilder.setContentIntent(resultPendingIntent);
			NotificationManager mNotificationManager = (NotificationManager)context.
					getSystemService(Context.NOTIFICATION_SERVICE);
			mNotificationManager.notify(new Random().nextInt(), mBuilder.build());
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	public static void showToast(final String content, final Context context) {
		 if (!isAppOnForeground(context))
			 return;
		 new Thread(new Runnable() {
		 @Override
		 public void run() {
			 Looper.prepare();
			 Toast.makeText(context, content, Toast.LENGTH_SHORT).show();
			 Looper.loop();
		 }
		 }).start();
	}

	public static String getAppKey(Context context) {
		Bundle metaData = null;
		String appKey = null;
		try {
			ApplicationInfo ai = context.getPackageManager()
					.getApplicationInfo(context.getPackageName(),PackageManager.GET_META_DATA);
			if ( ai!= null) {
				metaData = ai.metaData;
			}
			if ( metaData!=null ) {
				appKey = metaData.getString("YUNBA_APPKEY");
				if (( appKey== null) || appKey.length() != 24) {
					appKey = "error";
				}
			}
		} catch (NameNotFoundException e) {}
		return appKey;
	}

	public static String getSecretKey(Context context) {
		Bundle metaData = null;
		String seckey = null;
		try {
			ApplicationInfo ai = context.getPackageManager()
			.getApplicationInfo(context.getPackageName(),PackageManager.GET_META_DATA);
			if ( ai!= null) {
				metaData = ai.metaData;
			}
			if ( metaData!=null ) {
				seckey = metaData.getString("YUNBA_SECRETKEY");
				if (seckey== null) {
					seckey = "error";
				}
			}
		} catch (NameNotFoundException e) {}
		return seckey;
	}

	//获取域名对应的ip
	public static void getInetAddress(final Handler handler, final String host){
		new Thread(new Runnable() {
			@Override
			public void run() {
			String ip = "";
			InetAddress inetAddress = null;
			Bundle bundle =new Bundle();
			Message msg =new Message();
			try {
				inetAddress = InetAddress.getByName(host);
				ip = inetAddress.getHostAddress();
			} catch (Exception e) {
				e.printStackTrace();
				bundle.putString("ip",ip);
				msg.obj=bundle;
				handler.sendMessage(msg);
				return ;
			}
			bundle.putString("ip",ip);
			msg.obj=bundle;
			handler.sendMessage(msg);
			return;
				}
		}).start();
	}

	public static void setTitleOfApp(Activity activity, final String status, boolean isConnected) {
		if (!DemoUtil.isEmpty(status)) {
			activity.setTitle(status);
		}
		ActionBar actionBar = activity.getActionBar();
		if(isConnected){
			actionBar.setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.actionbar_connected_bg));
		}else{
			actionBar.setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.actionbar_disconnected_bg));
		}
	}

	//开启计时器
	public static void startTimer(){
		MainActivity.timerView.setVisibility(View.VISIBLE);
		MainActivity.timer.start(0);
	}

	//停止计时器
	public static void  stopTimer(){
		MainActivity.timer.stop();
	}

	//将数组按指定分割符连接成字符串
	public static <T> String join(T[] array, String cement) {
		StringBuilder builder = new StringBuilder();
		if (array == null || array.length == 0) {
			return null;
		}
		for (T t : array) {
			builder.append(t).append(cement);
		}
		builder.delete(builder.length() - cement.length(), builder.length());
		return builder.toString();
	}

}
