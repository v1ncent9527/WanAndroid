package com.v1ncent.wanandroid.utils.eventbus;

/**
 * Created by v1ncent on 2017/10/27.
 * EventBus对象基类
 */

public class EventBean {
    private String origin;
    private Object object = new Object();

    public EventBean() {
    }

    public EventBean(String origin, Object object) {
        this.origin = origin;
        this.object = object;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    @Override
    public String toString() {
        return "EventBean{" +
                "origin='" + origin + '\'' +
                ", object=" + object +
                '}';
    }
}
