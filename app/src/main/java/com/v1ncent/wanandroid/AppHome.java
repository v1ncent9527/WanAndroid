package com.v1ncent.wanandroid;

import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

import com.v1ncent.wanandroid.base.AppActivity;
import com.v1ncent.wanandroid.ui.about.AboutActivity;
import com.v1ncent.wanandroid.ui.collection.CollectTionFragment;
import com.v1ncent.wanandroid.ui.index.IndexFragment;
import com.v1ncent.wanandroid.ui.navigation.NavigationFragment;
import com.v1ncent.wanandroid.ui.project.ProjectFragment;
import com.v1ncent.wanandroid.ui.search.SearcherFragment;
import com.v1ncent.wanandroid.ui.system.SystemFragment;
import com.v1ncent.wanandroid.ui.user.LoginActivity;
import com.v1ncent.wanandroid.ui.user.UserInfoActivity;
import com.v1ncent.wanandroid.utils.ActivityUtils;
import com.v1ncent.wanandroid.utils.Constant;
import com.v1ncent.wanandroid.utils.LogUtils;
import com.v1ncent.wanandroid.utils.eventbus.EventBean;
import com.v1ncent.wanandroid.utils.eventbus.EventBusUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * ================================================
 * 作    者：v1ncent
 * 版    本：1.0.0
 * 创建日期：2018/3/14
 * 描    述：
 * 修订历史：
 * ================================================
 */

