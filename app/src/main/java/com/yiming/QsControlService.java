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
import android.graphics.Rect;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.inputmethod.InputMethodManager;

import androidx.core.app.NotificationCompat;

import java.lang.reflect.Method;
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
    public void onStartListening() {

        super.onStartListening();
        createAndShowNotification(this);
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
        System.out.println("QsControlService click");
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        this.startActivity(intent);
    }

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
        // 根据 API 版本选择标志
        int flags = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
                ? PendingIntent.FLAG_IMMUTABLE
                : PendingIntent.FLAG_UPDATE_CURRENT;
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, flags);
        return pendingIntent;
    }



}