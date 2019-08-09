package com.v1ncent.wanandroid.ui.article.adapter;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.v1ncent.wanandroid.ui.article.ArticleListFragment;
import com.v1ncent.wanandroid.ui.system.pojo.KnowledgeSystemBean;

import java.util.List;

/**
 * ================================================
 * 作    者：v1ncent
 * 版    本：1.0.0
 * 创建日期：2018/3/27
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class ArticleTypeFragmentPagerAdapter extends FragmentPagerAdapter {
    @Nullable
    private List<KnowledgeSystemBean.DataBean.ChildrenBean> mChildrenData;
    private List<ArticleListFragment> mArticleTypeFragments;

    public ArticleTypeFragmentPagerAdapter(FragmentManager fm, List<ArticleListFragment> mArticleTypeFragments, List<KnowledgeSystemBean.DataBean.ChildrenBean> childrenData) {
        super(fm);
        this.mChildrenData = childrenData;
        this.mArticleTypeFragments = mArticleTypeFragments;
    }

    @Override
    public Fragment getItem(int position) {
        return mArticleTypeFragments.get(position);
    }

    @Override
    public int getCount() {
        return mArticleTypeFragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mChildrenData.get(position).getName();
    }
}
