package com.yiming;

import static com.yiming.MainActivity.isRunningForeground;
import static com.yiming.MainActivity.setTopApp;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Icon;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;

import androidx.annotation.NonNull;

import java.util.List;


public class QsControlService extends TileService {

    @Override
    public void onStartListening() {

        super.onStartListening();
        Tile tile = getQsTile();
//        refresh();
        if (tile != null) {
            System.out.println("its not null");
            // 更新Tile的状态
//            tile.setState(Tile.STATE_ACTIVE);
            tile.setState(Tile.STATE_INACTIVE);
            tile.setIcon(Icon.createWithResource(this, R.drawable.ic_launcher_foreground));
            tile.updateTile();
        }

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
        showActivity();


//        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm.showInputMethodPicker();

    }


    private void showActivity() {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity"));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);


    }
}