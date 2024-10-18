package com.yiming.acimapplication.Tutorial;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.yiming.acimapplication.R;

public class TutorialActivity extends AppCompatActivity {

    ViewPager2 viewPager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

        // 设置状态栏颜色
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.blue2));

        viewPager = findViewById(R.id.viewPager);
        TutorialAdapter tutorialAdapter = new TutorialAdapter();
        viewPager.setAdapter(tutorialAdapter);
        viewPager.setCurrentItem(0);
    }


    public void onImageButtonBackClicked(View view) {
        if (viewPager.getCurrentItem() < 3) {
            viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
        } else {
            finish();
        }
    }
}
