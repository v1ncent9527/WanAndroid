package com.v1ncent.wanandroid.utils.eventbus;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by v1ncent on 2017/10/27.
 * EventBus工具类
 */

public class EventBusUtils {
    /**
     * 注册EventBus
     *
     * @param object
     */
    public static void register(Object object) {
        if (!EventBus.getDefault().isRegistered(object)) {
            EventBus.getDefault().register(object);
        }
    }


    /**
     * 反注册EventBus
     *
     * @param object
     */
    public static void unRegister(Object object) {
        if (EventBus.getDefault().isRegistered(object)) {
            EventBus.getDefault().unregister(object);
        }
    }

    /**
     * 通过origin（来源）区分消息
     *
     * @param origin
     * @param object
     */
    public static void postEvent(String origin, Object object) {
        EventBus.getDefault().post(new EventBean(origin, object));
    }

}
