package com.v1ncent.wanandroid.api;

/**
 * Created by v1ncent on 2018/3/16.
 */

public class Urls {
    //首页banner
    public static String BANNER = "http://www.wanandroid.com/banner/json";
    //知识体系
    public static String KNOWLEDGESYSTEM = "http://www.wanandroid.com/tree/json";
    //分类
    public static String NAVIGATION = "http://www.wanandroid.com/friend/json";
    //分类
    public static String PROJECTCATEGORY = "http://www.wanandroid.com/project/tree/json";
    //登录
    public static String LOGIN = "http://www.wanandroid.com/user/login";
    //注册
    public static String REGISTER = "http://www.wanandroid.com/user/register";

    //首页数据
    public static String getIndexUrl(int index) {
        return "http://www.wanandroid.com/article/list/" + index + "/json";
    }

    //文章Url
    public static String getArticleUrl(int position, int id) {
        return "http://www.wanandroid.com/article/list/" + position + "/json?cid=" + id;
    }

    //项目Url
    public static String getProjectUrl(int position, int id) {
        return "http://www.wanandroid.com/project/list/" + position + "/json?cid=" + id;
    }

    //搜索
    public static String getSearchUrl(int index) {
        return "http://www.wanandroid.com/article/query/" + index + "/json";
    }


}
