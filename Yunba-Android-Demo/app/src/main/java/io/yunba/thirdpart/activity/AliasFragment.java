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


public class AliasFragment extends Fragment implements View.OnClickListener{

    private int qos=1;

    private Button setAlias;
    private Button getAlias;

    private Button publishBtn;
    private Button publish2Btn;

    private Button qos0;
    private Button qos1;
    private Button qos2;

    private Button publish;
    private Button publish2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.tab_alias,container,false);
    }

    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setAlias = (Button)getView().findViewById(R.id.setAliasBtn);
        getAlias = (Button)getView().findViewById(R.id.getAliasBtn);

        publishBtn = (Button)getView().findViewById(R.id.pubToAliasBtn);
        publish2Btn = (Button)getView().findViewById(R.id.pub2ToAliasBtn);

        qos0 = (Button)getView().findViewById(R.id.qos0_alias);
        qos1 = (Button)getView().findViewById(R.id.qos1_alias);
        qos2 = (Button)getView().findViewById(R.id.qos2_alias);

        publish = (Button)getView().findViewById(R.id.pubToAlias);
        publish2 = (Button)getView().findViewById(R.id.pub2ToAlias);

        setAlias.setOnClickListener(this);
        getAlias.setOnClickListener(this);

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
        switch (v.getId()) {
            case R.id.setAliasBtn:
                if(DemoUtil.isNetworkConnected(getActivity()))
                    setAlias();
                break;
            case R.id.getAliasBtn:
                if(DemoUtil.isNetworkConnected(getActivity()))
                    getAlias();
                break;

            case R.id.pubToAliasBtn:
                toggle();
                break;
            case R.id.pub2ToAliasBtn:
                toggle();
                break;

            case R.id.qos0_alias:
                qos(0);
                break;
            case R.id.qos1_alias:
                qos(1);
                break;
            case R.id.qos2_alias:
                qos(2);
                break;

            case R.id.pubToAlias:
                if(DemoUtil.isNetworkConnected(getActivity()))
                    publish();
                break;
            case R.id.pub2ToAlias:
                try {
                    if(DemoUtil.isNetworkConnected(getActivity()))
                        publish2();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;

        }
    }


    private void setAlias() {
        EditText aliasEt=(EditText)getView().findViewById(R.id.alias_set_get);
        final String alias = aliasEt.getText().toString().trim();
        if (TextUtils.isEmpty(alias)) {
            DemoUtil.showToast("alias should not be null",getActivity());
            return;
        }
        DemoUtil.printOnScree("set alias = " + alias,DemoUtil.PUBLISH);
        YunBaManager.setAlias(getActivity(), alias, new IMqttActionListener() {

            @Override
            public void onSuccess(IMqttToken arg) {
                StringBuilder str = new StringBuilder();
                str.append("set alias ")
                   .append(" = ").append(alias).append(" succeed");
                DemoUtil.printOnScree(str.toString(),DemoUtil.PUBLISHACK);
            }

            @Override
            public void onFailure(IMqttToken arg0, Throwable arg) {
                StringBuilder str = new StringBuilder();
                str.append("set alias ")
                   .append(" = ").append(alias).append(" failed");
                DemoUtil.printOnScree(str.toString(),DemoUtil.PUBLISHACK);
            }
        });
    }

    private void getAlias() {
        DemoUtil.printOnScree("get alias ...",DemoUtil.PUBLISH);
        YunBaManager.getAlias(getActivity(), new IMqttActionListener() {

            @Override
            public void onSuccess(IMqttToken arg) {
                final String alias = arg.getAlias();
                final EditText aliasSEt=(EditText)getView().findViewById(R.id.alias_set_get);
                aliasSEt.post(new Runnable() {
                    @Override
                    public void run() {
                        aliasSEt.setText(alias);
                    }
                });

                StringBuilder str = new StringBuilder();
                str.append("get alias ")
                   .append(" = ").append(alias).append(" succeed");
                DemoUtil.printOnScree(str.toString(),DemoUtil.PUBLISHACK);
            }

            @Override
            public void onFailure(IMqttToken arg0, Throwable arg) {
                StringBuilder str = new StringBuilder();
                str.append("get alias ").append(" failed");
                DemoUtil.printOnScree(str.toString(),DemoUtil.PUBLISHACK);
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

    private void publish() {
        EditText aliasEt=(EditText)getView().findViewById(R.id.alias);
        EditText messageEt=(EditText)getView().findViewById(R.id.msgToAlias);

        final String alias = aliasEt.getText().toString().trim();
        final String message = messageEt.getText().toString().trim();
        if (TextUtils.isEmpty(alias) ) {
            DemoUtil.showToast("alias should not be null", getActivity());
            return;
        }
        if ( TextUtils.isEmpty(message)) {
            DemoUtil.showToast("message should not be null", getActivity());
            return;
        }

        DemoUtil.printOnScree("publish message = " + message + " to alias = " + alias,DemoUtil.PUBLISH);
        DemoUtil.startTimer();
        YunBaManager.publishToAlias(getActivity(), alias, message, new IMqttActionListener() {
            public void onSuccess(IMqttToken asyncActionToken) {
                DemoUtil.stopTimer();
                StringBuilder str1 = new StringBuilder();
                str1.append("publish  message").append(" = ").append(message).append(" to ")
                    .append("alias").append(" = ").append(alias).append(" succeed");
                DemoUtil.printOnScree(str1.toString(),DemoUtil.PUBLISHACK);
                String str2 = "publish  succeed : alias " + alias;
                DemoUtil.showToast(str2, getActivity());
            }

            @Override
            public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                DemoUtil.stopTimer();
                String str = "publish alias = " + alias + " failed : " + exception.getMessage();
                DemoUtil.printOnScree(str,DemoUtil.PUBLISHACK);
                DemoUtil.showToast(str, getActivity());
            }
        });
    }

    private void publish2() throws JSONException {
        EditText aliasEt=(EditText)getView().findViewById(R.id.alias);
        EditText messageEt=(EditText)getView().findViewById(R.id.msgToAlias);
        EditText alertEt=(EditText)getView().findViewById(R.id.alert_alias);
        EditText badgeEt=(EditText)getView().findViewById(R.id.badge_alias);
        EditText soundEt=(EditText)getView().findViewById(R.id.sound_alias);
        EditText notificationTitleEt=(EditText)getView().findViewById(R.id.notificationTitle_alias);
        EditText notificationContentEt=(EditText)getView().findViewById(R.id.notificationContent_alias);
        EditText timeToLiveEt=(EditText)getView().findViewById(R.id.timeTooLive_alias);

        final String alias = aliasEt.getText().toString().trim();
        final String message = messageEt.getText().toString().trim();
        final String alert = alertEt.getText().toString().trim();
        final String sound = soundEt.getText().toString().trim();
        final String badgeStr = badgeEt.getText().toString().trim();
        final String notificationTitle = notificationTitleEt.getText().toString().trim();
        final String notificationContent = notificationContentEt.getText().toString().trim();
        final String timeToLiveStr = timeToLiveEt.getText().toString().trim();
        //alias和message必填
        if (TextUtils.isEmpty(alias) || TextUtils.isEmpty(message)) {
            DemoUtil.showToast("alias or message should not be null", getActivity());
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

        DemoUtil.printOnScree("publish message = " + message +" , "+ " alias = "+alias+" , "
                +" opts = "+opts.toString(),DemoUtil.PUBLISH);
        DemoUtil.startTimer();

        YunBaManager.publish2ToAlias(getActivity(), alias, message, opts,new IMqttActionListener() {
            public void onSuccess(IMqttToken asyncActionToken) {
                DemoUtil.stopTimer();
                StringBuilder str1 = new StringBuilder();
                str1.append("publish(2) message").append(" = ").append(message)
                .append(" to ").append("alias").append(" = ").append(alias).append(" succeed");
                DemoUtil.printOnScree(str1.toString(),DemoUtil.PUBLISHACK);
                String str2 = "publish(2) succeed : " + alias;
                DemoUtil.showToast(str2, getActivity());
            }

            @Override
            public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                DemoUtil.stopTimer();
                String str = "publish(2) alias = " + alias + " failed : " + exception.getMessage();
                DemoUtil.printOnScree(str.toString(),DemoUtil.PUBLISHACK);
                DemoUtil.showToast(str, getActivity());
            }
        });
    }
}
