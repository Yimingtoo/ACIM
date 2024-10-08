package com.yiming.acimapplication;

import static androidx.core.content.ContextCompat.startActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.PowerManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.IOException;
import java.io.InputStream;

public class ACIMUtils {
    private static final String TAG = "ACIMUtils";

    /**
     * 计时器
     */
    public static class CountDown extends android.os.CountDownTimer {
        public CountDown(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        public CountDown(long millisInFuture) {
            super(millisInFuture, 1000);
        }

        @Override
        public void onTick(long millisUntilFinished) {

        }

        @Override
        public void onFinish() {

        }
    }


    public static boolean isAutoStartPermissionGranted(Context context) {
        String packageName = context.getPackageName();
        String autoStartSetting = Settings.ACTION_APPLICATION_DETAILS_SETTINGS;
        Intent intent = new Intent(autoStartSetting, Uri.parse("package:" + packageName));
        ResolveInfo resolveInfo = context.getPackageManager().resolveActivity(intent, 0);
        return !(TextUtils.isEmpty(packageName) || resolveInfo == null || resolveInfo.activityInfo == null);
    }


    /**
     * 获取自启动管理页面的Intent
     *
     * @param context context
     * @return 返回自启动管理页面的Intent
     */
    public static Intent getAutostartSettingIntent(Context context) {
        ComponentName componentName = null;
        String brand = Build.MANUFACTURER;
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        switch (brand.toLowerCase()) {
            case "samsung"://三星
                componentName = new ComponentName("com.samsung.android.sm", "com.samsung.android.sm.app.dashboard.SmartManagerDashBoardActivity");
                break;
            case "huawei"://华为
                //荣耀V8，EMUI 8.0.0，Android 8.0上，以下两者效果一样
                componentName = new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.appcontrol.activity.StartupAppControlActivity");
                break;
            case "xiaomi"://小米
                componentName = new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity");
                break;
            case "vivo"://VIVO
                componentName = new ComponentName("com.iqoo.secure", "com.iqoo.secure.ui.phoneoptimize.AddWhiteListActivity");
                break;
            case "oppo"://OPPO
                componentName = new ComponentName("com.coloros.oppoguardelf", "com.coloros.powermanager.fuelgaue.PowerUsageModelActivity");
                break;
            case "yulong":
            case "360"://360
                componentName = new ComponentName("com.yulong.android.coolsafe", "com.yulong.android.coolsafe.ui.activity.autorun.AutoRunListActivity");
                break;
            case "meizu"://魅族
                componentName = new ComponentName("com.meizu.safe", "com.meizu.safe.permission.SmartBGActivity");
                break;
            case "oneplus"://一加
                componentName = new ComponentName("com.oneplus.security", "com.oneplus.security.chainlaunch.view.ChainLaunchAppListActivity");
                break;
            case "letv"://乐视
                intent.setAction("com.letv.android.permissionautoboot");
            default://其他
                intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                intent.setData(Uri.fromParts("package", context.getPackageName(), null));
                break;
        }
        intent.setComponent(componentName);
        return intent;
    }

    public static boolean isIgnoringBatteryOptimizations(Context context) {
        String packageName = context.getPackageName();
        boolean isAutoStartup = false;
        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        if (powerManager != null && powerManager.isIgnoringBatteryOptimizations(packageName)) {
            isAutoStartup = true;
        }
        return isAutoStartup;
    }

    public static void requestIgnoreBatteryOptimizations(Context context) {
        try {
            @SuppressLint("BatteryLife") Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
            intent.setData(Uri.parse("package:" + context.getPackageName()));
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isOverlayPermissionGranted(Context context) {
        return Settings.canDrawOverlays(context);
    }

    private static final int REQUEST_OVERLAY = 5004;

    public static void RequestOverlayPermission(Activity instance) {
        String ACTION_MANAGE_OVERLAY_PERMISSION = "android.settings.action.MANAGE_OVERLAY_PERMISSION";
        Intent intent = new Intent(ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + instance.getPackageName()));
        instance.startActivityForResult(intent, REQUEST_OVERLAY);
    }


    public static class LogThread extends Thread {

        Process process;
        final InputStream is;

        public boolean closeFlag = false;

        public LogThread() throws IOException {
            // 小米专用
            process = Runtime.getRuntime().exec("logcat -s HandWritingStubImpl");
            is = process.getInputStream();
        }

        @Override
        public void run() {
            Log.d(TAG, "Thread start");
            byte[] buffer = new byte[1024];
            long len = 0;
            String rec_str = "";
            while (true) {
                try {
                    len = is.read(buffer);
                    System.out.println(rec_str);
                    rec_str = new String(buffer);
                    // 逃课监测log
                    if (rec_str.contains("getCurrentKeyboardType") && closeFlag) {
                        runInsert();
                    }
                    if (-1 == len) {
                        break;
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        public void setCloseFlag(boolean closeFlag1) {
            closeFlag = closeFlag1;
        }

        public void runInsert() {
            Log.d(TAG, "runInsert");
        }

    }
}
