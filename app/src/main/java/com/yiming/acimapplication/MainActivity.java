package com.yiming.acimapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.yiming.acimapplication.Tutorial.TutorialActivity;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    int firstPermission;
    int NO_PERMISSION = 0;
    int BATTERY_OPTIMIZATION = 1;
    int OVERLAY_PERMISSION = 2;
    int CHECK_PERMISSION = 3;

    boolean backFlag = false;
    boolean isFromCimTileService = false;

    InputMethodManager imm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        firstPermission = this.NO_PERMISSION;

        // 设置状态栏颜色
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.blue2));


        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        if (isFirstRun()) {
//            obtainFirstPermission();
            showTutorialAlertDialog();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
//        obtainPermissionOnResume();
        SharedPreferences sharedPreferences = getSharedPreferences("isFromCimTileService", 0);
        isFromCimTileService = sharedPreferences.getBoolean("key1", false);

        if (isFromCimTileService) {
            sharedPreferences.edit().putBoolean("key1", false).commit();
            new ACIMUtils.CountDown(500) {
                @Override
                public void onFinish() {
                    super.onFinish();
                    imm.showInputMethodPicker();
                }
            }.start();
        }
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop");
        super.onStop();

//        finish();
    }


    public void onImageButtonClicked(View view) {
//        Log.d(TAG, "image_button1 was clicked");
        imm.showInputMethodPicker();
    }

    public void onAboutButtonClicked(View view) {
        Intent intent = new Intent(this, AboutActivity.class);
        startActivity(intent);
    }

    public void onTutorialButtonClicked(View view) {
        Intent intent = new Intent(this, TutorialActivity.class);
        startActivity(intent);
    }

    public void onTestButtonClicked(View view) {

    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
//        Log.d(TAG, "onWindowFocusChanged " + hasFocus);
        if (backFlag) {
            finish();
            backFlag = false;
        } else if (!hasFocus && isFromCimTileService) {
            isFromCimTileService = false;
            backFlag = true;
        }
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

    private void showTutorialAlertDialog() {
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle("提示")
                .setMessage("是否需要快捷开关设置引导(◍•ᴗ•◍)")
                .setIcon(R.mipmap.ic_launcher)
                .setPositiveButton("确定", (dialogInterface, i) -> {
                    Intent intent = new Intent(MainActivity.this, TutorialActivity.class);
                    startActivity(intent);

                })
                .setNegativeButton("不需要", (dialogInterface, i) -> {
                })
                .create();
        alertDialog.show();
    }

    @Deprecated
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
                        obtainPermissionOnResume();
                    }

                })
                .setNegativeButton("后续设置", (dialogInterface, i) -> {
                    Toast.makeText(MainActivity.this, "跳过申请权限", Toast.LENGTH_SHORT).show();
                })
                .create();
        alertDialog.show();

    }

    @Deprecated
    public void obtainPermissionOnResume() {
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