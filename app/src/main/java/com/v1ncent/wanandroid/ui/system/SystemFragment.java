package com.v1ncent.wanandroid.ui.system;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.v1ncent.wanandroid.R;
import com.v1ncent.wanandroid.api.JsonCallback;
import com.v1ncent.wanandroid.api.Urls;
import com.v1ncent.wanandroid.base.AppFragment;
import com.v1ncent.wanandroid.ui.article.ArticleTypeActivity;
import com.v1ncent.wanandroid.ui.system.adapter.KnowledgeSystemAdapter;
import com.v1ncent.wanandroid.ui.system.pojo.KnowledgeSystemBean;
import com.v1ncent.wanandroid.utils.ActivityUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by v1ncent on 2018/3/15.
 */

public class SystemFragment extends AppFragment implements
        SwipeRefreshLayout.OnRefreshListener
        , BaseQuickAdapter.OnItemClickListener {
    @BindView(R.id.rvKnowledgeSystems)
    RecyclerView rvKnowledgeSystems;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;

    private Context mContext;
    private KnowledgeSystemAdapter knowledgeSystemAdapter;
    private List<KnowledgeSystemBean.DataBean> knowledgeSystemList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_system, null);
        ButterKnife.bind(this, view);
        mContext = getActivity();

        initView();
        loadDate();
        return view;
    }

    private void initView() {
        //刷新监听
        swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(getActivity(), R.color.colorPrimary));
        swipeRefreshLayout.setOnRefreshListener(this);

        knowledgeSystemAdapter = new KnowledgeSystemAdapter(R.layout.item_knowledge_system, knowledgeSystemList);
        rvKnowledgeSystems.setLayoutManager(new LinearLayoutManager(mContext));
        knowledgeSystemAdapter.setOnItemClickListener(this);
        rvKnowledgeSystems.setAdapter(knowledgeSystemAdapter);
    }

    private void loadDate() {
        OkGo.<KnowledgeSystemBean>get(Urls.KNOWLEDGESYSTEM)
                .tag(this)
                .execute(new JsonCallback<KnowledgeSystemBean>() {
                    @Override
                    public void onSuccess(Response<KnowledgeSystemBean> response) {
                        KnowledgeSystemBean knowledgeSystemBean = response.body();
                        knowledgeSystemList.clear();
                        for (int i = 0; i < knowledgeSystemBean.getData().size(); i++) {
                            knowledgeSystemList.add(knowledgeSystemBean.getData().get(i));
                        }
                        knowledgeSystemAdapter.replaceData(knowledgeSystemList);
                    }

                    @Override
                    public void onError(Response<KnowledgeSystemBean> response) {
                        super.onError(response);
                        showError(response.getException().getMessage());
                    }

                    @Override
                    public void onFinish() {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
    }

    @Override
    public void onClickListener(View v) {

    }

    @Override
    public void onRefresh() {
        loadDate();
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("knowledgeSystemList", knowledgeSystemList.get(position));
        //进入文章详情页
        ActivityUtils.startActivity(bundle, ArticleTypeActivity.class);
    }
}
