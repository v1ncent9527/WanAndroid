package com.v1ncent.wanandroid.ui.system.adapter;

import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.v1ncent.wanandroid.R;
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
public class KnowledgeSystemAdapter extends BaseQuickAdapter<KnowledgeSystemBean.DataBean, BaseViewHolder> {
    public KnowledgeSystemAdapter(@LayoutRes int layoutResId, @Nullable List<KnowledgeSystemBean.DataBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, KnowledgeSystemBean.DataBean item) {
        helper.setText(R.id.typeItemFirst, item.getName());
        StringBuffer sb = new StringBuffer();
        for (KnowledgeSystemBean.DataBean.ChildrenBean childrenBean : item.getChildren()) {
            sb.append(childrenBean.getName() + "     ");
        }
        helper.setText(R.id.typeItemSecond, sb.toString());
    }
}
