package com.yiming.acimapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    int firstPermission;
    int NO_PERMISSION = 0;
    int BATTERY_OPTIMIZATION = 1;
    int OVERLAY_PERMISSION = 2;
    int CHECK_PERMISSION = 3;

    boolean backFlag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        firstPermission = this.NO_PERMISSION;
        // 设置状态栏颜色
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.blue2));

//        if (isFirstRun()) {
//            obtainFirstPermission();
//        }
        initEvent();

    }

    @Override
    protected void onResume() {
        super.onResume();
//        obtainPermissionOnResume();
        new ACIMUtils.CountDown(700) {
            @Override
            public void onFinish() {
                super.onFinish();
                Intent intent = getIntent();
                if ("text/plain".equals(intent.getType())) {
                    String text = intent.getStringExtra(Intent.EXTRA_TEXT);
                    System.out.println("text/plain " + text);
                    if (text != null && text.equals("CimTileService")) {
                        Log.d(TAG, "处理线程");
                        backFlag = true;
                    }
                }
            }
        }.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }

    @SuppressLint("Recycle")
    public void initEvent() {
        ImageButton image_button1 = findViewById(R.id.image_button);
        image_button1.setOnClickListener(view -> {
            Log.d(TAG, "image_button1 was clicked");
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showInputMethodPicker();
        });
        LinearLayout linearLayout = findViewById(R.id.l_about);
        TextView textView1 = findViewById(R.id.tv);
        TextView textView2 = findViewById(R.id.tv2);
        TextView textView3 = findViewById(R.id.tv3);

        linearLayout.setOnClickListener(v -> {
            Log.d(TAG, "linearLayout was clicked");
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) linearLayout.getLayoutParams();
            ValueAnimator valueAnimator;
            if (params.weight > 2.8f) {
                // 收回关于
                valueAnimator = ValueAnimator.ofFloat(3f, 1f);
                valueAnimator.setDuration(400);
                valueAnimator.addUpdateListener(animation -> {
                    float value = (float) animation.getAnimatedValue();
                    params.weight = value;
                    linearLayout.setLayoutParams(params);
                    LinearLayout.LayoutParams params1 = (LinearLayout.LayoutParams) textView2.getLayoutParams();
                    params1.weight = (value - 1) / 2;
                    textView2.setLayoutParams(params1);
                    textView3.setLayoutParams(params1);
                    textView2.setAlpha((value - 1) / 2);
                    textView3.setAlpha((value - 1) / 2);
                });
                valueAnimator.start();
            }
        });
        textView1.setOnClickListener(v -> {
            Log.d(TAG, "textView was clicked");
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) linearLayout.getLayoutParams();
            ValueAnimator valueAnimator;
            if (params.weight < 1.5f) {
                // 展开关于
                valueAnimator = ValueAnimator.ofFloat(1f, 3f);
                valueAnimator.setDuration(400);
                valueAnimator.addUpdateListener(animation -> {
                    float value = (float) animation.getAnimatedValue();
                    params.weight = value;
                    linearLayout.setLayoutParams(params);
                    LinearLayout.LayoutParams params1 = (LinearLayout.LayoutParams) textView2.getLayoutParams();
                    params1.weight = (value - 1) / 2;
                    textView2.setLayoutParams(params1);
                    textView3.setLayoutParams(params1);
                    textView2.setAlpha((value - 1) / 2);
                    textView3.setAlpha((value - 1) / 2);
                });
                valueAnimator.start();
            }
        });
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        Log.d(TAG, "onWindowFocusChanged " + hasFocus);
        if (backFlag) {
            finish();
            backFlag = false;
        }
    }

    @Deprecated
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