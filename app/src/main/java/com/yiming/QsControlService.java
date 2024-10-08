package com.yiming;


import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Instrumentation;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.StatusBarManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Rect;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import android.view.inputmethod.InputMethodManager;

import java.lang.reflect.InvocationTargetException;
import java.util.List;


public class QsControlService extends TileService {


    @Override
    public void onCreate() {
        super.onCreate();
        Tile tile = getQsTile();
        if (tile != null) {
            System.out.println("its not null");
            // 更新Tile的状态
            // tile.setState(Tile.STATE_ACTIVE);
            tile.setState(Tile.STATE_INACTIVE);
            tile.setIcon(Icon.createWithResource(this, R.drawable.ic_keyboard));
            tile.updateTile();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onStartListening() {

        super.onStartListening();
//        createAndShowNotification(this);
    }

    public void refresh() {
        final int state;
        state = Tile.STATE_ACTIVE;
        getQsTile().setState(state);
        getQsTile().updateTile();
    }

    @Override
    public void onClick() {
        super.onClick();
        System.out.println("QsControlService click " + isRunningForeground(this));
//        Intent intent = new Intent(this, MainActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivityAndCollapse(intent);
//        System.out.println();;

        PendingIntent pendingIntent = getPendingIntent(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startActivityAndCollapse(pendingIntent);
        }


        CountDownTimer countDownTimer = new CountDownTimer(500, 100) {
            @Override
            public void onTick(long millisUntilFinished) {
                // 每次倒计时时执行的代码
//                System.out.println("倒计时中...剩余时间：" + millisUntilFinished + " ms");
            }

            @Override
            public void onFinish() {
                // 倒计时结束时执行的代码
                System.out.println("倒计时结束");
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showInputMethodPicker();

            }
        };
        // 启动计时器
        countDownTimer.start();

    }

    /**
     * 获取栈顶Activity
     */
    public static ComponentName getTopActivity(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager != null) {
            List<ActivityManager.RunningTaskInfo> taskInfo = activityManager.getRunningTasks(1);
            if (taskInfo != null && !taskInfo.isEmpty()) {
                return taskInfo.get(0).topActivity;
            }
        }
        return null;
    }


    private static final String CHANNEL_ID = "test_channel";
    private static final String CHANNEL_NAME = "Test Channel";
    private static final int NOTIFICATION_ID = 1;

    public static void createAndShowNotification(Context context) {
        // 创建 NotificationManager
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // 如果是 Android Oreo 及以上版本，需要创建通知渠道
        CharSequence name = CHANNEL_NAME;
        String description = "This is an example channel for notifications.";
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_HIGH);
        channel.setDescription(description);
        notificationManager.createNotificationChannel(channel);

        // 构建 Notification 对象
        Notification notification = new Notification.Builder(context, CHANNEL_ID)
                .setContentTitle("这是一个通知标题")
                .setContentText("这是一个通知内容")
                .setSmallIcon(R.drawable.ic_keyboard) // 使用你的图标资源
                .setContentIntent(getPendingIntent(context))
                .setOngoing(true)
                .setAutoCancel(true)
                .setTimeoutAfter(3000)

                .build();

        // 发布通知
        notificationManager.notify(NOTIFICATION_ID, notification);
    }

    private static PendingIntent getPendingIntent(Context context) {
        // 创建一个 Intent，指定点击通知后的行为
        Intent intent = new Intent(context, MainActivity.class);
        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, "notification");
        intent.setType("text/plain");
        // 根据 API 版本选择标志
        int flags = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
                ? PendingIntent.FLAG_IMMUTABLE
                : PendingIntent.FLAG_UPDATE_CURRENT;
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, flags);
        return pendingIntent;
    }

    /**
     * 判断本应用是否已经位于最前端：已经位于最前端时，返回 true；否则返回 false
     */
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


}