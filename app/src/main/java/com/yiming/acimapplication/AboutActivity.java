package com.yiming.acimapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class AboutActivity extends AppCompatActivity {
    private static final String TAG = "AboutActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.blue2));

    }

    public void onBiliClicked(View view) {
        Log.d(TAG, "bili was clicked");
        Uri uri = Uri.parse("https://space.bilibili.com/434913626");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }
    public void onGithubClicked(View view) {
        Log.d(TAG, "github was clicked");
        Uri uri = Uri.parse("https://github.com/Yimingtoo/ACIM");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }
    public void onGiteeClicked(View view) {
        Log.d(TAG, "gitee was clicked");
        Uri uri = Uri.parse("https://gitee.com/yimingtoo");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

}
