package com.v1ncent.wanandroid.ui.project;

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
import com.v1ncent.wanandroid.ui.project.adapter.ProjectCategoryAdapter;
import com.v1ncent.wanandroid.ui.project.pojo.ProjectCategoryBean;
import com.v1ncent.wanandroid.utils.ActivityUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by v1ncent on 2018/3/15.
 */

public class ProjectFragment extends AppFragment implements
        SwipeRefreshLayout.OnRefreshListener
        , BaseQuickAdapter.OnItemClickListener {
    @BindView(R.id.rv_list)
    RecyclerView recyclerView;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;

    private ProjectCategoryAdapter projectCategoryAdapter;
    private List<ProjectCategoryBean.DataBean> dataBeanList = new ArrayList<>();
    private Context mContext;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_project, null);
        ButterKnife.bind(this, view);
        mContext = getActivity();

        initView();
        return view;
    }

    private void initView() {
        //刷新监听
        swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(getActivity(), R.color.colorPrimary));
        swipeRefreshLayout.setOnRefreshListener(this);

        projectCategoryAdapter = new ProjectCategoryAdapter(R.layout.item_project_category, dataBeanList, mContext);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        projectCategoryAdapter.setOnItemClickListener(this);
        projectCategoryAdapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_BOTTOM);
        recyclerView.setAdapter(projectCategoryAdapter);

        loadData();
    }

    private void loadData() {
        //加载首页数据
        OkGo.<ProjectCategoryBean>get(Urls.PROJECTCATEGORY)
                .tag(this)
                .execute(new JsonCallback<ProjectCategoryBean>() {
                    @Override
                    public void onSuccess(Response<ProjectCategoryBean> response) {
                        ProjectCategoryBean projectCategoryBean = response.body();
                        dataBeanList.clear();
                        for (int i = 0; i < projectCategoryBean.getData().size(); i++) {
                            dataBeanList.add(projectCategoryBean.getData().get(i));
                        }
                        projectCategoryAdapter.replaceData(dataBeanList);
                    }

                    @Override
                    public void onError(Response<ProjectCategoryBean> response) {
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
        bundle.putSerializable("projectCategoryBean", dataBeanList.get(position));
        //进入文章详情页
        ActivityUtils.startActivity(bundle, ProjectListActivity.class);
    }
}
