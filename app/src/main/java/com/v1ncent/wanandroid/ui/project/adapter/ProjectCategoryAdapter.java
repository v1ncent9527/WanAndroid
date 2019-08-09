package com.v1ncent.wanandroid.ui.project.adapter;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.v1ncent.wanandroid.R;
import com.v1ncent.wanandroid.ui.project.pojo.ProjectCategoryBean;

import java.util.List;

/**
 * ================================================
 * 作    者：v1ncent
 * 版    本：1.0.0
 * 创建日期：2018/3/28
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class ProjectCategoryAdapter extends BaseQuickAdapter<ProjectCategoryBean.DataBean, BaseViewHolder> {
    private int colorArray[] = {R.color.red, R.color.pink, R.color.purple, R.color.deep_purple,
            R.color.lndigo, R.color.blue, R.color.light_blue, R.color.cyan,
            R.color.teal, R.color.green, R.color.brown, R.color.blue_gray,
            R.color.light_green, R.color.lime, R.color.yellow, R.color.amber,
            R.color.orange, R.color.deep_orange};
    private Context mContext;

    public ProjectCategoryAdapter(@LayoutRes int layoutResId,
                                  @Nullable List<ProjectCategoryBean.DataBean> data,
                                  Context mContext) {
        super(layoutResId, data);
        this.mContext = mContext;
    }

    @Override
    protected void convert(BaseViewHolder helper, ProjectCategoryBean.DataBean item) {
        helper.setTextColor(R.id.item_project_title, ContextCompat.getColor(mContext, colorArray[(int) (Math.random() * 14 + 1)]));
        helper.setText(R.id.item_project_title, item.getName());
    }
}