public class AppHome extends AppActivity implements NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.content_index)
    RelativeLayout contentIndex;
    @BindView(R.id.nav_view)
    NavigationView navView;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    private View headerView;
    private AppHome mContext;

    private IndexFragment indexFragment;
    private SystemFragment systemFragment;
    private NavigationFragment navigationFragment;
    private ProjectFragment projectFragment;
    private CollectTionFragment collectTionFragment;
    private SearcherFragment searchFragment;
    private FragmentManager fragmentManager;

    private int INDEX_INDEX = 0;        //首页
    private int SYSTEM_INDEX = 1;       //知识系统
    private int NAVIGATION_INDEX = 2;   //常用网站
    private int PROJECT_INDEX = 3;      //项目
    private int COLLECT_INDEX = 4;      //收藏
    private int SEARCH_INDEX = 5;       //搜索

    private int a = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_index);
        ButterKnife.bind(this);
        //向EventBus注册，成为订阅者
        EventBusUtils.register(this);

        initView();
    }

    private void initView() {
        toolbar.setTitle(R.string.title_index);
        setSupportActionBar(toolbar);

        /*----------------第一次进入显示首页-------------*/
        fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        hideFragments(transaction);
        if (indexFragment == null) {
            indexFragment = new IndexFragment();
            transaction.add(R.id.content_index, indexFragment);
        } else {
            transaction.show(indexFragment);
        }
        transaction.commit();
        /*----------------第一次进入显示首页-------------*/

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.setDrawerListener(toggle);
        toggle.syncState();
        navView.setNavigationItemSelectedListener(this);
        setUpProfileImage();
        headerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Constant.ISLOGINED) {
                    ActivityUtils.startActivity(mContext, UserInfoActivity.class);
                } else {
                    ActivityUtils.startActivity(mContext, LoginActivity.class);
                }
                drawerLayout.closeDrawer(GravityCompat.START);
            }
        });
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_index:
                toolbar.setTitle(R.string.title_index);
                plateSelect(INDEX_INDEX);
                break;
            case R.id.nav_sysytem:
                toolbar.setTitle(R.string.title_system);
                plateSelect(SYSTEM_INDEX);
                break;
            case R.id.nav_navigation:
                toolbar.setTitle(R.string.title_navigation);
                plateSelect(NAVIGATION_INDEX);
                break;
            case R.id.nav_project:
                toolbar.setTitle(R.string.title_project);
                plateSelect(PROJECT_INDEX);
                break;
            case R.id.nav_collection:
                toolbar.setTitle(R.string.title_collection);
                plateSelect(COLLECT_INDEX);
                break;
            case R.id.nav_search:
                toolbar.setTitle(R.string.title_search);
                plateSelect(SEARCH_INDEX);
                break;
            case R.id.nav_about:
                ActivityUtils.startActivity(mContext, AboutActivity.class);
                break;
            case R.id.nav_setting:
                String str = getUniquePsuedoID();
                LogUtils.e("uuid",str);
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    public static String getUniquePsuedoID() {
        String m_szDevIDShort = "35" + (Build.BOARD.length() % 10) + (Build.BRAND.length() % 10) + (Build.CPU_ABI.length() % 10) + (Build.DEVICE.length() % 10) + (Build.MANUFACTURER.length() % 10) + (Build.MODEL.length() % 10) + (Build.PRODUCT.length() % 10);

        String serial;
        try {
            serial = android.os.Build.class.getField("SERIAL").get(null).toString();
            return new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
        } catch (Exception exception) {
            serial = "serial";
        }
        return new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
    }


    /**
     * 点击选项后显示相应的板块
     *
     * @param index
     */
    private void plateSelect(int index) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        hideFragments(transaction);
        switch (index) {
            case 0:
                if (indexFragment == null) {
                    indexFragment = new IndexFragment();
                    transaction.add(R.id.content_index, indexFragment);
                } else {
                    transaction.show(indexFragment);
                }
                break;
            case 1:
                if (systemFragment == null) {
                    systemFragment = new SystemFragment();
                    transaction.add(R.id.content_index, systemFragment);
                } else {
                    transaction.show(systemFragment);
                }
                break;
            case 2:
                if (navigationFragment == null) {
                    navigationFragment = new NavigationFragment();
                    transaction.add(R.id.content_index, navigationFragment);
                } else {
                    transaction.show(navigationFragment);
                }
                break;
            case 3:
                if (projectFragment == null) {
                    projectFragment = new ProjectFragment();
                    transaction.add(R.id.content_index, projectFragment);
                } else {
                    transaction.show(projectFragment);
                }
                break;
            case 4:
                if (collectTionFragment == null) {
                    collectTionFragment = new CollectTionFragment();
                    transaction.add(R.id.content_index, collectTionFragment);
                } else {
                    transaction.show(collectTionFragment);
                }
                break;
            case 5:
                toolbar.setVisibility(View.GONE);
                if (searchFragment == null) {
                    searchFragment = new SearcherFragment();
                    transaction.add(R.id.content_index, searchFragment);
                } else {
                    transaction.show(searchFragment);
                }
                break;
        }
        transaction.commit();
    }

    /**
     * 先隐藏掉所有的Fragment，以防止有多个Fragment显示在界面上的情况
     *
     * @param transaction
     */
    private void hideFragments(FragmentTransaction transaction) {
        toolbar.setVisibility(View.VISIBLE);
        if (indexFragment != null) {
            transaction.hide(indexFragment);
        }
        if (systemFragment != null) {
            transaction.hide(systemFragment);
        }
        if (navigationFragment != null) {
            transaction.hide(navigationFragment);
        }
        if (projectFragment != null) {
            transaction.hide(projectFragment);
        }
        if (collectTionFragment != null) {
            transaction.hide(collectTionFragment);
        }
        if (searchFragment != null) {
            transaction.hide(searchFragment);
        }

    }

    /**
     * 设置header_index
     */
    private void setUpProfileImage() {
        headerView = navView.inflateHeaderView(R.layout.nav_header_index);
    }

    @OnClick({})
    public void onClickListener(View v) {
        switch (v.getId()) {
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.index, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * 处理eventbus返回的数据
     *
     * @param eventBean
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EventBean eventBean) {
        String origin = eventBean.getOrigin();
        switch (origin) {
            case Constant.OPENMEUN:
                drawerLayout.openDrawer(GravityCompat.START);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBusUtils.unRegister(this);
    }
}
