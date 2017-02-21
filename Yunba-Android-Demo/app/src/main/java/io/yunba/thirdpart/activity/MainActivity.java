package io.yunba.thirdpart.activity;


import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.iwgang.countuptime.CountupView;
import io.yunba.android.manager.YunBaManager;
import io.yunba.thirdpart.R;
import io.yunba.thirdpart.util.DemoUtil;
import io.yunba.thirdpart.util.SharePrefsHelper;

public class MainActivity extends FragmentActivity implements View.OnClickListener {

    public static Handler handler;
    public static LinearLayout timerView;
    public static CountupView timer;

    private LinearLayout screen;
    private ScrollView scrollView;

    private Button hide;
    private ViewPager viewPager;
    private TextView topicTab;
    private TextView aliasTab;
    private TextView restfulTab;

    private TextView ExtendTab;
    private TopicFragment topicFragment;
    private AliasFragment aliasFragment;
    private RestfulFragment restfulFragment;
    private ExtendFragment extendFragment;
    private List<Fragment> fragmentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        YunBaApplication.mainActivity=MainActivity.this;

        initActionBar();
        initView();
        initEvent();
        initHandler();
        initNotification();
    }

    private void initActionBar() {
        // 初始化ActionBar实例
        ActionBar actionBar = getActionBar();
        // 设置不显示左侧图标
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setLogo(R.drawable.yb_circle);
        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.actionbar_default_bg));
        // 重要内容，获取Title的id, 关于id的定义，可见于SDK安装目录下的sdk\platforms\android-21\data\res\layout
        int titleId = Resources.getSystem().getIdentifier("action_bar_title", "id", "android");

        TextView tvTitle = (TextView) findViewById(titleId);
        tvTitle.setTextColor(0xFFFFFFFF);
        tvTitle.setTextSize(20);

        // 获取屏幕的相关尺寸，用以转换单位
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        float density = metric.density;
        int widthMax = metric.widthPixels;

        // 关键，设置宽度为屏幕宽度（但是实际显示并不是那样,左边还有一部分空白）
        tvTitle.setWidth(widthMax);
        // 所以需要设置左右内边距不同
        int paddingLeft = (int) (-16 * density);    // 控制左边距
        int paddingRight = (int) (30 * density);   // 右边距（近似值）
        tvTitle.setPadding(0, 0, paddingRight, 0);

        // 设置文字居中（因为设置了padding，显示的时候会以padding后的尺寸为准居中显示）
        tvTitle.setGravity(Gravity.CENTER);

        String status = SharePrefsHelper.getString(getApplicationContext(), DemoUtil.CONNECT_STATUS, null);
        if (!DemoUtil.isEmpty(status)) {
            boolean isConnected=status.equals("Yunba - Connected")?true:false;
            DemoUtil.setTitleOfApp(YunBaApplication.mainActivity,status,isConnected);
        }
        if (!DemoUtil.isNetworkConnected(getApplicationContext())) {
            DemoUtil.setTitleOfApp(YunBaApplication.mainActivity,"Yunba - DisConnected",false);
        }
    }

    private void initView() {
        //top view
        scrollView = (ScrollView) findViewById(R.id.scrollView);
        screen = (LinearLayout) findViewById(R.id.screen);

        //timer
        timerView = (LinearLayout) findViewById(R.id.timerView);
        timer = (CountupView) findViewById(R.id.countupView);
        hide = (Button) findViewById(R.id.hideTimerBtn);

        //bottom tap
        topicTab = (TextView) findViewById(R.id.topicTab);
        aliasTab = (TextView) findViewById(R.id.aliasTab);
        restfulTab = (TextView) findViewById(R.id.restfulTab);
        ExtendTab = (TextView) findViewById(R.id.Extend);

        //viewpager fragment
        topicFragment = new TopicFragment();
        aliasFragment = new AliasFragment();
        restfulFragment = new RestfulFragment();
        extendFragment = new ExtendFragment();
        fragmentList = new ArrayList<Fragment>();
        fragmentList.add(topicFragment);
        fragmentList.add(aliasFragment);
        fragmentList.add(restfulFragment);
        fragmentList.add(extendFragment);

        FragmentPagerAdapter adapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return fragmentList.get(position);
            }

            @Override
            public int getCount() {
                return fragmentList.size();
            }
        };

        viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setAdapter(adapter);
        selectedTab(0);
    }

    private void initEvent() {
        clearScreen();
        handleViewPager();

        hide.setOnClickListener(this);
        topicTab.setOnClickListener(this);
        aliasTab.setOnClickListener(this);
        restfulTab.setOnClickListener(this);
        ExtendTab.setOnClickListener(this);
    }

    private void clearScreen() {
        screen.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                new AlertDialog.Builder(MainActivity.this)
                    .setMessage("               clear screen")
                    .setPositiveButton("confirm",new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            screen.removeAllViews();
                        }
                    } ).setNegativeButton("cancl",null).show();
                return true;
            }
        });
    }

    private void handleViewPager() {
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }

            @Override
            public void onPageSelected(int position) {
                int currentItem = viewPager.getCurrentItem();
                selectedTab(currentItem);
                hideTimer();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //隐藏timer
            case R.id.hideTimerBtn:
                hideTimer();
                break;

            //bottom部分tab按钮的监听
            case R.id.topicTab:
                selectedTab(0);
                viewPager.setCurrentItem(0);
                break;
            case R.id.aliasTab:
                selectedTab(1);
                viewPager.setCurrentItem(1);
                break;
            case R.id.restfulTab:
                selectedTab(2);
                viewPager.setCurrentItem(2);
                break;
            case R.id.Extend:
                selectedTab(3);
                viewPager.setCurrentItem(3);
                break;

        }
    }

    private void hideTimer() {
        timer.stop();
        timerView.setVisibility(View.GONE);
    }

    private void selectedTab(int currentItem) {
        hideTimer();
        topicTab.setTextColor(getResources().getColor(R.color.yunba_black));
        aliasTab.setTextColor(getResources().getColor(R.color.yunba_black));
        restfulTab.setTextColor(getResources().getColor(R.color.yunba_black));
        ExtendTab.setTextColor(getResources().getColor(R.color.yunba_black));
        switch (currentItem) {
            case 0:
                topicTab.setTextColor(getResources().getColor(R.color.yunba_universal_blue));
                break;
            case 1:
                aliasTab.setTextColor(getResources().getColor(R.color.yunba_universal_blue));
                break;
            case 2:
                restfulTab.setTextColor(getResources().getColor(R.color.yunba_universal_blue));
                break;
            case 3:
                ExtendTab.setTextColor(getResources().getColor(R.color.yunba_universal_blue));
                break;
        }
    }

    //创建handler对象，并在handleMessage中将收到的消息按不同类型以不同颜色打印到“屏幕”
    private void initHandler() {
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                Bundle bundle = (Bundle) msg.obj;
                String str=bundle.getString("str");
                int msgType = bundle.getInt("msgType");

                String userLogPrefix="[userLog]: ";
                String subscribePrefix="[subscribe]: ";
                String subscribeackPrefix="[subscribeack]: ";
                String publishPrefix="[publish]: ";
                String publishackPrefix="[publishack]: ";
                String connectPrefix="[connect]: ";
                String disconnetPrefix="[disconnet]: ";
                String expandPrefix="[expand]: ";
                String expandackPrefix="[expandack]: ";

                TextView textView = new TextView(MainActivity.this);
                LinearLayout.LayoutParams params=new LinearLayout.LayoutParams
                        (LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
                textView.setLayoutParams(params);
                Typeface font = Typeface.createFromAsset(getAssets(),"fonts/consolab.ttf");
                textView.setTypeface(font);
                textView.setTextSize(14);
                if(msgType== DemoUtil.USERLOG){
                    textView.setTextColor(getResources().getColor(R.color.yunba_screen_userlog));
                    textView.setText(userLogPrefix+str);
                }else if(msgType==DemoUtil.CONNECT){
                    textView.setTextColor(getResources().getColor(R.color.yunba_screen_connect));
                    textView.setText(connectPrefix+str);
                }else if(msgType==DemoUtil.DISCONNET){
                    textView.setTextColor(getResources().getColor(R.color.yunba_screen_disconnect));
                    textView.setText(disconnetPrefix+str);
                }else if(msgType==DemoUtil.SUBSCRIBE){
                    textView.setTextColor(getResources().getColor(R.color.yunba_screen_subscribe));
                    textView.setText(subscribePrefix+str);
                }else if(msgType==DemoUtil.SUBSCRIBEACk){
                    textView.setTextColor(getResources().getColor(R.color.yunba_screen_subscribeack));
                    textView.setText(subscribeackPrefix+str);
                }else if(msgType==DemoUtil.PUBLISH){
                    textView.setTextColor(getResources().getColor(R.color.yunba_screen_publish));
                    textView.setText(publishPrefix+str);
                }else if(msgType==DemoUtil.PUBLISHACK){
                    textView.setTextColor(getResources().getColor(R.color.yunba_screen_publishack));
                    textView.setText(publishackPrefix+str);
                }else if(msgType==DemoUtil.EXPAND){
                    textView.setTextColor(getResources().getColor(R.color.yunba_screen_expand));
                    textView.setText(expandPrefix+str);
                }else if(msgType==DemoUtil.EXPANDACK){
                    textView.setTextColor(getResources().getColor(R.color.yunba_screen_expandack));
                    textView.setText(expandackPrefix+str);
                }
                screen.addView(textView);

                scrollView.post(new Runnable() {
                    @Override
                    public void run() {
                       scrollView.fullScroll(View.FOCUS_DOWN);
                    }
                });
            }
        };
    }

    //点击通知栏会执行此方法
    private void initNotification() {
        Intent intent = getIntent();
        if (intent != null) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                String topic = bundle.getString(YunBaManager.MQTT_TOPIC);
                String message = bundle.getString(YunBaManager.MQTT_MSG);

                if (topic != null && message != null) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("click notification : ")
                            .append(YunBaManager.MQTT_TOPIC).append(" = ").append(topic).append(" , ")
                            .append(YunBaManager.MQTT_MSG).append(" = ").append(message);
                    DemoUtil.printOnScree(sb.toString(), DemoUtil.USERLOG);
                }
            } else {
                System.out.println("no notification message");
            }
        } else {
            System.out.println("null intent");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home)
        {
            new ConnInfoDialog(MainActivity.this).show();
        }
        return super.onOptionsItemSelected(item);
    }

    public class ConnInfoDialog extends Dialog {
        public Activity context;

        public TextView brokerUrl;
        public TextView uID;
        public TextView password;
        public TextView clientID;
        public TextView deviceID;
        public TextView packageName;
        public TextView appkey;
        public TextView secretKey;
        public TextView copyToClipboard;

        String url = "";
        String uIDStr="";
        String passwordStr="";
        String clientIDStr="";
        String deviceIdStr= "";
        String defaultStr = "null";

        public ConnInfoDialog(Activity context) {
            super(context);
            this.context = context;
            initView();
            initEvent();
        }

        private void initView() {
            LayoutInflater inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.dialog_conn_info, null);
            setContentView(view);
            //去掉蓝色分割线
            int divierId = context.getResources().getIdentifier("android:id/titleDivider", null, null);
            View divider = findViewById(divierId);
            divider.setBackgroundColor(Color.TRANSPARENT);
            initWindow();

            brokerUrl = (TextView) findViewById(R.id.brokerUrl);
            uID = (TextView) findViewById(R.id.uID);
            password = (TextView) findViewById(R.id.password);
            clientID = (TextView) findViewById(R.id.clientID);
            deviceID= (TextView) findViewById(R.id.device_id);
            packageName= (TextView) findViewById(R.id.packageName);
            appkey= (TextView) findViewById(R.id.appKey);
            secretKey= (TextView) findViewById(R.id.seckey);
            copyToClipboard = (TextView)findViewById(R.id.copy);

            brokerUrl.setText(defaultStr);
            uID.setText(defaultStr);
            password.setText(defaultStr);
            clientID.setText(defaultStr);
            deviceID.setText(defaultStr);
            packageName.setText(context.getPackageName());
            appkey.setText(DemoUtil.getAppKey(context));
            secretKey.setText(DemoUtil.getSecretKey(context));
        }

        private void initEvent() {
            if(context.getTitle().toString().equals("Yunba - Connected")){
                Map<String, String> userInfo = YunBaManager.getUserInfo(context);
                url =userInfo.get("BrokerURL");
                uIDStr=userInfo.get("UserName");
                passwordStr=userInfo.get("Password");
                clientIDStr=userInfo.get("ClientID");
                deviceIdStr=userInfo.get("DeviceID");

                brokerUrl.setText(url);
                uID.setText(uIDStr);
                password.setText(passwordStr);
                clientID.setText(clientIDStr);
                deviceID.setText(deviceIdStr);

                copyToClipboard.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    copy();
                    dismiss();
                    }
                });
            }else{
                copyToClipboard.setText("Getting Information Failure");
                copyToClipboard.setTextColor(context.getResources().getColor(R.color.yunba_screen_disconnect));

                copyToClipboard.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dismiss();
                    }
                });
                DemoUtil.showToast("Failed to get information due to connection failure ",context);
            }
        }

        private void copy() {
            String str="{\n"
                    +"  Broker IP/Port:"+ url +"\n"
                    +"  UID:"+uIDStr+"\n"
                    +"  Password:"+passwordStr+"\n"
                    +"  ClientID:"+clientIDStr+"\n"
                    +"  DeviceID:"+deviceIdStr+"\n"
                    +"  PackageName:"+context.getPackageName()+"\n"
                    +"  AppKey:"+DemoUtil.getAppKey(context)+"\n"
                    +"  SecretKey:"+DemoUtil.getSecretKey(context)+"\n"
                    +"}";

            ClipboardManager cmb = (ClipboardManager)context.getSystemService(Context.CLIPBOARD_SERVICE);
            cmb.setText(str.trim());
            DemoUtil.showToast("The information have been copied to the clipboard",context);
            DemoUtil.printOnScree("copy to clipboard successfully.", DemoUtil.USERLOG);
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
    }

}
