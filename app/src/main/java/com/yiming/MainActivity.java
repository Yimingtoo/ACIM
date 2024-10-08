package com.yiming;


import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.IntentFilter;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowMetrics;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.content.Intent;
import android.widget.ImageButton;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import android.app.Notification;
import android.app.NotificationChannel;
import android.widget.LinearLayout;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener {
    private static final String TAG = "my_test1";
    private final int NOT_CONNECT = 0;

    private boolean finishFlag = false;
    public LogThread logThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.blue2));

        try {
            logThread = new LogThread(){
                @Override
                public void runInsert() {
                    super.runInsert();
                    finish();
                }
            };
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        getPermission();
        initEvent();
//        imPop();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        System.out.println("onTouch" + event.getX() + "," + event.getY());
        finish();
        return false;
    }

    public boolean logFlag = false;

    @SuppressLint("ClickableViewAccessibility")
    public void initEvent() {
        LinearLayout lTouch = (LinearLayout) findViewById(R.id.l_touch);
        lTouch.setOnTouchListener(this);

        Button button1 = findViewById(R.id.button);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("button1");
//                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//                imm.showInputMethodPicker();
//                System.out.println(getWindow().getDecorView().getParent()!= null);
//                System.out.println(imm.isActive());

            }
        });
        ImageButton image_button1 = findViewById(R.id.image_button);
        image_button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("button1");
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showInputMethodPicker();
                System.out.println(imm.isActive());
            }
        });
        Button button2 = findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("button2");
                int[] point = new int[2];
                System.out.println("point：" + view.getX() + "," + view.getY());
            }
        });


        button1.setVisibility(View.INVISIBLE);
        button2.setVisibility(View.INVISIBLE);

    }





    /**
     * 判断当前电源策略
     */
    public void getPermission() {
        System.out.println("isIgnoringBatteryOptimizations" + isIgnoringBatteryOptimizations());
        // 获得后台工作和自启动权限
        if (!isIgnoringBatteryOptimizations()) {
            System.out.println("没有在白名单");
            // 后台保活
            requestIgnoreBatteryOptimizations();
            Intent intent;
            try {
                String manufacturer = Build.MANUFACTURER;
                // 自启动
                if ("Xiaomi".equalsIgnoreCase(manufacturer)) {
                    intent = new Intent();
                    intent.setComponent(new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity"));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
                else if ("HUAWEI".equalsIgnoreCase(manufacturer)) {
                    intent = new Intent();
                    intent.setComponent(new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.process.ProtectActivity"));
                    startActivity(intent);
                }
            } catch (Exception e) {
                System.out.println("不支持的设备，请手动开启自启动权限");
                Toast.makeText(this, "不支持的设备，请手动开启自启动权限", Toast.LENGTH_SHORT).show();
            }


        } else {
            System.out.println("在白名单");

        }

        RequestOverlayPermission(this);

        checkAndRequestNotificationPermission();


    }

    /**
     * 判断当前电源策略
     */
    @SuppressLint("ObsoleteSdkInt")
    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean isIgnoringBatteryOptimizations() {
        boolean isIgnoring = false;
        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        if (powerManager != null) {
            isIgnoring = powerManager.isIgnoringBatteryOptimizations(getPackageName());
        }
        return isIgnoring;
    }

    /**
     * 跳转 BatteryLife
     */
    @SuppressLint("ObsoleteSdkInt")
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void requestIgnoreBatteryOptimizations() {
        try {
            @SuppressLint("BatteryLife") Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
            intent.setData(Uri.parse("package:" + getPackageName()));
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static boolean CanShowFloat = false;

    private static final int REQUEST_OVERLAY = 5004;

    /**
     * 动态请求悬浮窗权限
     */
    public static void RequestOverlayPermission(Activity Instatnce) {
        if (Build.VERSION.SDK_INT >= 23) {
            if (!Settings.canDrawOverlays(Instatnce)) {
                String ACTION_MANAGE_OVERLAY_PERMISSION = "android.settings.action.MANAGE_OVERLAY_PERMISSION";
                Intent intent = new Intent(ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + Instatnce.getPackageName()));

                Instatnce.startActivityForResult(intent, REQUEST_OVERLAY);
            } else {
                CanShowFloat = true;
            }
        }
    }

    /**
     * 监听键盘是否弹出
     */
    public void imPop() {

        View rootView = getWindow().getDecorView().getRootView();
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                System.out.println("onGlobalLayout");
                // 在这里处理软键盘的弹出和隐藏事件
                Rect r = new Rect();
                rootView.getWindowVisibleDisplayFrame(r);
                int screenHeight = rootView.getRootView().getHeight();
                int keypadHeight = screenHeight - r.bottom;

                if (keypadHeight > screenHeight * 0.15) {
                    // 软键盘弹出
                    System.out.println("软键盘弹出");
                } else {
                    // 软键盘隐藏
                    System.out.println("软键盘隐藏");
                }
            }
        });

    }


    /**
     * 检查并请求通知权限
     */
    private static final String CHANNEL_ID = "test_channel";

    private void checkAndRequestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (!NotificationManagerCompat.from(this).areNotificationsEnabled()) {
                Intent intent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
                intent.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
                intent.putExtra(Settings.EXTRA_CHANNEL_ID, CHANNEL_ID);
                startActivityForResult(intent, 1);
            }
        }
    }

    public boolean isDialogShowing() {
        System.out.println(getWindow().getContext().getClass().getName());
        return false;
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        logThread.start();


        CountDownTimer countDownTimer = new CountDownTimer(700, 100) {
            @Override
            public void onTick(long millisUntilFinished) {
                // 每次倒计时时执行的代码
//                System.out.println("倒计时中...剩余时间：" + millisUntilFinished + " ms");
            }

            @Override
            public void onFinish() {
                // 倒计时结束时执行的代码
                System.out.println("onResume 倒计时结束");
                Intent intent = getIntent();
                if ("text/plain".equals(intent.getType())) {
                    String text = intent.getStringExtra(Intent.EXTRA_TEXT);
                    System.out.println("text/plain " + text);
                    if (text != null && text.equals("notification")) {
                        logThread.setCloseFlag(true);
                    }
                }


            }
        };
        // 启动计时器
        countDownTimer.start();
    }


    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
        logThread.interrupt();
        logThread.setCloseFlag(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart");
    }


}