package com.heima.utils.threadlocal;

import com.heima.model.user.pojos.ApUser;

/**
 * 线程变量工具类
 */
public class AppThreadLocalUtils {
    private final static ThreadLocal<ApUser> userThreadLocal = new ThreadLocal<>();

    /**
     * 设置当前线程中的用户
     * @param user
     */
    public static void setUser(ApUser user) {
        userThreadLocal.set(user);
    }

    /**
     * 获取线程中的用户
     * @return
     */
    public static ApUser getUser() {
        return userThreadLocal.get();
    }

    public static void remove() {
        userThreadLocal.remove();
    }
}
