package com.v1ncent.wanandroid.base;

import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Toast;

import com.v1ncent.wanandroid.utils.Toasty;

/**
 * Created by v1ncent on 2018/3/15.
 */

public abstract class AppFragment extends Fragment implements View.OnClickListener {
    public abstract void onClickListener(View v);


    /**
     * 上次点击时间
     */
    private long lastClick = 0;


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
        Toasty.info(getActivity().getApplicationContext(), info, Toast.LENGTH_SHORT, true).show();
    }

    /**
     * toast SUCCESS
     *
     * @param success
     */
    public void showSuccess(String success) {
        Toasty.success(getActivity().getApplicationContext(), success, Toast.LENGTH_SHORT, true).show();
    }

    /**
     * toast ERROR
     *
     * @param error
     */
    public void showError(String error) {
        Toasty.error(getActivity().getApplicationContext(), error, Toast.LENGTH_SHORT, true).show();

    }

    @Override
    public void onClick(final View view) {
        if (!isFastClick()) onClickListener(view);
    }
}
