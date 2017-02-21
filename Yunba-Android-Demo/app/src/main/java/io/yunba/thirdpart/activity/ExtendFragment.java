package io.yunba.thirdpart.activity;


import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.util.regex.Pattern;

import io.yunba.android.manager.YunBaManager;
import io.yunba.thirdpart.R;
import io.yunba.thirdpart.util.DemoUtil;


public class ExtendFragment extends Fragment implements View.OnClickListener{

    private Button setBroker;
    private Button getTopicList;
    private Button getAliasList;
    private Button getState;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.tab_extend,container,false);
    }

    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setBroker = (Button)getView().findViewById(R.id.setBroker);
        getTopicList = (Button)getView().findViewById(R.id.getTopicList);
        getAliasList = (Button)getView().findViewById(R.id.getAliasList);
        getState = (Button)getView().findViewById(R.id.getState);

        setBroker.setOnClickListener(this);
        getTopicList.setOnClickListener(this);
        getAliasList.setOnClickListener(this);
        getState.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.setBroker:
                if(DemoUtil.isNetworkConnected(getActivity()))
                    setBroker();
                break;
            case R.id.getTopicList:
                if(DemoUtil.isNetworkConnected(getActivity()))
                    getTopicList();
                break;
            case R.id.getAliasList:
                if(DemoUtil.isNetworkConnected(getActivity()))
                    getAliasList();
                break;
            case R.id.getState:
                if(DemoUtil.isNetworkConnected(getActivity()))
                    getState();
                break;

        }
    }

    private void setBroker() {
        EditText ipEt=(EditText)getView().findViewById(R.id.ip);
        EditText portEt=(EditText)getView().findViewById(R.id.port);
        final String[] ip = {ipEt.getText().toString().trim()};
        final String port = portEt.getText().toString().trim();
        if (TextUtils.isEmpty(ip[0])) {
            DemoUtil.showToast("ip should not be null",getActivity());
            return;
        }
        if (TextUtils.isEmpty(port)) {
            DemoUtil.showToast("port should not be null",getActivity());
            return;
        }

        //匹配ip的正则表达式
        String str="^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\."
                +"(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
                +"(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
                +"(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)$";

        if(!Pattern.matches(str, ip[0])){
            android.os.Handler handler = new android.os.Handler(){
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);

                    Bundle bundle = (Bundle) msg.obj;
                    ip[0] =bundle.getString("ip");
                    if(DemoUtil.isEmpty(ip[0])){
                        DemoUtil.showToast("please enter the correct domain name or ip \n",getActivity());
                        return;
                    }
                    DemoUtil.printOnScree("set broker ip = "+ ip[0] +" and port = "+port, DemoUtil.EXPAND);
                    YunBaManager.setBroker(getActivity(), "tcp://" + ip[0] + ":"+port);
                }
            };
            DemoUtil.getInetAddress(handler, ip[0]);

        }else{
            DemoUtil.printOnScree("set broker ip = "+ ip[0] +" and port = "+port, DemoUtil.EXPAND);
            YunBaManager.setBroker(getActivity(), "tcp://" + ip[0] + ":"+port);
        }
    }

    private void getTopicList() {
        EditText aliasEt=(EditText)getView().findViewById(R.id.getTopicLisEt);
        final String alias = aliasEt.getText().toString().trim();
        if (TextUtils.isEmpty(alias)) {
            DemoUtil.showToast("alias should not be null",getActivity());
            return;
        }
        DemoUtil.printOnScree("get topic list of  " + alias+"...",DemoUtil.EXPAND);

        YunBaManager.getTopicListV2(getActivity(), alias, new IMqttActionListener() {
            @Override
            public void onSuccess(IMqttToken token) {
                String str = "get topic list of  " + alias + " success : result = " + token.getResult();
                DemoUtil.printOnScree(str,DemoUtil.EXPANDACK);
            }

            @Override
            public void onFailure(IMqttToken token, Throwable exception) {
                String str = "get topic list of  " + alias +exception.getLocalizedMessage();
                DemoUtil.printOnScree(str,DemoUtil.EXPANDACK);
            }
        });

    }

    private void getAliasList() {
        EditText topicEt=(EditText)getView().findViewById(R.id.getAliasListEt);
        final String topic = topicEt.getText().toString().trim();
        if (TextUtils.isEmpty(topic)) {
            DemoUtil.showToast("topic should not be null",getActivity());
            return;
        }
        DemoUtil.printOnScree("get alias list of  " + topic+"...",DemoUtil.EXPAND);

        YunBaManager.getAliasListV2(getActivity(), topic, new IMqttActionListener() {
            @Override
            public void onSuccess(IMqttToken token) {
                String str = "get alias list of  " + topic + " success : result = " + token.getResult();
                DemoUtil.printOnScree(str,DemoUtil.EXPANDACK);
            }

            @Override
            public void onFailure(IMqttToken token, Throwable exception) {
                String str = "get alias list of  " + topic +" failed : " + exception.getLocalizedMessage();
                DemoUtil.printOnScree(str,DemoUtil.EXPANDACK);
            }
        });
    }

    private void getState() {
        EditText stateEt=(EditText)getView().findViewById(R.id.getStateEt);
        final String alias = stateEt.getText().toString().trim();
        if (TextUtils.isEmpty(alias)) {
            DemoUtil.showToast("alias should not be null",getActivity());
            return;
        }
        DemoUtil.printOnScree("get state of  " + alias+"...",DemoUtil.EXPAND);

        YunBaManager.getStateV2(getActivity(), alias, new IMqttActionListener() {
            @Override
            public void onSuccess(IMqttToken arg) {
                String str = "get state of  " + alias + " success : " + arg.getResult();
                DemoUtil.printOnScree(str,DemoUtil.EXPANDACK);
            }

            @Override
            public void onFailure(IMqttToken arg0, Throwable exception) {
                String str = "get state of  " + alias + " failed : " + exception.getLocalizedMessage();
                if(exception instanceof MqttException) {
                    MqttException mqttException = (MqttException)exception;
                    Log.e("MqttException", "error code = " + mqttException.getReasonCode());
                }
                DemoUtil.printOnScree(str,DemoUtil.EXPANDACK);
            }
        });
    }

}
