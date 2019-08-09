package com.v1ncent.wanandroid.ui.search;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.v1ncent.wanandroid.R;
import com.v1ncent.wanandroid.api.JsonCallback;
import com.v1ncent.wanandroid.api.Urls;
import com.v1ncent.wanandroid.base.AppFragment;
import com.v1ncent.wanandroid.ui.article.ArticleDetailsActivity;
import com.v1ncent.wanandroid.ui.index.CategoryDetailActivity;
import com.v1ncent.wanandroid.ui.index.adapter.IndexAdapter;
import com.v1ncent.wanandroid.ui.index.pojo.IndexBean;
import com.v1ncent.wanandroid.utils.ActivityUtils;
import com.v1ncent.wanandroid.utils.Constant;
import com.v1ncent.wanandroid.utils.LogUtils;
import com.v1ncent.wanandroid.utils.eventbus.EventBusUtils;
import com.wyt.searchbox.SearchFragment;
import com.wyt.searchbox.custom.IOnSearchClickListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * ================================================
 * 作    者：v1ncent
 * 版    本：1.0.0
 * 创建日期：2018/4/20
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class SearcherFragment extends AppFragment implements
        SwipeRefreshLayout.OnRefreshListener
        , BaseQuickAdapter.OnItemClickListener {
    @BindView(R.id.iv_menu)
    LinearLayout ivMenu;
    @BindView(R.id.toolbar_title)
    TextView toolbarTitle;
    @BindView(R.id.iv_serach)
    LinearLayout ivSerach;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.rv_list)
    RecyclerView recyclerView;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;

    private Context mContext;
    private int page = 0;
    private List<IndexBean.DataBean.DatasBean> indexBeenList = new ArrayList<>();
    private IndexAdapter indexAdapter;
    private View footer, no_data_footer;
    private boolean isLoadMore = false;
    private boolean isHasFooter = false;
    private boolean isHasMore = true;
    private String keywordStr = "";
    private View errorView, notDataView, firstLoadView;
    private boolean isFirstLoad = true;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_search, null);
        ButterKnife.bind(this, view);

        mContext = getActivity();
        initView();
        return view;
    }

    private void initView() {
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        //刷新监听
        swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(getActivity(), R.color.colorPrimary));
        swipeRefreshLayout.setOnRefreshListener(this);

        indexAdapter = new IndexAdapter(R.layout.item_index, indexBeenList, true);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        indexAdapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_BOTTOM);
        indexAdapter.setOnItemClickListener(this);

        indexAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                switch (view.getId()) {
                    case R.id.item_category:
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("indexBeen", indexBeenList.get(position));
                        //进入文章详情页
                        ActivityUtils.startActivity(bundle, CategoryDetailActivity.class);
                        break;
                    case R.id.item_collect:
                        showSuccess(position + "❤️");
                        break;
                }
            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                LinearLayoutManager lm = (LinearLayoutManager) recyclerView.getLayoutManager();
                int totalItemCount = recyclerView.getAdapter().getItemCount();
                int lastVisibleItemPosition = lm.findLastVisibleItemPosition();
                int visibleItemCount = recyclerView.getChildCount();

                //滑动的时候显示footview
                if (!isHasFooter && !isFirstLoad && isHasMore) {
                    footer = LayoutInflater.from(getActivity()).inflate(R.layout.index_foot_view, recyclerView, false);
                    indexAdapter.addFooterView(footer);
                    isHasFooter = true;
                }

                //滑动最后一条是请求数据
                if (newState == RecyclerView.SCROLL_STATE_IDLE
                        && lastVisibleItemPosition == totalItemCount - 1
                        && visibleItemCount > 0) {
                    if (!isLoadMore && !isFirstLoad && isHasMore) {
                        isLoadMore = true;
                        onLoadMore();
                    }
                }
            }
        });

        notDataView = getActivity().getLayoutInflater().inflate(R.layout.empty_view, (ViewGroup) recyclerView.getParent(), false);
        firstLoadView = getActivity().getLayoutInflater().inflate(R.layout.view_serach_index, (ViewGroup) recyclerView.getParent(), false);

        errorView = getActivity().getLayoutInflater().inflate(R.layout.error_view, (ViewGroup) recyclerView.getParent(), false);
        errorView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRefresh();
            }
        });
        recyclerView.setAdapter(indexAdapter);
        indexAdapter.setEmptyView(firstLoadView);
    }

    @OnClick({R.id.iv_menu, R.id.iv_serach})
    public void onClickListener(View v) {
        switch (v.getId()) {
            case R.id.iv_menu:
                EventBusUtils.postEvent(Constant.OPENMEUN, true);
                break;
            case R.id.iv_serach:
                loadSearchView();
                break;
        }
    }

    private void loadSearchView() {
        SearchFragment searchFragment = SearchFragment.newInstance();

        searchFragment.setOnSearchClickListener(new IOnSearchClickListener() {
            @Override
            public void OnSearchClick(String keyword) {
                keywordStr = keyword;
                page = 0;
                isFirstLoad = false;
                ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("搜索 (" + keyword + " )");
                loadSearch();
            }
        });
        searchFragment.show(getActivity().getSupportFragmentManager(), SearchFragment.TAG);
    }

    private void loadSearch() {
        indexAdapter.removeAllFooterView();
        isHasMore = true;
        if (isFirstLoad || TextUtils.isEmpty(keywordStr.trim())) {
            swipeRefreshLayout.setRefreshing(false);
            return;
        }
        //加载首页数据
        OkGo.<IndexBean>post(Urls.getSearchUrl(page))
                .tag(this)
                .params("k", keywordStr)
                .execute(new JsonCallback<IndexBean>() {
                    @Override
                    public void onSuccess(Response<IndexBean> response) {
                        IndexBean indexBean = response.body();
                        if (indexBean.getErrorCode() == 0) {
                            if (!indexBean.getData().getDatas().isEmpty()) {
                                indexBeenList.clear();
                                for (int i = 0; i < indexBean.getData().getDatas().size(); i++) {
                                    indexBeenList.add(indexBean.getData().getDatas().get(i));
                                }
                                indexAdapter.replaceData(indexBeenList);
                                page += 1;
                            } else {
                                isHasMore = false;
                                indexAdapter.setNewData(null);
                                indexAdapter.setEmptyView(notDataView);
                            }
                        } else {
                            isHasMore = false;
                            indexAdapter.setNewData(null);
                            indexAdapter.setEmptyView(notDataView);
                        }
                    }

                    @Override
                    public void onError(Response<IndexBean> response) {
                        indexAdapter.setEmptyView(errorView);
                    }

                    @Override
                    public void onFinish() {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
    }

    /**
     * 上拉加载更多数据
     */

    public void onLoadMore() {
        LogUtils.i("indexPosition-->", page);
        swipeRefreshLayout.setEnabled(false);
        OkGo.<IndexBean>post(Urls.getSearchUrl(page))
                .tag(this)
                .params("k", keywordStr)
                .execute(new JsonCallback<IndexBean>() {
                    @Override
                    public void onSuccess(Response<IndexBean> response) {
                        IndexBean indexBean = response.body();
                        if (!indexBean.getData().getDatas().isEmpty()) {
                            for (int i = 0; i < indexBean.getData().getDatas().size(); i++) {
                                indexBeenList.add(indexBean.getData().getDatas().get(i));
                            }
                            indexAdapter.notifyDataSetChanged();
                            page += 1;
                            LoadMoreComplete();
                        } else {
                            LoadMoreComplete();
                            indexAdapter.notifyDataSetChanged();
                            isHasMore = false;
                            no_data_footer = LayoutInflater.from(getActivity()).inflate(R.layout.no_data_foot_view, recyclerView, false);
                            indexAdapter.addFooterView(no_data_footer);
                        }
                    }

                    @Override
                    public void onError(Response<IndexBean> response) {
                        indexAdapter.setEmptyView(errorView);
                        LoadMoreComplete();
                    }

                });
    }

    /**
     * 上拉加载完成之后的View处理
     */
    private void LoadMoreComplete() {
        indexAdapter.removeAllFooterView();
        isLoadMore = false;
        isHasFooter = false;
        swipeRefreshLayout.setEnabled(true);
    }

    @Override
    public void onRefresh() {
        indexAdapter.setEnableLoadMore(false);
        page = 0;
        loadSearch();
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("indexBeen", indexBeenList.get(position));
        //进入文章详情页
        ActivityUtils.startActivity(bundle, ArticleDetailsActivity.class);
    }
}