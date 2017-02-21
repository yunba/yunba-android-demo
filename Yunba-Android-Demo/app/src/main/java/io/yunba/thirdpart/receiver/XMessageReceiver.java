package io.yunba.thirdpart.receiver;

import android.content.Context;
import android.util.Log;

import com.xiaomi.mipush.sdk.MiPushCommandMessage;
import com.xiaomi.mipush.sdk.MiPushMessage;
import com.xiaomi.mipush.sdk.PushMessageReceiver;


public class XMessageReceiver extends PushMessageReceiver {
    public static final String TAG = "XMessageReceiver";

    @Override
    public void onReceivePassThroughMessage(Context context, MiPushMessage miPushMessage) {
        super.onReceivePassThroughMessage(context, miPushMessage);
        Log.i(TAG, "XM-onReceivePassThroughMessage" + miPushMessage.toString());
    }

    @Override
    public void onNotificationMessageClicked(Context context, MiPushMessage miPushMessage) {
        super.onNotificationMessageClicked(context, miPushMessage);
        Log.i(TAG,"XM-onNotificationMessageClicked" + miPushMessage.toString());
    }

    @Override
    public void onNotificationMessageArrived(Context context, MiPushMessage miPushMessage) {
        super.onNotificationMessageArrived(context, miPushMessage);
        String str = "XM-onNotificationMessageArrived:"
                + " description：" + miPushMessage.getDescription()
                + " title： " + miPushMessage.getTitle()
                + " messageId: " + miPushMessage.getMessageId();
        Log.i(TAG, str);
    }

    @Override
    public void onReceiveMessage(Context context, MiPushMessage miPushMessage) {
        super.onReceiveMessage(context, miPushMessage);
        Log.i(TAG, "XM-onNotificationMessageArrived:"
                + " description：" + miPushMessage.getDescription()
                + " title： " + miPushMessage.getTitle()
                + "messageId: " + miPushMessage.getMessageId());
    }

    @Override
    public void onReceiveRegisterResult(Context context, MiPushCommandMessage miPushCommandMessage) {
        super.onReceiveRegisterResult(context, miPushCommandMessage);
        Log.i(TAG,"XM-onReceiveRegisterResult: "
                + " Category: " + miPushCommandMessage.getCategory()
                + " Command: " + miPushCommandMessage.getCommand()
                + " Reason: " + miPushCommandMessage.getReason()
                + " CommandArguments: " + miPushCommandMessage.getCommandArguments()
                + "ResultCode: " + miPushCommandMessage.getResultCode());
    }

    @Override
    public void onCommandResult(Context context, MiPushCommandMessage miPushCommandMessage) {
        super.onCommandResult(context, miPushCommandMessage);
        Log.i(TAG, "XM-onCommandResult" + miPushCommandMessage.toString());
    }


}
