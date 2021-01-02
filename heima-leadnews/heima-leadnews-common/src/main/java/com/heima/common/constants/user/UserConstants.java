package com.heima.common.constants.user;

public class UserConstants {
    //审核通过
    public static final Short PASS_AUTH = 9;
    //审核失败，驳回
    public static final Short FAIL_AUTH = 2;

    public static final Integer AUTH_TYPE = 2;
    /*自媒体账户状态
        0 暂时不可用
        1 永久不可用
        9 正常可用
    */
    public static final Integer WM_STATUS_UNAVAILABLE=0;
    public static final Integer WM_STATUS_UNAVAILABLE_NEVER=1;
    public static final Integer WM_STATUS_AVAILABLE=9;
    /**
     * 发布文章作者类型
     0 爬取数据
     1 签约合作商
     2 平台自媒体人
     */
    public static final Integer AUTH_TYPE_PYTHON = 0;
    public static final Integer AUTH_TYPE_SIGN = 1;
    public static final Integer AUTH_TYPE_SELF = 2;
}
