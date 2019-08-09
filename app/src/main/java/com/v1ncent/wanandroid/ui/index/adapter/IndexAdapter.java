package com.v1ncent.wanandroid.ui.index.adapter;

import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.v1ncent.wanandroid.R;
import com.v1ncent.wanandroid.ui.index.pojo.IndexBean;
import com.v1ncent.wanandroid.utils.LogUtils;

import java.util.List;

/**
 * ================================================
 * 作    者：v1ncent
 * 版    本：1.0.0
 * 创建日期：2018/3/20
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class IndexAdapter extends BaseQuickAdapter<IndexBean.DataBean.DatasBean, BaseViewHolder> {
    private boolean isShowCategory;
    private int logoid[] = {
            R.mipmap.ic_person_logo1, R.mipmap.ic_person_logo2, R.mipmap.ic_person_logo3,
            R.mipmap.ic_person_logo4, R.mipmap.ic_person_logo5, R.mipmap.ic_person_logo6,
            R.mipmap.ic_person_logo7, R.mipmap.ic_person_logo8, R.mipmap.ic_person_logo9,
            R.mipmap.ic_person_logo10, R.mipmap.ic_person_logo11, R.mipmap.ic_person_logo3,
            R.mipmap.ic_person_logo13, R.mipmap.ic_person_logo14, R.mipmap.ic_person_logo15,
            R.mipmap.ic_person_logo16, R.mipmap.ic_person_logo17, R.mipmap.ic_person_logo18,
            R.mipmap.ic_person_logo19, R.mipmap.ic_person_logo20
    };

    public IndexAdapter(@LayoutRes int layoutResId, @Nullable List<IndexBean.DataBean.DatasBean> data, boolean isShowCategory) {
        super(layoutResId, data);
        this.isShowCategory = isShowCategory;
    }

    @Override
    protected void convert(BaseViewHolder helper, IndexBean.DataBean.DatasBean item) {
        LogUtils.i("BaseViewHolder", helper.getAdapterPosition());
        helper.addOnClickListener(R.id.item_category).addOnClickListener(R.id.item_collect);
        helper.setImageResource(R.id.item_head, logoid[(int) (Math.random() * 19 + 1)]);
        helper.setText(R.id.item_author, item.getAuthor());
        helper.setText(R.id.item_time, item.getNiceDate());
        helper.setText(R.id.item_content, item.getTitle());
        if (isShowCategory) {
            helper.setVisible(R.id.item_category_img, true);
            helper.setText(R.id.item_category, item.getChapterName());
        } else {
            helper.setVisible(R.id.item_category_img, false);
            helper.setText(R.id.item_category, "");
        }
    }
}
