package com.yiming.acimapplication;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;

public class CimTileService extends TileService {

    private static final String TAG = "CimTileService";

    @Override
    public void onCreate() {
        super.onCreate();
        Tile tile = getQsTile();
        if (tile != null) {
            // 更新Tile的状态
            tile.setState(Tile.STATE_INACTIVE);
            tile.setIcon(Icon.createWithResource(this, R.drawable.ic_keyboard));
            tile.updateTile();
        }
    }

    @Override
    public void onClick() {
        super.onClick();
        Log.d(TAG,"CimTileService was clicked");

        // 创建一个 Intent，指定点击通知后的行为
        Intent intent = new Intent(this, MainActivity.class);
        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, "CimTileService");
        intent.setType("text/plain");
        // 根据 API 版本选择标志
        int flags = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
                ? PendingIntent.FLAG_IMMUTABLE
                : PendingIntent.FLAG_UPDATE_CURRENT;
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, flags);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startActivityAndCollapse(pendingIntent);
        }

        // 延迟500ms再打开输入法
        new ACIMUtils.CountDown(500){
            @Override
            public void onFinish() {
                super.onFinish();
                Log.d(TAG,"倒计时结束");
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showInputMethodPicker();
            }
        }.start();

    }

}
