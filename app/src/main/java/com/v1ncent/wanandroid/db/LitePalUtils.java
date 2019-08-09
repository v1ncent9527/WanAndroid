package com.v1ncent.wanandroid.db;

import com.v1ncent.wanandroid.ui.user.pojo.UserBean;

import org.litepal.crud.DataSupport;

/**
 * ================================================
 * 作    者：v1ncent
 * 版    本：1.0.0
 * 创建日期：2018/4/28
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class LitePalUtils {
    private static LitePalUtils litePalUtils;

    public static LitePalUtils getInstance() {
        if (litePalUtils == null) {
            litePalUtils = new LitePalUtils();
        }
        return litePalUtils;
    }

    /**
     * 保存登录信息
     *
     * @param userBean
     * @return
     */
    public boolean saveUserInfo(UserBean userBean) {
        return userBean.save();
    }

    /**
     * 获取最近一次登录人信息
     *
     * @return
     */
    public UserBean getUserInfo() {
        return (UserBean) DataSupport.findLast(UserBean.class);
    }
}
