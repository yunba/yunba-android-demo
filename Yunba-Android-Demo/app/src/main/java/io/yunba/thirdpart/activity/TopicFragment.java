package io.yunba.thirdpart.activity;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.json.JSONException;
import org.json.JSONObject;

import io.yunba.android.manager.YunBaManager;
import io.yunba.thirdpart.R;
import io.yunba.thirdpart.util.DemoUtil;


public class TopicFragment extends Fragment implements View.OnClickListener {

    private int qos=1;

    private Button subscribe;
    private Button unsubscribe;
    private Button prensence;
    private Button unprensence;

    private Button publishBtn;
    private Button publish2Btn;

    private Button qos0;
    private Button qos1;
    private Button qos2;

    private Button publish;
    private Button publish2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.tab_topic,container,false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
         subscribe = (Button)getView().findViewById(R.id.subscribeBtn);
         unsubscribe = (Button)getView().findViewById(R.id.unsubscribeBtn);
         prensence = (Button)getView().findViewById(R.id.prensenceBtn);
         unprensence = (Button)getView().findViewById(R.id.unPrensenceBtn);

        publishBtn = (Button)getView().findViewById(R.id.pubToTopicBtn);
        publish2Btn = (Button)getView().findViewById(R.id.pub2ToTopicBtn);


        qos0 = (Button)getView().findViewById(R.id.qos0_topic);
        qos1 = (Button)getView().findViewById(R.id.qos1_topic);
        qos2 = (Button)getView().findViewById(R.id.qos2_topic);

        publish = (Button)getView().findViewById(R.id.pubToTopic);
        publish2 = (Button)getView().findViewById(R.id.pub2ToTopic);

        subscribe.setOnClickListener(this);
        unsubscribe.setOnClickListener(this);
        prensence.setOnClickListener(this);
        unprensence.setOnClickListener(this);

        publishBtn.setOnClickListener(this);
        publish2Btn.setOnClickListener(this);

        qos0.setOnClickListener(this);
        qos1.setOnClickListener(this);
        qos2.setOnClickListener(this);

        publish.setOnClickListener(this);
        publish2.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.subscribeBtn:
                if(DemoUtil.isNetworkConnected(getActivity()))
                    subscribe();
                break;
            case R.id.unsubscribeBtn:
                if(DemoUtil.isNetworkConnected(getActivity()))
                    unsubscribe();
                break;
            case R.id.prensenceBtn:
                if(DemoUtil.isNetworkConnected(getActivity()))
                    prensence();
                break;
            case R.id.unPrensenceBtn:
                if(DemoUtil.isNetworkConnected(getActivity()))
                    unprensence();
                break;

            case R.id.pubToTopicBtn:
                toggle();
                break;
            case R.id.pub2ToTopicBtn:
                toggle();
                break;

            case R.id.qos0_topic:
                qos(0);
                break;
            case R.id.qos1_topic:
                qos(1);
                break;
            case R.id.qos2_topic:
                qos(2);
                break;

