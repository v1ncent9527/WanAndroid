package com.v1ncent.wanandroid.ui.article;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.just.agentweb.AgentWeb;
import com.just.agentweb.DefaultWebClient;
import com.r0adkll.slidr.Slidr;
import com.r0adkll.slidr.model.SlidrConfig;
import com.r0adkll.slidr.model.SlidrPosition;
import com.v1ncent.wanandroid.R;
import com.v1ncent.wanandroid.base.AppActivity;
import com.v1ncent.wanandroid.ui.index.pojo.BannerBean;
import com.v1ncent.wanandroid.ui.index.pojo.IndexBean.DataBean.DatasBean;
import com.v1ncent.wanandroid.utils.BarUtils;
import com.v1ncent.wanandroid.utils.LogUtils;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * ================================================
 * 作    者：v1ncent
 * 版    本：1.0.0
 * 创建日期：2018/3/26
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class ArticleDetailsActivity extends AppActivity implements PopupMenu.OnMenuItemClickListener {
    @BindView(R.id.iv_back)
    LinearLayout ivBack;
    @BindView(R.id.view_line)
    View viewLine;
    @BindView(R.id.iv_finish)
    LinearLayout ivFinish;
    @BindView(R.id.toolbar_title)
    TextView toolbarTitle;
    @BindView(R.id.iv_more)
    LinearLayout ivMore;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.linearLayout)
    LinearLayout linearLayout;

    public static final String TAG = ArticleDetailsActivity.class.getSimpleName();

    private DatasBean indexBeen;
    private AgentWeb mAgentWeb;
    private PopupMenu mPopupMenu;
    private BannerBean.DataBean bannerBean;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_details);
        ButterKnife.bind(this);

        if (getIntent().getExtras().getSerializable("indexBeen") != null) {
            indexBeen = (DatasBean) getIntent().getExtras().getSerializable("indexBeen");
            LogUtils.i("indexBeen", indexBeen.toString());
        } else {
            LogUtils.i("indexBeen", "is empty");
        }

        if (getIntent().getExtras().getSerializable("bannerBeen") != null) {
            bannerBean = (BannerBean.DataBean) getIntent().getExtras().getSerializable("bannerBeen");
            indexBeen = new DatasBean();
            indexBeen.setTitle(bannerBean.getTitle());
            indexBeen.setLink(bannerBean.getUrl());
        }
        initView();
    }

    private void initView() {
        SlidrConfig config = new SlidrConfig.Builder()
                .position(SlidrPosition.LEFT)//滑动起始方向
                .edge(true)
                .edgeSize(0.18f)//距离左边界占屏幕大小的18%
                .build();
        Slidr.attach(this, config);
        BarUtils.setStatusBarColor(this, ContextCompat.getColor(mContext, R.color.colorPrimaryDark), 24);
        BarUtils.addMarginTopEqualStatusBarHeight(linearLayout);

        //mAgentWeb初始化
        mAgentWeb = AgentWeb.with(this)//
                .setAgentWebParent(linearLayout, -1, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))//传入AgentWeb的父控件。
                .useDefaultIndicator(-1, 3)//设置进度条颜色与高度，-1为默认值，高度为2，单位为dp。
                .setWebViewClient(mWebViewClient)//WebViewClient ， 与 WebView 使用一致 ，但是请勿获取WebView调用setWebViewClient(xx)方法了,会覆盖AgentWeb DefaultWebClient,同时相应的中间件也会失效。
                .setWebChromeClient(mWebChromeClient) //WebChromeClient
                .setSecurityType(AgentWeb.SecurityType.STRICT_CHECK) //严格模式 Android 4.2.2 以下会放弃注入对象 ，使用AgentWebView没影响。
                .setMainFrameErrorView(R.layout.load_error, -1) //参数1是错误显示的布局，参数2点击刷新控件ID -1表示点击整个布局都刷新， AgentWeb 3.0.0 加入。
                .setOpenOtherPageWays(DefaultWebClient.OpenOtherPageWays.ASK)//打开其他应用时，弹窗咨询用户是否前往其他应用
                .interceptUnkownUrl() //拦截找不到相关页面的Url AgentWeb 3.0.0 加入。
                .createAgentWeb()//创建AgentWeb。
                .ready()//设置 WebSettings。
                .go(indexBeen.getLink()); //WebView载入该url地址的页面并显示。

    }

    protected WebViewClient mWebViewClient = new WebViewClient() {

        private HashMap<String, Long> timer = new HashMap<>();

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            return shouldOverrideUrlLoading(view, request.getUrl() + "");
        }

        @Override
        public boolean shouldOverrideUrlLoading(final WebView view, String url) {
            return false;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            timer.put(url, System.currentTimeMillis());
            if (url.equals(indexBeen.getLink())) {
                ivBack.setVisibility(View.GONE);
                viewLine.setVisibility(View.GONE);
            } else {
                ivBack.setVisibility(View.VISIBLE);
                viewLine.setVisibility(View.VISIBLE);
            }

        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            if (timer.get(url) != null) {
                long overTime = System.currentTimeMillis();
                Long startTime = timer.get(url);
                Log.i(TAG, "  page mUrl:" + url + "  used time:" + (overTime - startTime));
            }

        }

        @Override
        public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
            super.onReceivedHttpError(view, request, errorResponse);
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
        }
    };

    protected WebChromeClient mWebChromeClient = new WebChromeClient() {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            Log.i(TAG, "onProgressChanged:" + newProgress + "  view:" + view);
        }

        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
            if (toolbarTitle != null && !TextUtils.isEmpty(title)) {
                if (title.length() > 10) {
                    title = title.substring(0, 10).concat("...");
                }
            }
            toolbarTitle.setText(title);
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (mAgentWeb.handleKeyEvent(keyCode, event)) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @OnClick({R.id.iv_back, R.id.iv_finish, R.id.iv_more})
    public void onClickListener(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                if (!mAgentWeb.back()) {
                    finish();
                }
                break;
            case R.id.iv_finish:
                finish();
                break;
            case R.id.iv_more:
                showPoPup(v);
                break;
            default:
                break;
        }
    }

    /**
     * 显示更多菜单
     *
     * @param view 菜单依附在该View下面
     */
    private void showPoPup(View view) {
        if (mPopupMenu == null) {
            mPopupMenu = new PopupMenu(this, view);
            mPopupMenu.inflate(R.menu.article_menu);
            mPopupMenu.setOnMenuItemClickListener(this);
        }
        mPopupMenu.show();
    }

    /**
     * 菜单事件
     */
    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            // 分享
            case R.id.share:
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_article_url, getString(R.string.app_name), indexBeen.getTitle(), indexBeen.getLink()));
                intent.setType("text/plain");
                startActivity(intent);
                break;
            // 收藏
            case R.id.love:
                break;
            // 刷新
            case R.id.refresh:
                if (mAgentWeb != null) {
                    mAgentWeb.getUrlLoader().reload();
                }
                break;
            // 复制链接
            case R.id.copy:
                if (mAgentWeb != null) {
                    toCopy(this, mAgentWeb.getWebCreator().getWebView().getUrl());
                    showSuccess("本文链接已复制到剪贴板 :-D");
                }
                break;
            // 浏览器打开
            case R.id.default_browser:
                if (mAgentWeb != null) {
                    openBrowser(mAgentWeb.getWebCreator().getWebView().getUrl());
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 复制字符串到剪贴板
     *
     * @param context
     * @param text
     */
    private void toCopy(Context context, String text) {
        ClipboardManager mClipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        mClipboardManager.setPrimaryClip(ClipData.newPlainText(null, text));

    }

    /**
     * 打开浏览器
     *
     * @param targetUrl 外部浏览器打开的地址
     */
    private void openBrowser(String targetUrl) {
        if (TextUtils.isEmpty(targetUrl) || targetUrl.startsWith("file://")) {
            showInfo("该链接无法使用浏览器打开!");
            return;
        }
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        Uri mUri = Uri.parse(targetUrl);
        intent.setData(mUri);
        startActivity(intent);
    }

    @Override
    protected void onPause() {
        mAgentWeb.getWebLifeCycle().onPause();
        super.onPause();

    }

    @Override
    protected void onResume() {
        mAgentWeb.getWebLifeCycle().onResume();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAgentWeb.getWebLifeCycle().onDestroy();
    }


}
