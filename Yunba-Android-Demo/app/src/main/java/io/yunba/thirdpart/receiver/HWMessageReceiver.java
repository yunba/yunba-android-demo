package io.yunba.thirdpart.receiver;

import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.huawei.android.pushagent.api.PushEventReceiver;


public class HWMessageReceiver extends PushEventReceiver {
    public static final String TAG = "HWMessageReceiver";

    @Override
    public void onToken(Context context, String s, Bundle bundle) {
        String str = "get token and belongId successfully : token = " + s + " , "
                +"belongId = " + bundle.getString("belongId");
        Log.i(TAG, str);
    }

    @Override
    public boolean onPushMsg(Context context, byte[] bytes, Bundle bundle) {
        try {
            String str = "receive a passThrough message : " + new String(bytes, "UTF-8");
            Log.i(TAG, str);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public void onEvent(Context context, Event event, Bundle bundle) {
        if (Event.NOTIFICATION_OPENED.equals(event) || Event.NOTIFICATION_CLICK_BTN.equals(event)) {
            int notifyId = bundle.getInt(BOUND_KEY.pushNotifyId, 0);
            if (0 != notifyId) {
                NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                manager.cancel(notifyId);
            }
            String str = "receive a notification message : " + bundle.getString(BOUND_KEY.pushMsgKey);
            Log.i(TAG, str);
        } else if (Event.PLUGINRSP.equals(event)) {
            final int TYPE_LBS = 1;
            final int TYPE_TAG = 2;
            int reportType = bundle.getInt(BOUND_KEY.PLUGINREPORTTYPE, -1);
            boolean isSuccess = bundle.getBoolean(BOUND_KEY.PLUGINREPORTRESULT, false);
            String message = "";
            if (TYPE_LBS == reportType) {
                message = "LBS report result :";
            } else if (TYPE_TAG == reportType) {
                message = "TAG report result :";
            }
            Log.i(TAG, message + isSuccess);
        }
        super.onEvent(context, event, bundle);
    }

}
