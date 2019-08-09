package com.v1ncent.wanandroid.ui.index;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
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
import com.v1ncent.wanandroid.ui.index.adapter.IndexAdapter;
import com.v1ncent.wanandroid.ui.index.pojo.BannerBean;
import com.v1ncent.wanandroid.ui.index.pojo.IndexBean;
import com.v1ncent.wanandroid.utils.ActivityUtils;
import com.v1ncent.wanandroid.utils.GlideImageLoader;
import com.v1ncent.wanandroid.utils.LogUtils;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.listener.OnBannerListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by v1ncent on 2018/3/15.
 */

public class IndexFragment extends AppFragment implements OnBannerListener,
        SwipeRefreshLayout.OnRefreshListener
        , BaseQuickAdapter.OnItemClickListener {
    @BindView(R.id.rv_list)
    RecyclerView recyclerView;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.fab)
    FloatingActionButton fab;

    private Banner banner;
    private Context mContext;
    private List<String> picPathList = new ArrayList<>();
    private List<String> pictitleList = new ArrayList<>();
    private List<BannerBean.DataBean> bannerList = new ArrayList<>();
    private List<IndexBean.DataBean.DatasBean> indexBeenList = new ArrayList<>();

    private IndexAdapter indexAdapter;
    private int indexPosition = 0;
    private View footer;
    private boolean isLoadMore = false;
    private boolean isHasFooter = false;

    private static final int HIDE_THRESHOLD = 20;
    private int scrolledDistance = 0;
    private boolean controlsVisible = true;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_index, null);
        ButterKnife.bind(this, view);
        mContext = getActivity();

        initView();
        loadDate(indexPosition);//进入首页第一次加载数据
        return view;
    }

    private void initView() {
        //刷新监听
        swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(getActivity(), R.color.colorPrimary));
        swipeRefreshLayout.setOnRefreshListener(this);

        indexAdapter = new IndexAdapter(R.layout.item_index, indexBeenList, true);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));

        //添加Header
        View header = LayoutInflater.from(getActivity()).inflate(R.layout.banner_index, recyclerView, false);
        banner = (Banner) header;
        //初始化banner配置
        banner.setDelayTime(3000)
                .setBannerStyle(BannerConfig.CIRCLE_INDICATOR_TITLE_INSIDE)
                .setImageLoader(new GlideImageLoader())
                .setOnBannerListener(this);
        indexAdapter.addHeaderView(banner);
        indexAdapter.openLoadAnimation(BaseQuickAdapter.SCALEIN);
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
        /*---------------BaseRecyclerViewAdapterHelper 上拉加载在某些机型上有Bug,上拉加载自己实现-------------*/
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                LinearLayoutManager lm = (LinearLayoutManager) recyclerView.getLayoutManager();
                int totalItemCount = recyclerView.getAdapter().getItemCount();
                int lastVisibleItemPosition = lm.findLastVisibleItemPosition();
                int visibleItemCount = recyclerView.getChildCount();

                //滑动的时候显示footview
                if (!isHasFooter) {
                    footer = LayoutInflater.from(getActivity()).inflate(R.layout.index_foot_view, recyclerView, false);
                    indexAdapter.addFooterView(footer);
                    isHasFooter = true;
                }

                //滑动最后一条是请求数据
                if (newState == RecyclerView.SCROLL_STATE_IDLE
                        && lastVisibleItemPosition == totalItemCount - 1
                        && visibleItemCount > 0) {
                    if (!isLoadMore) {
                        isLoadMore = true;
                        onLoadMore();
                    }
                }
            }
            /*---------------BaseRecyclerViewAdapterHelper 上拉加载在某些机型上有Bug,上拉加载自己实现-------------*/

            //fab的显示于隐藏
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (scrolledDistance > HIDE_THRESHOLD && controlsVisible) {//上滑
                    fab.hide();
                    controlsVisible = false;
                    scrolledDistance = 0;
                } else if (scrolledDistance < -HIDE_THRESHOLD && !controlsVisible) {//下滑
                    fab.show();
                    controlsVisible = true;
                    scrolledDistance = 0;
                }
                if ((controlsVisible && dy > 0) || (!controlsVisible && dy < 0)) {
                    scrolledDistance += dy;
                }
            }
        });

        recyclerView.setAdapter(indexAdapter);
    }


    //下拉刷新
    @Override
    public void onRefresh() {
        indexAdapter.setEnableLoadMore(false);
        indexPosition = 0;
        loadDate(indexPosition);
    }

    /**
     * 加载下来刷新数据
     */
    private void loadDate(final int index) {
        //第一部 加载banner数据
        OkGo.<BannerBean>get(Urls.BANNER)
                .tag(this)
                .execute(new JsonCallback<BannerBean>() {
                    @Override
                    public void onSuccess(Response<BannerBean> response) {
                        BannerBean bannerResult = response.body();
                        bannerList.clear();
                        picPathList.clear();
                        pictitleList.clear();

                        for (int i = 0; i < bannerResult.getData().size(); i++) {
                            bannerList.add(bannerResult.getData().get(i));
                            picPathList.add(bannerResult.getData().get(i).getImagePath());
                            pictitleList.add(bannerResult.getData().get(i).getTitle());
                            banner.setImages(picPathList)
                                    .setBannerTitles(pictitleList).start();
                        }
                        //加载首页数据
                        OkGo.<IndexBean>get(Urls.getIndexUrl(index))
                                .tag(this)
                                .execute(new JsonCallback<IndexBean>() {
                                    @Override
                                    public void onSuccess(Response<IndexBean> response) {
                                        IndexBean indexBean = response.body();
                                        indexBeenList.clear();
                                        for (int i = 0; i < indexBean.getData().getDatas().size(); i++) {
                                            indexBeenList.add(indexBean.getData().getDatas().get(i));
                                        }
                                        indexAdapter.replaceData(indexBeenList);
                                        indexPosition += 1;
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
                    public void onError(Response<BannerBean> response) {
                        showError(response.getException().getMessage());
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
    }

    /**
     * 上拉加载更多数据
     */

    public void onLoadMore() {
        LogUtils.i("indexPosition-->", indexPosition);
        swipeRefreshLayout.setEnabled(false);
        OkGo.<IndexBean>get(Urls.getIndexUrl(indexPosition))
                .tag(this)
                .execute(new JsonCallback<IndexBean>() {
                    @Override
                    public void onSuccess(Response<IndexBean> response) {
                        IndexBean indexBean = response.body();
                        for (int i = 0; i < indexBean.getData().getDatas().size(); i++) {
                            indexBeenList.add(indexBean.getData().getDatas().get(i));
                        }
                        indexAdapter.notifyDataSetChanged();
                        indexPosition += 1;
                        LoadMoreComplete();
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
    public void onStop() {
        super.onStop();
        banner.stopAutoPlay();
    }

    @OnClick({R.id.fab})
    public void onClickListener(View v) {
        switch (v.getId()) {
            case R.id.fab:
                //置顶
                recyclerView.smoothScrollToPosition(0);
                break;
        }
    }

    @Override
    public void OnBannerClick(int position) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("bannerBeen", bannerList.get(position));
        //进入文章详情页
        ActivityUtils.startActivity(bundle, ArticleDetailsActivity.class);
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("indexBeen", indexBeenList.get(position));
        //进入文章详情页
        ActivityUtils.startActivity(bundle, ArticleDetailsActivity.class);
    }
}

