package io.yunba.thirdpart.activity;


import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import io.yunba.thirdpart.R;
import io.yunba.thirdpart.util.DemoUtil;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;


public class RestfulFragment extends Fragment implements View.OnClickListener{

    private String bodyType="key-value";
    private String topicORalias_key="topic";
    private String url="";
    private String body="";
    private int qos=1;
    private boolean isBatch=false;
    private RequestBody requestBody = null;
    private MediaType MEDIA_TYPE= MediaType.parse("application/json");

    private Button getBtn;
    private Button keyValueBtn;
    private Button rawBtn;
    private Button postBtn;

    private EditText urlEt;
    private EditText bodyEt;
    private EditText methodEt;
    private EditText topicORaliasEt;

    private Button qos0;
    private Button qos1;
    private Button qos2;

    private Button getPublish;
    private Button postPublish;

    public RestfulFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.tab_restful,container,false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getBtn = (Button)getView().findViewById(R.id.getBtn);
        postBtn = (Button)getView().findViewById(R.id.postBtn);
        keyValueBtn = (Button)getView().findViewById(R.id.keyValueBtn);
        rawBtn = (Button)getView().findViewById(R.id.rawBtn);

        urlEt=(EditText)getView().findViewById(R.id.url);
        bodyEt=(EditText)getView().findViewById(R.id.body);
        methodEt=(EditText)getView().findViewById(R.id.method);
        topicORaliasEt=(EditText)getView().findViewById(R.id.topicORalias);

        qos0 = (Button)getView().findViewById(R.id.qos0);
        qos1 = (Button)getView().findViewById(R.id.qos1);
        qos2 = (Button)getView().findViewById(R.id.qos2);

        getPublish = (Button)getView().findViewById(R.id.getPublish);
        postPublish = (Button)getView().findViewById(R.id.postPublish);

        getBtn.setOnClickListener(this);
        postBtn.setOnClickListener(this);

