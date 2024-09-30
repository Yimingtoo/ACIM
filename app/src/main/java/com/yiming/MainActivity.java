package com.yiming;


import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.content.Intent;

import java.util.List;


public class MainActivity extends AppCompatActivity {
    private final int NOT_CONNECT = 0;

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initEvent();

        getPermission();
    }

    public void initEvent() {
        Button button1 = findViewById(R.id.button);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("button1");

            }
        });
        Button button2 = findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("button2");

                QsControlService qsControlService = new QsControlService();
                qsControlService.onStartListening();
            }
        });

    }


    @Override
    protected void onResume() {
        super.onResume();
    }


    //判断本应用是否已经位于最前端：已经位于最前端时，返回 true；否则返回 false
    public static boolean isRunningForeground(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcessInfoList = activityManager.getRunningAppProcesses();

        for (ActivityManager.RunningAppProcessInfo appProcessInfo : appProcessInfoList) {
            if (appProcessInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
                    && appProcessInfo.processName.equals(context.getApplicationInfo().processName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断当前电源策略
     */
    public void getPermission() {
        System.out.println("isIgnoringBatteryOptimizations" + isIgnoringBatteryOptimizations());
        if (!isIgnoringBatteryOptimizations()) {
            System.out.println("没有在白名单");
            // 后台保活
            requestIgnoreBatteryOptimizations();

            // 自启动
            Intent intent = new Intent();
            intent.setComponent(new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity"));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

        } else {
            System.out.println("在白名单");
        }
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

}