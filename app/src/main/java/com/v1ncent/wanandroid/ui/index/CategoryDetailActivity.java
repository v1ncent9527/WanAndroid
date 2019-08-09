package com.v1ncent.wanandroid.ui.index;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.r0adkll.slidr.Slidr;
import com.v1ncent.wanandroid.R;
import com.v1ncent.wanandroid.api.JsonCallback;
import com.v1ncent.wanandroid.api.Urls;
import com.v1ncent.wanandroid.base.AppActivity;
import com.v1ncent.wanandroid.ui.article.ArticleDetailsActivity;
import com.v1ncent.wanandroid.ui.index.adapter.IndexAdapter;
import com.v1ncent.wanandroid.ui.index.pojo.IndexBean;
import com.v1ncent.wanandroid.utils.ActivityUtils;
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
 * 创建日期：2018/4/27
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class CategoryDetailActivity extends AppActivity
        implements SwipeRefreshLayout.OnRefreshListener
        , BaseQuickAdapter.OnItemClickListener {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.rv_list)
    RecyclerView rvArticleList;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.rootLayout)
    FrameLayout rootLayout;

    private IndexBean.DataBean.DatasBean indexBeen;
    private IndexAdapter indexAdapter;
    private List<IndexBean.DataBean.DatasBean> indexBeenList = new ArrayList<>();

    private View footer, no_data_footer;
    private boolean isLoadMore = false;
    private boolean isHasFooter = false;
    private boolean isHasMore = true;
    private int page = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_detail);
        ButterKnife.bind(this);

        if (getIntent().getExtras().getSerializable("indexBeen") != null) {
            indexBeen = (IndexBean.DataBean.DatasBean) getIntent().getExtras().getSerializable("indexBeen");
            LogUtils.i("indexBeen", indexBeen.toString());
        } else {
            LogUtils.i("indexBeen", "is empty");
        }
        initView();
    }

    private void initView() {
        Slidr.attach(this);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            // Enable the Up button
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(indexBeen.getChapterName());
        BarUtils.setStatusBarColor(this, ContextCompat.getColor(mContext, R.color.colorPrimaryDark), 24);
        BarUtils.addMarginTopEqualStatusBarHeight(rootLayout);

        //刷新监听
        swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(this, R.color.colorPrimary));
        swipeRefreshLayout.setOnRefreshListener(this);

        indexAdapter = new IndexAdapter(R.layout.item_index, indexBeenList, false);
        rvArticleList.setLayoutManager(new LinearLayoutManager(this));
        indexAdapter.setOnItemClickListener(CategoryDetailActivity.this);
        rvArticleList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                LinearLayoutManager lm = (LinearLayoutManager) recyclerView.getLayoutManager();
                int totalItemCount = recyclerView.getAdapter().getItemCount();
                int lastVisibleItemPosition = lm.findLastVisibleItemPosition();
                int visibleItemCount = recyclerView.getChildCount();

                //滑动的时候显示footview
                if (!isHasFooter && isHasMore) {
                    footer = LayoutInflater.from(CategoryDetailActivity.this).inflate(R.layout.index_foot_view, recyclerView, false);
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
        OkGo.<IndexBean>get(Urls.getArticleUrl(page, indexBeen.getChapterId()))
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

    private void onLoadMore() {
        LogUtils.i("indexPosition-->", page);
        swipeRefreshLayout.setEnabled(false);
        OkGo.<IndexBean>get(Urls.getArticleUrl(page, indexBeen.getChapterId()))
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
                                no_data_footer = LayoutInflater.from(CategoryDetailActivity.this).inflate(R.layout.no_data_foot_view, rvArticleList, false);
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
