package com.v1ncent.wanandroid.ui.user.pojo;

import org.litepal.crud.DataSupport;

import java.io.Serializable;
import java.util.List;

/**
 * ================================================
 * 作    者：v1ncent
 * 版    本：1.0.0
 * 创建日期：2018/3/28
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class UserBean extends DataSupport implements Serializable {

    /**
     * collectIds : []
     * email :
     * icon :
     * id : 4181
     * password : v1ncent
     * type : 0
     * username : 舟舟努蜡33
     */

    private UserBean.DataBean data;
    /**
     * data : {"collectIds":[],"email":"","icon":"","id":4181,"password":"v1ncent","type":0,"username":"舟舟努蜡33"}
     * errorCode : 0
     * errorMsg :
     */

    private int errorCode;
    private String errorMsg;

    public UserBean.DataBean getData() {
        return data;
    }

    public void setData(UserBean.DataBean data) {
        this.data = data;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public static class DataBean implements Serializable{
        private String email;
        private String icon;
        private int id;
        private String password;
        private int type;
        private String username;
        private List<?> collectIds;

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public List<?> getCollectIds() {
            return collectIds;
        }

        public void setCollectIds(List<?> collectIds) {
            this.collectIds = collectIds;
        }

        @Override
        public String toString() {
            return "DataBean{" +
                    "email='" + email + '\'' +
                    ", icon='" + icon + '\'' +
                    ", id=" + id +
                    ", password='" + password + '\'' +
                    ", type=" + type +
                    ", username='" + username + '\'' +
                    ", collectIds=" + collectIds +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "RegisterResultBean{" +
                "data=" + data.toString() +
                ", errorCode=" + errorCode +
                ", errorMsg='" + errorMsg + '\'' +
                '}';
    }
}
