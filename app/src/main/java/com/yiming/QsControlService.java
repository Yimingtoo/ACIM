package com.yiming;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Icon;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;

import androidx.core.app.NotificationCompat;


public class QsControlService extends TileService {

    @Override
    public void onStartListening() {

        super.onStartListening();
        Tile tile = getQsTile();
        if (tile != null) {
            System.out.println("its not null");
            // 更新Tile的状态
            // tile.setState(Tile.STATE_ACTIVE);
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
        Intent intent = new Intent(this,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        this.startActivity(intent);



//        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm.showInputMethodPicker();
    }




}