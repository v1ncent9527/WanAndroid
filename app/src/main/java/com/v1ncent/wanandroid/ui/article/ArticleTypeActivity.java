package com.v1ncent.wanandroid.ui.article;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.r0adkll.slidr.Slidr;
import com.r0adkll.slidr.model.SlidrConfig;
import com.r0adkll.slidr.model.SlidrPosition;
import com.v1ncent.wanandroid.R;
import com.v1ncent.wanandroid.base.AppActivity;
import com.v1ncent.wanandroid.ui.article.adapter.ArticleTypeFragmentPagerAdapter;
import com.v1ncent.wanandroid.ui.system.pojo.KnowledgeSystemBean;
import com.v1ncent.wanandroid.utils.BarUtils;
import com.v1ncent.wanandroid.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

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
public class ArticleTypeActivity extends AppActivity {
    @BindView(R.id.tab_layout_main)
    TabLayout tabLayoutMain;
    @BindView(R.id.view_pager_main)
    ViewPager viewPagerMain;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.rootLayout)
    CoordinatorLayout rootLayout;

    private KnowledgeSystemBean.DataBean articleTypeBean;
    private List<KnowledgeSystemBean.DataBean.ChildrenBean> childrenBeanList = new ArrayList<>();
    private ArticleTypeFragmentPagerAdapter mArticleTypeFragmentPagerAdapter;
    private List<ArticleListFragment> mArticleTypeFragments = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_type);
        ButterKnife.bind(this);

        SlidrConfig config = new SlidrConfig.Builder()
                .position(SlidrPosition.LEFT)//滑动起始方向
                .edge(true)
                .edgeSize(0.18f)//距离左边界占屏幕大小的18%
                .build();
        Slidr.attach(this, config);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            // Enable the Up button
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        BarUtils.setStatusBarColor(this, ContextCompat.getColor(mContext, R.color.colorPrimaryDark), 24);
        BarUtils.addMarginTopEqualStatusBarHeight(rootLayout);

        if (getIntent().getExtras().getSerializable("knowledgeSystemList") != null) {
            articleTypeBean = (KnowledgeSystemBean.DataBean) getIntent().getExtras().getSerializable("knowledgeSystemList");
            getSupportActionBar().setTitle(articleTypeBean.getName());
            for (KnowledgeSystemBean.DataBean.ChildrenBean childrenBean : articleTypeBean.getChildren()) {
                childrenBeanList.add(childrenBean);
                ArticleListFragment articleListFragment = new ArticleListFragment(childrenBean);
                mArticleTypeFragments.add(articleListFragment);
            }
            LogUtils.i("childrenBeanList", childrenBeanList);
        } else {
            LogUtils.i("articleTypeBean", "is empty");
        }

        initView();
    }

    private void initView() {
        mArticleTypeFragmentPagerAdapter = new ArticleTypeFragmentPagerAdapter(getSupportFragmentManager(), mArticleTypeFragments, childrenBeanList);
        viewPagerMain.setAdapter(mArticleTypeFragmentPagerAdapter);
        tabLayoutMain.setupWithViewPager(viewPagerMain);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClickListener(View v) {

    }
}
