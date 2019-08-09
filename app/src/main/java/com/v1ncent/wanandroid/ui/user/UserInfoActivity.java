package com.v1ncent.wanandroid.ui.user;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.flaviofaria.kenburnsview.KenBurnsView;
import com.flaviofaria.kenburnsview.RandomTransitionGenerator;
import com.r0adkll.slidr.Slidr;
import com.v1ncent.wanandroid.R;
import com.v1ncent.wanandroid.base.AppActivity;
import com.v1ncent.wanandroid.utils.BarUtils;
import com.v1ncent.wanandroid.utils.LogUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * ================================================
 * 作    者：v1ncent
 * 版    本：1.0.0
 * 创建日期：2018/3/28
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class UserInfoActivity extends AppActivity {
    @BindView(R.id.kenBurnsView)
    KenBurnsView kenBurnsView;
    @BindView(R.id.rootLayout)
    LinearLayout rootLayout;
    @BindView(R.id.toolbar_title)
    TextView toolbarTitle;
    @BindView(R.id.user_info_title)
    LinearLayout userInfoTitle;
    @BindView(R.id.user_name)
    TextView userName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // transparent status bar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS |
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_userinfo);
        ButterKnife.bind(this);

        initView();
    }

    private void initView() {
        Slidr.attach(this);
        LogUtils.i("getStatusBarHeight", BarUtils.getStatusBarHeight());
        userInfoTitle.setPadding(0, BarUtils.getStatusBarHeight(), 0, 0);
        BarUtils.setStatusBarAlpha(this, 0);
        RandomTransitionGenerator generator = new RandomTransitionGenerator(8000, new LinearInterpolator());
        kenBurnsView.setTransitionGenerator(generator);
        Glide.with(this).load(R.drawable.splash_bg).into(kenBurnsView);
    }

    @Override
    public void onClickListener(View v) {

    }
}