        keyValueBtn.setOnClickListener(this);
        rawBtn.setOnClickListener(this);
        methodEt.setOnClickListener(this);
        methodEt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){ selectMethod(); }
            }
        });

        qos0.setOnClickListener(this);
        qos1.setOnClickListener(this);
        qos2.setOnClickListener(this);

        getPublish.setOnClickListener(this);
        postPublish.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.getBtn:
                toggle();
                break;
            case R.id.postBtn:
                toggle();
                break;

            case R.id.keyValueBtn:
                changeBodyType();
                break;
            case R.id.rawBtn:
                changeBodyType();
                break;
            case R.id.method:
                selectMethod();
                break;

            case R.id.qos0:
                qos(0);
                break;
            case R.id.qos1:
                qos(1);
                break;
            case R.id.qos2:
                qos(2);
                break;

            case R.id.getPublish:
                getPublish();
                break;
            case R.id.postPublish:
                postPublish();
                break;
        }
    }

    private void toggle() {
        LinearLayout linearLayout= (LinearLayout)getView().findViewById(R.id.postLinearLayout);
        if(linearLayout.getVisibility()==View.GONE){
            linearLayout.setVisibility(View.VISIBLE);
            getPublish.setVisibility(View.GONE);
            getBtn.setBackgroundDrawable(getResources()
                    .getDrawable(R.drawable.left_gray_bg));
            postBtn.setBackgroundDrawable(getResources()
                    .getDrawable(R.drawable.right_blue_bg));
            urlEt.setHint(R.string.postUrlHint);
            urlEt.setMinHeight(0);
        }else if(linearLayout.getVisibility()==View.VISIBLE){
            linearLayout.setVisibility(View.GONE);
            getPublish.setVisibility(View.VISIBLE);
            getBtn.setBackgroundDrawable(getResources()
                    .getDrawable(R.drawable.left_blue_bg));
            postBtn.setBackgroundDrawable(getResources()
                    .getDrawable(R.drawable.right_gray_bg));
            urlEt.setHint(R.string.getUrlHint);
        }
    }

    private void changeBodyType() {
        LinearLayout rawBody= (LinearLayout)getView().findViewById(R.id.rawBody);
        LinearLayout keyValueBody= (LinearLayout)getView().findViewById(R.id.keyValueBody);
        if(rawBody.getVisibility()==View.GONE){
            bodyType ="raw";
            rawBody.setVisibility(View.VISIBLE);
            keyValueBody.setVisibility(View.GONE);
            keyValueBtn.setBackgroundDrawable(getResources()
                    .getDrawable(R.drawable.left_gray_bg));
            rawBtn.setBackgroundDrawable(getResources()
                    .getDrawable(R.drawable.right_blue_bg));
        }else if(rawBody.getVisibility()==View.VISIBLE){
            bodyType ="key-value";
            rawBody.setVisibility(View.GONE);
            keyValueBody.setVisibility(View.VISIBLE);
            keyValueBtn.setBackgroundDrawable(getResources()
                    .getDrawable(R.drawable.left_blue_bg));
            rawBtn.setBackgroundDrawable(getResources()
                    .getDrawable(R.drawable.right_gray_bg));
        }
    }

    private void selectMethod() {
        new SelectMethodDialog(getActivity()).show();
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

    private void getPublish() {
        url = urlEt.getText().toString().trim();
        if (TextUtils.isEmpty(url) ) {
            DemoUtil.showToast("url should not be null", getActivity());
            return;
        }
        DemoUtil.printOnScree("publish by get url = " + url ,DemoUtil.PUBLISH);

        Request request = new Request.Builder().url(url).build();
        sendRequest(request);
    }

    private void postPublish() {
        url = urlEt.getText().toString().trim();
        body = bodyEt.getText().toString().trim();
        if (TextUtils.isEmpty(url) ) {
           url="http://rest.yunba.io:8080";
        }

        if(bodyType.equals("raw")){
            handleRaw(url, body);
        }
        if(bodyType.equals("key-value")){
            try {
                handleKeyValue();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleRaw(String url, final String body){
        if ( TextUtils.isEmpty(body)) {
            DemoUtil.showToast("body should not be null", getActivity());
            return;
        }
        DemoUtil.printOnScree("publish by post url = " + url +" , "+" body = " + body,DemoUtil.PUBLISH);

        requestBody = new RequestBody() {
            @Override
            public MediaType contentType() {
                return MEDIA_TYPE;
            }
            @Override
            public void writeTo(BufferedSink sink) throws IOException {
                sink.writeUtf8(body);
            }
        };

        Request request = new Request.Builder().url(url)
                .header("content-type","application/json")
                .post(requestBody).build();
        sendRequest(request);
    }

    private void handleKeyValue() throws JSONException {
        String appkey=DemoUtil.getAppKey(getActivity());
        String seckey=DemoUtil.getSecretKey(getActivity());

        EditText messageEt=(EditText)getView().findViewById(R.id.message);
        EditText alertEt=(EditText)getView().findViewById(R.id.alert);
        EditText soundEt=(EditText)getView().findViewById(R.id.sound);
        EditText badgeEt=(EditText)getView().findViewById(R.id.badge);
        EditText notificationTitleEt=(EditText)getView().findViewById(R.id.notificationTitle);
        EditText notificationContentEt=(EditText)getView().findViewById(R.id.notificationContent);
        EditText timeToLiveEt=(EditText)getView().findViewById(R.id.timeToLive);

        final String method = methodEt.getText().toString().trim();
        final String topicORalias = topicORaliasEt.getText().toString().trim();
        final String message = messageEt.getText().toString().trim();
        final String alert = alertEt.getText().toString().trim();
        final String sound = soundEt.getText().toString().trim();
        final String badgeStr = badgeEt.getText().toString().trim();
        final String notificationTitle = notificationTitleEt.getText().toString().trim();
        final String notificationContent = notificationContentEt.getText().toString().trim();
        final String timeToLiveStr = timeToLiveEt.getText().toString().trim();
        //必须选择一个方法
        if (TextUtils.isEmpty(method) ) {
            DemoUtil.showToast("please select a method ", getActivity());
            return ;
        }
        //alias和message必填
        if (TextUtils.isEmpty(topicORalias) || TextUtils.isEmpty(message)) {
            DemoUtil.showToast(topicORalias_key+" or message should not be null", getActivity());
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

        final JSONObject jsonData = new JSONObject();
        jsonData.put("method",method);
        jsonData.put("appkey",appkey);
        jsonData.put("seckey",seckey);
        if(!isBatch) {
            jsonData.put(topicORalias_key,topicORalias);
        }else {
            String[] aliasArray = topicORalias.split(",");
            JSONArray aliases = new JSONArray();
            for (String alias:aliasArray) {
                aliases.put(alias);
            }
            jsonData.put(topicORalias_key,aliases);
        }
        jsonData.put("msg",message);

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

        jsonData.put("opts",opts);
        DemoUtil.printOnScree("publish by post : " + jsonData.toString(),DemoUtil.PUBLISH);

        requestBody = new RequestBody() {
            @Override
            public MediaType contentType() {
                return MEDIA_TYPE;
            }
            @Override
            public void writeTo(BufferedSink sink) throws IOException {
                sink.writeUtf8(jsonData.toString());
            }
        };

        Request request = new Request.Builder().url(url)
                .header("content-type","application/json")
                .post(requestBody).build();
        sendRequest(request);
    }

    private void sendRequest(Request request){
        if(DemoUtil.isNetworkConnected(getActivity())) {
            DemoUtil.startTimer();
            YunBaApplication.getOkHttpClient().newCall(request).enqueue(new Callback() {
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    DemoUtil.stopTimer();
                    if (response.isSuccessful()) {
                        String str = response.body().string();
                        DemoUtil.printOnScree(str, DemoUtil.PUBLISHACK);
                    } else {
                        DemoUtil.printOnScree(response.code() + ":server error.", DemoUtil.PUBLISHACK);
                        DemoUtil.showToast(response.code() + ":server error.", getActivity());
                    }
                }

                @Override
                public void onFailure(Call call, IOException e) {
                    DemoUtil.stopTimer();
                    DemoUtil.printOnScree("publish failed.", DemoUtil.PUBLISHACK);
                    DemoUtil.showToast( "publish failed.", getActivity());
                }
            });
        }else{
            DemoUtil.showToast( "network is not available.", getActivity());
        }
    }

    /**
     * post选项下，点击“method”所弹出的自定义对话框
     */
    public class SelectMethodDialog extends Dialog implements View.OnClickListener {

        private final Context context;
        private TextView publishTv;
        private TextView publishAsyncTv;
        private TextView publishToAliasTv;
        private TextView publishToAliasBatchTv;

        public SelectMethodDialog(Context context) {
            super(context);
            this.context = context;
            initView();
            initEvent();
        }

        private void initView() {
            LayoutInflater inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.dialog_restful_method, null);
            setContentView(view);
            //去掉蓝色分割线
            int divierId = context.getResources().getIdentifier("android:id/titleDivider", null, null);
            View divider = findViewById(divierId);
            divider.setBackgroundColor(Color.TRANSPARENT);
            initWindow();

            publishTv = (TextView) findViewById(R.id.publish);
            publishAsyncTv = (TextView) findViewById(R.id.publishAsync);
            publishToAliasTv = (TextView) findViewById(R.id.publishToAlias);
            publishToAliasBatchTv = (TextView) findViewById(R.id.publishToAliasBatch);
        }

        private void initWindow() {
            Window dialogWindow = getWindow();
            dialogWindow.setBackgroundDrawable(new ColorDrawable(0));
            dialogWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN
                    | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
            WindowManager.LayoutParams lp = dialogWindow.getAttributes();
            DisplayMetrics d = context.getResources().getDisplayMetrics();
            lp.width = (int) (d.widthPixels * 0.8);
            lp.gravity = Gravity.CENTER;
            dialogWindow.setAttributes(lp);
        }

        private void initEvent() {
            publishTv.setOnClickListener(this);
            publishAsyncTv.setOnClickListener(this);
            publishToAliasTv.setOnClickListener(this);
            publishToAliasBatchTv.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.publish:
                    handleMethod(0);
                    break;
                case R.id.publishAsync:
                    handleMethod(1);
                    break;
                case R.id.publishToAlias:
                    handleMethod(2);
                    break;
                case R.id.publishToAliasBatch:
                    handleMethod(3);
                    break;
            }
        }

        private void handleMethod(final int i) {
            final TextView topicORaliasTv=(TextView)getView().findViewById(R.id.topicORaliasTv);
            final String[] methodArray={"publish","publish_async","publish_to_alias","publish_to_alias_batch"};
            methodEt.post(new Runnable() {
                @Override
                public void run() {
                    methodEt.setText(methodArray[i]);
                }
            });

            if(i==0||i==1){
                isBatch=false;
                topicORalias_key="topic";
                topicORaliasTv.post(new Runnable() {
                    @Override
                    public void run() {
                        topicORaliasTv.setText("Topic");
                    }
                });
                topicORaliasEt.setHint("topic");
            }

            if(i==2){
                isBatch=false;
                topicORalias_key="alias";
                topicORaliasTv.post(new Runnable() {
                    @Override
                    public void run() {
                        topicORaliasTv.setText("Alias");
                    }
                });
                topicORaliasEt.setHint("alias");
            }

            if(i==3){
                isBatch=true;
                topicORalias_key="aliases";
                topicORaliasTv.post(new Runnable() {
                    @Override
                    public void run() {
                        topicORaliasTv.setText("Aliases");
                    }
                });
                topicORaliasEt.setHint("alias1,alias2");
            }
            dismiss();
        }

    }
}


