package io.yunba.thirdpart.activity;

import android.app.Application;

import java.util.concurrent.TimeUnit;

import io.yunba.android.manager.YunBaManager;
import io.yunba.thirdpart.util.DemoUtil;
import io.yunba.thirdpart.util.SharePrefsHelper;
import okhttp3.OkHttpClient;

public class YunBaApplication extends Application {

	private static OkHttpClient mOkHttpClient=null;
	public static MainActivity mainActivity;

	@Override
	public void onCreate() {
		super.onCreate();
		initConnectStatus();
		startBlackService();
	}

	//set MainActivity title status
	private void initConnectStatus() {
		SharePrefsHelper.setString(getApplicationContext(), DemoUtil.CONNECT_STATUS, "");
	}

	private void startBlackService() {
		YunBaManager.setThirdPartyEnable(getApplicationContext(), true);
		YunBaManager.setXMRegister("2882303761517513281","5311751394281");
		YunBaManager.start(getApplicationContext());
	}

	public static OkHttpClient getOkHttpClient() {
		if (mOkHttpClient == null) {
			mOkHttpClient = new OkHttpClient().newBuilder()
					.connectTimeout(10, TimeUnit.SECONDS)//设置超时时间
					.readTimeout(10, TimeUnit.SECONDS)//设置读取超时时间
					.writeTimeout(10, TimeUnit.SECONDS)//设置写入超时时间
					.build();
		}
		return mOkHttpClient;
	}

}
