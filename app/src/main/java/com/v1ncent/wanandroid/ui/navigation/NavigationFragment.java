package com.v1ncent.wanandroid.ui.navigation;

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
import com.v1ncent.wanandroid.ui.article.ArticleDetailsActivity;
import com.v1ncent.wanandroid.ui.index.pojo.IndexBean;
import com.v1ncent.wanandroid.ui.navigation.adapter.NavigationAdapter;
import com.v1ncent.wanandroid.ui.navigation.pojo.NavigationBean;
import com.v1ncent.wanandroid.utils.ActivityUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by v1ncent on 2018/3/15.
 */

public class NavigationFragment extends AppFragment implements
        SwipeRefreshLayout.OnRefreshListener
        , BaseQuickAdapter.OnItemClickListener {

    @BindView(R.id.rv_list)
    RecyclerView recyclerView;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;

    private NavigationAdapter navigationAdapter;
    private List<NavigationBean.DataBean> dataBeanList = new ArrayList<>();
    private Context mContext;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_navigation, null);
        ButterKnife.bind(this, view);
        mContext = getActivity();

        initView();
        return view;
    }

    private void initView() {
        //刷新监听
        swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(getActivity(), R.color.colorPrimary));
        swipeRefreshLayout.setOnRefreshListener(this);

        navigationAdapter = new NavigationAdapter(R.layout.item_navigation, dataBeanList, mContext);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        navigationAdapter.setOnItemClickListener(this);
        navigationAdapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_BOTTOM);
        recyclerView.setAdapter(navigationAdapter);

        loadData();
    }

    private void loadData() {
        //加载首页数据
        OkGo.<NavigationBean>get(Urls.NAVIGATION)
                .tag(this)
                .execute(new JsonCallback<NavigationBean>() {
                    @Override
                    public void onSuccess(Response<NavigationBean> response) {
                        NavigationBean navigationBean = response.body();
                        dataBeanList.clear();
                        for (int i = 0; i < navigationBean.getData().size(); i++) {
                            dataBeanList.add(navigationBean.getData().get(i));
                        }
                        navigationAdapter.replaceData(dataBeanList);
                    }

                    @Override
                    public void onError(Response<NavigationBean> response) {
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
        loadData();
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        Bundle bundle = new Bundle();
        IndexBean.DataBean.DatasBean indexBeen = new IndexBean.DataBean.DatasBean();
        indexBeen.setLink(dataBeanList.get(position).getLink());
        indexBeen.setTitle(dataBeanList.get(position).getName());
        bundle.putSerializable("indexBeen", indexBeen);
        //进入文章详情页
        ActivityUtils.startActivity(bundle, ArticleDetailsActivity.class);
    }
}
