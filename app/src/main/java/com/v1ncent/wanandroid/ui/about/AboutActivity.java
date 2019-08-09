package com.v1ncent.wanandroid.ui.about;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.r0adkll.slidr.Slidr;
import com.v1ncent.wanandroid.R;
import com.v1ncent.wanandroid.base.AppActivity;
import com.v1ncent.wanandroid.utils.AppUtils;
import com.v1ncent.wanandroid.utils.BarUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.v1ncent.wanandroid.R.id.scroll_about;
import static com.v1ncent.wanandroid.R.id.tv_about_version;

/**
 * Created by v1ncent on 2018/3/15.
 */

public class AboutActivity extends AppActivity {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.img_card_about_1)
    ImageView imgCardAbout1;
    @BindView(R.id.tv_card_about_1_1)
    TextView tvCardAbout11;
    @BindView(tv_about_version)
    TextView tvAboutVersion;
    @BindView(R.id.tv_card_about_2_1)
    TextView tvCardAbout21;
    @BindView(R.id.view_card_about_2_line)
    View viewCardAbout2Line;
    @BindView(R.id.ll_card_about_2_shop)
    LinearLayout llCardAbout2Shop;
    @BindView(R.id.ll_card_about_2_email)
    LinearLayout llCardAbout2Email;
    @BindView(R.id.ll_card_about_2_git_hub)
    LinearLayout llCardAbout2GitHub;
    @BindView(R.id.ll_card_about_2_location)
    LinearLayout llCardAbout2Location;
    @BindView(R.id.card_about_2)
    CardView cardAbout2;
    @BindView(scroll_about)
    ScrollView scrollAbout;
    @BindView(R.id.fab_about_share)
    FloatingActionButton fabAboutShare;
    @BindView(R.id.rootLayout)
    FrameLayout rootLayout;

    private AboutActivity mContext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);
        mContext = this;
        initView();
    }

    private void initView() {
        Slidr.attach(this);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            // Enable the Up button
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.title_about);
        BarUtils.setStatusBarColor(this, ContextCompat.getColor(mContext, R.color.colorPrimaryDark), 24);
        BarUtils.addMarginTopEqualStatusBarHeight(rootLayout);

        Animation animation = AnimationUtils.loadAnimation(this, R.anim.anim_about_card_show);
        scrollAbout.startAnimation(animation);

        AlphaAnimation alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
        alphaAnimation.setDuration(300);
        alphaAnimation.setStartOffset(600);

        tvAboutVersion.setText(getString(R.string.about_version) + " " + AppUtils.getAppInfo().getVersionName());
        tvAboutVersion.startAnimation(alphaAnimation);

    }

    @OnClick({R.id.fab_about_share})
    public void onClickListener(View v) {
        switch (v.getId()) {
            case R.id.fab_about_share:
                showInfo("share");
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }
}
