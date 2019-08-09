package com.v1ncent.wanandroid.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.v1ncent.wanandroid.utils.Toasty;

/**
 * Created by v1ncent on 2018/3/14.
 */

public abstract class AppActivity extends AppCompatActivity implements View.OnClickListener {
    protected AppActivity mContext;

    public abstract void onClickListener(View v);

    /**
     * 上次点击时间
     */
    private long lastClick = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    /**
     * 判断是否快速点击
     *
     * @return {@code true}: 是<br>{@code false}: 否
     */
    private boolean isFastClick() {
        long now = System.currentTimeMillis();
        if (now - lastClick >= 200) {
            lastClick = now;
            return false;
        }
        return true;
    }

    /**
     * toast INFO
     *
     * @param info
     */
    public void showInfo(String info) {
        Toasty.info(getApplicationContext(), info, Toast.LENGTH_SHORT, true).show();
    }

    /**
     * toast SUCCESS
     *
     * @param success
     */
    public void showSuccess(String success) {
        Toasty.success(getApplicationContext(), success, Toast.LENGTH_SHORT, true).show();
    }

    /**
     * toast ERROR
     *
     * @param error
     */
    public void showError(String error) {
        Toasty.error(getApplicationContext(), error, Toast.LENGTH_SHORT, true).show();

    }

    @Override
    public void onClick(final View view) {
        if (!isFastClick()) onClickListener(view);
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isShouldHideKeyboard(v, ev)) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    // 根据EditText所在坐标和用户点击的坐标相对比，来判断是否隐藏键盘
    private boolean isShouldHideKeyboard(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] l = {0, 0};
            v.getLocationInWindow(l);
            int left = l[0],
                    top = l[1],
                    bottom = top + v.getHeight(),
                    right = left + v.getWidth();
            return !(event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom);
        }
        return false;
    }

}
