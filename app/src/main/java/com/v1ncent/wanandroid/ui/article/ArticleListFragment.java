package com.v1ncent.wanandroid.ui.article;

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
import com.v1ncent.wanandroid.ui.index.adapter.IndexAdapter;
import com.v1ncent.wanandroid.ui.index.pojo.IndexBean;
import com.v1ncent.wanandroid.ui.system.pojo.KnowledgeSystemBean;
import com.v1ncent.wanandroid.utils.ActivityUtils;
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
public class ArticleListFragment extends AppFragment implements SwipeRefreshLayout.OnRefreshListener
        , BaseQuickAdapter.OnItemClickListener {
    @BindView(R.id.rvArticleList)
    RecyclerView rvArticleList;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;

    private KnowledgeSystemBean.DataBean.ChildrenBean childrenBean;
    private IndexAdapter indexAdapter;
    private List<IndexBean.DataBean.DatasBean> indexBeenList = new ArrayList<>();

    private View footer, no_data_footer;
    private boolean isLoadMore = false;
    private boolean isHasFooter = false;
    private boolean isHasMore = true;
    private int page = 0;

    public ArticleListFragment(KnowledgeSystemBean.DataBean.ChildrenBean childrenBean) {
        this.childrenBean = childrenBean;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_article_list, null);
        ButterKnife.bind(this, view);

        initView();
        return view;
    }

    private void initView() {
        //刷新监听
        swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(getActivity(), R.color.colorPrimary));
        swipeRefreshLayout.setOnRefreshListener(this);

        indexAdapter = new IndexAdapter(R.layout.item_index, indexBeenList, false);
        rvArticleList.setLayoutManager(new LinearLayoutManager(getActivity()));
        indexAdapter.setOnItemClickListener(this);
        rvArticleList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                LinearLayoutManager lm = (LinearLayoutManager) recyclerView.getLayoutManager();
                int totalItemCount = recyclerView.getAdapter().getItemCount();
                int lastVisibleItemPosition = lm.findLastVisibleItemPosition();
                int visibleItemCount = recyclerView.getChildCount();

                //滑动的时候显示footview
                if (!isHasFooter && isHasMore) {
                    footer = LayoutInflater.from(getActivity()).inflate(R.layout.index_foot_view, recyclerView, false);
                    indexAdapter.addFooterView(footer);
                    isHasFooter = true;
                }

                //滑动最后一条是请求数据
                if (newState == RecyclerView.SCROLL_STATE_IDLE
                        && lastVisibleItemPosition == totalItemCount - 1
                        && visibleItemCount > 0) {
                    if (!isLoadMore && isHasMore) {
                        isLoadMore = true;
                        onLoadMore();
                    }
                }
            }
        });

        rvArticleList.setAdapter(indexAdapter);

        loadData();
    }

    private void loadData() {
        OkGo.<IndexBean>get(Urls.getArticleUrl(page, childrenBean.getId()))
                .tag(this)
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
                                isHasMore = true;
                            }
                        }

                    }

                    @Override
                    public void onError(Response<IndexBean> response) {
                        showError(response.getException().getMessage());
                    }


                    @Override
                    public void onFinish() {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
    }

    /**
     * 加载更多
     */
    public void onLoadMore() {
        LogUtils.i("indexPosition-->", page);
        swipeRefreshLayout.setEnabled(false);
        OkGo.<IndexBean>get(Urls.getArticleUrl(page, childrenBean.getId()))
                .tag(this)
                .execute(new JsonCallback<IndexBean>() {
                    @Override
                    public void onSuccess(Response<IndexBean> response) {
                        IndexBean indexBean = response.body();
                        if (indexBean.getErrorCode() == 0) {
                            if (!indexBean.getData().getDatas().isEmpty()) {
                                for (int i = 0; i < indexBean.getData().getDatas().size(); i++) {
                                    indexBeenList.add(indexBean.getData().getDatas().get(i));
                                }
                                indexAdapter.replaceData(indexBeenList);
                                page += 1;
                                LoadMoreComplete();
                            } else {
                                LoadMoreComplete();
                                indexAdapter.notifyDataSetChanged();
                                isHasMore = false;
                                no_data_footer = LayoutInflater.from(getActivity()).inflate(R.layout.no_data_foot_view, rvArticleList, false);
                                indexAdapter.addFooterView(no_data_footer);
                            }
                        }

                    }

                    @Override
                    public void onError(Response<IndexBean> response) {
                        showError(response.getException().getMessage());
                        LoadMoreComplete();
                    }

                });
    }

    @Override
    public void onClickListener(View v) {

    }

    @Override
    public void onRefresh() {
        page = 0;
        indexAdapter.removeAllFooterView();
        isLoadMore = false;
        indexAdapter.setEnableLoadMore(false);
        isHasMore = false;
        loadData();
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
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("indexBeen", indexBeenList.get(position));
        //进入文章详情页
        ActivityUtils.startActivity(bundle, ArticleDetailsActivity.class);
    }
}
