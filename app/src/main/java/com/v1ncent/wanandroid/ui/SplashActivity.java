package com.v1ncent.wanandroid.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.flaviofaria.kenburnsview.KenBurnsView;
import com.flaviofaria.kenburnsview.RandomTransitionGenerator;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.v1ncent.wanandroid.AppHome;
import com.v1ncent.wanandroid.R;
import com.v1ncent.wanandroid.api.JsonCallback;
import com.v1ncent.wanandroid.api.Urls;
import com.v1ncent.wanandroid.base.AppActivity;
import com.v1ncent.wanandroid.db.LitePalUtils;
import com.v1ncent.wanandroid.ui.user.pojo.UserBean;
import com.v1ncent.wanandroid.utils.ActivityUtils;
import com.v1ncent.wanandroid.utils.Constant;
import com.v1ncent.wanandroid.utils.LogUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * ================================================
 * 作    者：v1ncent
 * 版    本：1.0.0
 * 创建日期：2018/3/27
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class SplashActivity extends AppActivity {
    @BindView(R.id.splash_root_layout)
    RelativeLayout mRootLayout;
    @BindView(R.id.splash_bg_img)
    KenBurnsView mBgImg;
    @BindView(R.id.splash_logo_img)
    AppCompatImageView mLogoImg;
    @BindView(R.id.splash_app_name_text)
    TextView mAppNameText;
    @BindView(R.id.splash_app_slogan_text)
    TextView mAppSloganText;

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
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);

        initView();
    }

    private void initView() {
        RandomTransitionGenerator generator = new RandomTransitionGenerator(5000, new LinearInterpolator());
        mBgImg.setTransitionGenerator(generator);
        Glide.with(this).load(R.drawable.splash_bg).into(mBgImg);
        mRootLayout.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View view, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7) {
                mRootLayout.removeOnLayoutChangeListener(this);

                final int transX = mAppSloganText.getMeasuredWidth() >> 1;
                final int transY = mAppSloganText.getMeasuredHeight();

                mLogoImg.animate()
                        .rotation(-90)
                        .translationX(-transX)
                        .setDuration(500)
                        .setStartDelay(500)
                        .setInterpolator(new LinearInterpolator());

                mAppNameText.setAlpha(0);
                mAppNameText.animate()
                        .alpha(1)
                        .translationX(transX)
                        .setDuration(500)
                        .setStartDelay(500)
                        .setInterpolator(new LinearInterpolator());

                mAppSloganText.setAlpha(0);
                mAppSloganText.animate()
                        .translationX(transX)
                        .setDuration(500)
                        .setStartDelay(500)
                        .setInterpolator(new LinearInterpolator())
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                mAppNameText.animate()
                                        .translationY(-transY >> 1)
                                        .setDuration(500)
                                        .setInterpolator(new LinearInterpolator());

                                mAppSloganText.animate()
                                        .alpha(1)
                                        .translationY(transY)
                                        .setDuration(500)
                                        .setInterpolator(new LinearInterpolator())
                                        .setListener(new AnimatorListenerAdapter() {
                                            @Override
                                            public void onAnimationEnd(Animator animation) {
                                                jumpToMain();
                                            }
                                        });
                            }
                        });
            }
        });

    }

    private void jumpToMain() {
        UserBean userBean = LitePalUtils.getInstance().getUserInfo();
        if (userBean != null) {
            LogUtils.i("userBean", userBean.toString());
            Constant.USERANME = userBean.getData().getUsername();
            Constant.PASSWORD = userBean.getData().getPassword();
            LogUtils.i("user_cookie", Constant.USERANME + " " + Constant.PASSWORD);
            if (!TextUtils.isEmpty(Constant.USERANME)
                    && !TextUtils.isEmpty(Constant.PASSWORD)) {
                //登录
                login(Constant.USERANME, Constant.PASSWORD);
            } else {
                intoAPP();
            }
        } else {
            intoAPP();
        }
    }

    //自动登录
    private void login(String userName, String passWord) {
        OkGo.<UserBean>post(Urls.LOGIN)
                .tag(this)
                .params("username", userName)
                .params("password", passWord)
                .execute(new JsonCallback<UserBean>() {
                    @Override
                    public void onSuccess(Response<UserBean> response) {
                        UserBean loginResultBean = response.body();
                        LogUtils.i("login-->", loginResultBean.getData() != null ? loginResultBean.toString() : "null");
                        if (loginResultBean.getErrorCode() == 0) {
                            Constant.ISLOGINED = true;
                        }
                    }

                    @Override
                    public void onError(Response<UserBean> response) {
                    }

                    @Override
                    public void onFinish() {
                        intoAPP();
                    }
                });
    }

    @Override
    public void onClickListener(View v) {

    }

    //进入APP
    private void intoAPP() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ActivityUtils.startActivity(AppHome.class);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            }
        }, 500);
    }
}
