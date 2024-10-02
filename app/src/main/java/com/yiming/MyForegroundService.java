package com.yiming;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import androidx.core.app.NotificationCompat;

public class MyForegroundService extends Service {

    private static final int NOTIFICATION_ID = 1;
    private static final String CHANNEL_ID = "foreground_service_channel";
    private NotificationManager mNotificationManager;

    @Override
    public void onCreate() {
        super.onCreate();
        System.out.println("MyForegroundService was created");
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        createNotificationChannel();
    }

    private void createNotificationChannel() {
        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                "Foreground Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
        );
        mNotificationManager.createNotificationChannel(channel);
    }

    @SuppressLint("ForegroundServiceType")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        System.out.println("MyForegroundService was onStartCommand");

        PendingIntent pendingIntent = getPendingIntent(this);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Foreground Service")
                .setContentText("This is a foreground service.")
                .setSmallIcon(R.drawable.ic_keyboard)
                .setContentIntent(pendingIntent)
                .build();

        startForeground(NOTIFICATION_ID, notification);


        // 启动服务后执行的逻辑
        // ...

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopForeground(true);
        mNotificationManager.cancel(NOTIFICATION_ID);
    }



    private static PendingIntent getPendingIntent(Context context) {
        // 创建一个 Intent，指定点击通知后的行为
        Intent intent = new Intent(context, MainActivity.class);
        // 根据 API 版本选择标志
        int flags = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
                ? PendingIntent.FLAG_IMMUTABLE
                : PendingIntent.FLAG_UPDATE_CURRENT;
        return PendingIntent.getActivity(context, 0, intent, flags| PendingIntent.FLAG_UPDATE_CURRENT);
    }

}
