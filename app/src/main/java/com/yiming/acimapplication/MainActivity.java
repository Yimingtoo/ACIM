package com.yiming.acimapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    int firstPermission;
    int NO_PERMISSION = 0;
    int BATTERY_OPTIMIZATION = 1;
    int OVERLAY_PERMISSION = 2;
    int CHECK_PERMISSION = 3;

    private ACIMUtils.LogThread logThread;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        firstPermission = this.NO_PERMISSION;
        // 设置状态栏颜色
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.blue2));

        try {
            logThread = new ACIMUtils.LogThread() {
                @Override
                public void runInsert() {
                    super.runInsert();
                    finish();
                }
            };
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (isFirstRun()) {
            obtainFirstPermission();
        }
        initEvent();

    }

    @Override
    protected void onResume() {
        super.onResume();
        obtaionPermissionOnResume();
        Log.d(TAG, "onResume " + logThread.isAlive() + " " + logThread.isInterrupted());
        if (!logThread.isAlive()) {
            logThread.start();
        }
        new ACIMUtils.CountDown(700) {
            @Override
            public void onFinish() {
                super.onFinish();
                Log.d(TAG, "onResume 倒计时结束");
                Intent intent = getIntent();
                if ("text/plain".equals(intent.getType())) {
                    String text = intent.getStringExtra(Intent.EXTRA_TEXT);
                    System.out.println("text/plain " + text);
                    if (text != null && text.equals("CimTileService")) {
                        // TODO: 处理线程
                        Log.d(TAG, "处理线程");
                        logThread.setCloseFlag(true);

                    }
                }
            }
        }.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        logThread.interrupt();
        logThread.setCloseFlag(false);
    }

    public void initEvent() {
        ImageButton image_button1 = findViewById(R.id.image_button);
        image_button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "image_button1 was clicked");
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showInputMethodPicker();
            }
        });
    }

    private boolean isFirstRun() {
        SharedPreferences sharedPreferences = getSharedPreferences("FirstRun", 0);
        boolean first_run = sharedPreferences.getBoolean("First", true);
        if (first_run) {
            sharedPreferences.edit().putBoolean("First", false).commit();
            // Toast.makeText(this, "第一次", Toast.LENGTH_LONG).show();
            return true;
        } else {
            // Toast.makeText(this, "不是第一次", Toast.LENGTH_LONG).show();
            return false;
        }
    }

    private void obtainFirstPermission() {
        Log.d(TAG, "Auto start: "
                + "\n isAutoStartPermissionGranted: " + ACIMUtils.isAutoStartPermissionGranted(this)
                + "\n isIgnoringBatteryOptimizations: " + ACIMUtils.isIgnoringBatteryOptimizations(this)
                + "\n isOverlayPermissionGranted: " + ACIMUtils.isOverlayPermissionGranted(this)
        );

        // 首次启动申请权限
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle("权限申请")
                .setMessage("为了更好的用户体验，请按照引导开启所需权限(◍•ᴗ•◍)")
                .setIcon(R.mipmap.ic_launcher)
                .setPositiveButton("确定", (dialogInterface, i) -> {
                    firstPermission = this.BATTERY_OPTIMIZATION;
                    Toast.makeText(MainActivity.this, "获取自启动权限", Toast.LENGTH_SHORT).show();
                    try {
                        startActivity(ACIMUtils.getAutostartSettingIntent(MainActivity.this));
                    } catch (Exception e) {
                        Toast.makeText(MainActivity.this, "请手动开启自启动权限", Toast.LENGTH_SHORT).show();
                        obtaionPermissionOnResume();
                    }

                })
                .setNegativeButton("后续设置", (dialogInterface, i) -> {
                    Toast.makeText(MainActivity.this, "跳过申请权限", Toast.LENGTH_SHORT).show();
                })
                .create();
        alertDialog.show();

    }

    public void obtaionPermissionOnResume() {
        if (firstPermission == this.BATTERY_OPTIMIZATION) {
            Toast.makeText(MainActivity.this, "请选择“无限制”运行", Toast.LENGTH_SHORT).show();
            if (!ACIMUtils.isIgnoringBatteryOptimizations(this)) {
                ACIMUtils.requestIgnoreBatteryOptimizations(this);
            }
            firstPermission = this.OVERLAY_PERMISSION;
        } else if (firstPermission == this.OVERLAY_PERMISSION) {
            Toast.makeText(MainActivity.this, "请务必允许此权限！", Toast.LENGTH_SHORT).show();
            if (!ACIMUtils.isOverlayPermissionGranted(this)) {
                ACIMUtils.RequestOverlayPermission(this);
            }
            firstPermission = this.CHECK_PERMISSION;
        } else if (firstPermission == this.CHECK_PERMISSION) {
            if (!ACIMUtils.isOverlayPermissionGranted(this)) {
                Toast.makeText(MainActivity.this, "请手动开启悬浮窗权限！", Toast.LENGTH_SHORT).show();
            }
            firstPermission = this.NO_PERMISSION;
        }
    }
}