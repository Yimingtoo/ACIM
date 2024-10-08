package com.yiming;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.StatusBarManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;
import android.view.inputmethod.InputMethodSubtype;

import java.lang.reflect.Method;
import java.util.List;

public class MainActivity2 extends AppCompatActivity {

    private static final String TAG = "my_test";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().setStatusBarColor(0);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        inputMethods();
    }

    // 获取输入法相关
    public void inputMethods() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        // 取得当前所有的输入法
        List<InputMethodInfo> infos = imm.getInputMethodList();
        for (InputMethodInfo info : infos) {
            System.out.println("输入法包名：" + info.getPackageName()+"\t"+info.getServiceName()+"\t"+info.loadLabel(getPackageManager()));
        }

        //取得当前所有的输入法
        System.out.println("\n当前输入法数量：" + infos.size());
        for (InputMethodInfo info : infos)
        {
            System.out.println("\n输入法包名：" + info.getPackageName());

            int sum = info.getSubtypeCount();
            System.out.println("输入法子类型数量：" + sum);

            for (int i = 0; i < sum; i++)
            {
                // 取得输入法中包含的每一个子类型
                final InputMethodSubtype subtype = info.getSubtypeAt(i);
                // 子类型的语言环境
                final String locale = subtype.getLocale().toString();
                System.out.println("\n子类型语言环境：  " + locale + "  ，hashcode："
                        + subtype.hashCode());
            }
        }

    }
    public void onButton1Click(View view) {
        String str =  Settings.Secure.getString(getContentResolver(),Settings.Secure.SELECTED_INPUT_METHOD_SUBTYPE);
        System.out.println(str);
    }
    public void onButton2Click(View view) {
        Settings.Secure.putString(getContentResolver(),Settings.Secure.DEFAULT_INPUT_METHOD,"com.android.inputmethod.latin/.LatinIME");
    }
    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart");
    }



}