            case R.id.pubToTopic:
                if(DemoUtil.isNetworkConnected(getActivity()))
                    publish();
                break;
            case R.id.pub2ToTopic:
                try {
                    if(DemoUtil.isNetworkConnected(getActivity()))
                        publish2();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;

        }

    }

    /*---------------------------------event------------------------------------*/
    private void subscribe() {
        EditText topicEt=(EditText)getView().findViewById(R.id.topic_sub);
        final String topic = topicEt.getText().toString().trim();
        if (TextUtils.isEmpty(topic)) {
            DemoUtil.showToast("topcic should not be null",getActivity());
            return;
        }

        DemoUtil.printOnScree("subscribe topic = " + topic,DemoUtil.SUBSCRIBE);
        YunBaManager.subscribe(getActivity(), topic, new IMqttActionListener() {

            @Override
            public void onSuccess(IMqttToken asyncActionToken) {
                DemoUtil.showToast("subscribe succeed : " + topic, getActivity());
                StringBuilder str = new StringBuilder();
                str.append("subscribe ").append(YunBaManager.MQTT_TOPIC)
                        .append(" = ").append(topic).append(" succeed");
                DemoUtil.printOnScree(str.toString(),DemoUtil.SUBSCRIBEACk);
            }

            @Override
            public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                String str = "subscribe topic = " + topic + " failed : " + exception.getMessage();
                DemoUtil.printOnScree(str,DemoUtil.SUBSCRIBEACk);
                DemoUtil.showToast(str, getActivity());
            }
        });

    }

    private void unsubscribe() {
        EditText topicEt=(EditText)getView().findViewById(R.id.topic_sub);
        final String topic = topicEt.getText().toString().trim();
        if (TextUtils.isEmpty(topic)) {
            DemoUtil.showToast("topic should not be null", getActivity());
            return;
        }

        DemoUtil.printOnScree("unsubscribe topic = " + topic,DemoUtil.SUBSCRIBE);
        YunBaManager.unsubscribe(getActivity(), topic, new IMqttActionListener() {

            @Override
            public void onSuccess(IMqttToken asyncActionToken) {
                DemoUtil.showToast("unsubscribe succeed : " + topic, getActivity());
                StringBuilder str = new StringBuilder();
                str.append("unsubscribe ").append(YunBaManager.MQTT_TOPIC)
                        .append(" = ").append(topic).append(" succeed");
                DemoUtil.printOnScree(str.toString(),DemoUtil.SUBSCRIBEACk);
            }

            @Override
            public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                String str = "unsubscribe topic = " + topic + " failed : " + exception.getMessage();
                DemoUtil.printOnScree(str,DemoUtil.SUBSCRIBEACk);
                DemoUtil.showToast(str, getActivity());
            }
        });

    }

    private void prensence() {
        EditText topicEt=(EditText)getView().findViewById(R.id.topic_pre);
        final String topic = topicEt.getText().toString().trim();
        if (TextUtils.isEmpty(topic)) {
            DemoUtil.showToast("topic should not be null", getActivity());
            return;
        }

        DemoUtil.printOnScree("subscribe presence topic = " + topic,DemoUtil.SUBSCRIBE);
        YunBaManager.subscribePresence(getActivity(), topic, new IMqttActionListener() {

            @Override
            public void onSuccess(IMqttToken arg) {
                String str = "subscribe presence of topic = " + topic + " succeed ";
                DemoUtil.printOnScree(str,DemoUtil.SUBSCRIBEACk);
            }

            @Override
            public void onFailure(IMqttToken arg, Throwable exception) {
                String str = "subscribe presence of topic = " + topic + " failed : " + exception.getLocalizedMessage();
                DemoUtil.printOnScree(str,DemoUtil.SUBSCRIBEACk);
                DemoUtil.showToast(str, getActivity());
            }
        });
    }

    private void unprensence() {
        EditText topicEt=(EditText)getView().findViewById(R.id.topic_pre);
        final String topic = topicEt.getText().toString().trim();
        if (TextUtils.isEmpty(topic)) {
            DemoUtil.showToast("topic should not be null", getActivity());
            return;
        }

        DemoUtil.printOnScree("unsubscribe presence topic = " + topic,DemoUtil.SUBSCRIBE);
        YunBaManager.unsubscribePresence(getActivity(), topic, new IMqttActionListener() {

            @Override
            public void onSuccess(IMqttToken arg) {
                String str = "unSubscribe presence of topic = " + topic + " succeed ";
                DemoUtil.printOnScree(str,DemoUtil.SUBSCRIBEACk);
            }

            @Override
            public void onFailure(IMqttToken arg, Throwable exception) {
                String str = "unsubscribe presence of topic = " + topic + " failed : " + exception.getLocalizedMessage();
                DemoUtil.printOnScree(str,DemoUtil.SUBSCRIBEACk);
                DemoUtil.showToast(str, getActivity());
            }
        });

    }

    private void toggle() {
        LinearLayout linearLayout= (LinearLayout)getView().findViewById(R.id.pub2LinearLayout);
        if(linearLayout.getVisibility()==View.GONE){
            linearLayout.setVisibility(View.VISIBLE);
            publish.setVisibility(View.GONE);
            publishBtn.setBackgroundDrawable(getResources()
                    .getDrawable(R.drawable.left_gray_bg));
            publish2Btn.setBackgroundDrawable(getResources()
                    .getDrawable(R.drawable.right_blue_bg));
        }else if(linearLayout.getVisibility()==View.VISIBLE){
            linearLayout.setVisibility(View.GONE);
            publish.setVisibility(View.VISIBLE);
            publishBtn.setBackgroundDrawable(getResources()
                    .getDrawable(R.drawable.left_blue_bg));
            publish2Btn.setBackgroundDrawable(getResources()
                    .getDrawable(R.drawable.right_gray_bg));
        }
    }

    private void publish() {
        EditText topicEt=(EditText)getView().findViewById(R.id.topic_pub);
        EditText messageEt=(EditText)getView().findViewById(R.id.msgToTopic);

        final String topic = topicEt.getText().toString().trim();
        final String message = messageEt.getText().toString().trim();
        if (TextUtils.isEmpty(topic) ) {
            DemoUtil.showToast("topic should not be null", getActivity());
            return;
        }
        if ( TextUtils.isEmpty(message)) {
            DemoUtil.showToast("message should not be null", getActivity());
            return;
        }

        DemoUtil.printOnScree("publish message = " + message + " to topic = " + topic,DemoUtil.PUBLISH);
        DemoUtil.startTimer();
        YunBaManager.publish(getActivity(), topic, message, new IMqttActionListener() {

            public void onSuccess(IMqttToken asyncActionToken) {
                DemoUtil.stopTimer();
                StringBuilder str1 = new StringBuilder();
                str1.append("publish message")
                        .append(" = ").append(message).append(" to ")
                        .append(YunBaManager.MQTT_TOPIC).append(" = ").append(topic).append(" succeed");
                DemoUtil.printOnScree(str1.toString(),DemoUtil.PUBLISHACK);
                String str2 = "publish succeed : " + " topic = "+ topic;
                DemoUtil.showToast(str2, getActivity());
            }

            @Override
            public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                DemoUtil.stopTimer();
                String str = "publish topic = " + topic + " failed : " + exception.getMessage();
                DemoUtil.printOnScree(str.toString(),DemoUtil.PUBLISHACK);
                DemoUtil.showToast(str, getActivity());
            }
        });

    }

    private void qos(int i) {
        qos=i;
        if(i==0){
            qos0.setBackgroundDrawable(getResources().getDrawable(R.drawable.left_blue_bg));
            qos1.setBackgroundColor(getResources().getColor(R.color.yunba_gray));
            qos2.setBackgroundDrawable(getResources().getDrawable(R.drawable.right_gray_bg));
        }else if(i==1){
            qos0.setBackgroundDrawable(getResources().getDrawable(R.drawable.left_gray_bg));
            qos1.setBackgroundColor(getResources().getColor(R.color.yunba_universal_blue));
            qos2.setBackgroundDrawable(getResources().getDrawable(R.drawable.right_gray_bg));
        }else if(i==2){
            qos0.setBackgroundDrawable(getResources().getDrawable(R.drawable.left_gray_bg));
            qos1.setBackgroundColor(getResources().getColor(R.color.yunba_gray));
            qos2.setBackgroundDrawable(getResources().getDrawable(R.drawable.right_blue_bg));
        }
    }

    private void publish2() throws JSONException {
        EditText topicEt=(EditText)getView().findViewById(R.id.topic_pub);
        EditText messageEt=(EditText)getView().findViewById(R.id.msgToTopic);
        EditText alertEt=(EditText)getView().findViewById(R.id.alert_topic);
        EditText badgeEt=(EditText)getView().findViewById(R.id.badge_topic);
        EditText soundEt=(EditText)getView().findViewById(R.id.sound_topic);
        EditText notificationTitleEt=(EditText)getView().findViewById(R.id.notificationTitle_topic);
        EditText notificationContentEt=(EditText)getView().findViewById(R.id.notificationContent_topic);
        EditText timeToLiveEt=(EditText)getView().findViewById(R.id.timeToLive_topic);

        final String topic = topicEt.getText().toString().trim();
        final String message = messageEt.getText().toString().trim();
        final String alert = alertEt.getText().toString().trim();
        final String sound = soundEt.getText().toString().trim();
        final String badgeStr = badgeEt.getText().toString().trim();
        final String notificationTitle = notificationTitleEt.getText().toString().trim();
        final String notificationContent = notificationContentEt.getText().toString().trim();
        final String timeToLiveStr = timeToLiveEt.getText().toString().trim();

        //alias和message必填
        if (TextUtils.isEmpty(topic) || TextUtils.isEmpty(message)) {
            DemoUtil.showToast("topic or message should not be null", getActivity());
            return;
        }
        //如果sound或badge不为空，则alert必填
        if (TextUtils.isEmpty(alert) && !TextUtils.isEmpty(sound)
            ||TextUtils.isEmpty(alert) && !TextUtils.isEmpty(badgeStr)) {
            DemoUtil.showToast("alert  should not be null", getActivity());
            return;
        }
        //notificationTitle和notificationContent必须同时为空或者同时非空
        if (TextUtils.isEmpty(notificationTitle)^TextUtils.isEmpty(notificationContent)) {
            DemoUtil.showToast("notificationTitle or notificationContent should not be null", getActivity());
            return;
        }

        JSONObject opts= new JSONObject();
        JSONObject apn_json= new JSONObject();
        JSONObject aps= new JSONObject();
        if(!TextUtils.isEmpty(alert))
            aps.put("alert",alert);
        if(!TextUtils.isEmpty(badgeStr))
            aps.put("badge",Integer.parseInt(badgeStr));
        if(!TextUtils.isEmpty(sound))
            aps.put("sound",sound);
        if(aps.length()!=0) {
            apn_json.put("aps", aps);
            opts.put("apn_json", apn_json);
        }

        if(!TextUtils.isEmpty(notificationTitle)) {
            JSONObject third_party_json = new JSONObject();
            third_party_json.put("notification_title", notificationTitle);
            third_party_json.put("notification_content", notificationContent);
            opts.put("third_party_push",third_party_json);
        }

        opts.put("qos",qos);
        if(!TextUtils.isEmpty(timeToLiveStr))
            opts.put("time_to_live",Integer.parseInt(timeToLiveStr));

        DemoUtil.printOnScree("publish message = " +message+" , "+" topic = "+topic+" , "
                +" opts = "+opts.toString(),DemoUtil.PUBLISH);
        DemoUtil.startTimer();

        YunBaManager.publish2(getActivity(), topic, message, opts,new IMqttActionListener() {
            public void onSuccess(IMqttToken asyncActionToken) {
                DemoUtil.stopTimer();
                StringBuilder str1 = new StringBuilder();
                str1.append("publish(2) message").append(" = ").append(message).append(" to ")
                 .append(YunBaManager.MQTT_TOPIC).append(" = ").append(topic).append(" succeed");
                DemoUtil.printOnScree(str1.toString(),DemoUtil.PUBLISHACK);
                String str2 = "publish(2) succeed : " + " topic = "+ topic;
                DemoUtil.showToast(str2, getActivity());
            }

            public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                DemoUtil.stopTimer();
                String str = "publish(2) topic = " + topic + " failed : " + exception.getMessage();
                DemoUtil.printOnScree(str.toString(),DemoUtil.PUBLISHACK);
                DemoUtil.showToast(str, getActivity());
            }
        });

    }

}
