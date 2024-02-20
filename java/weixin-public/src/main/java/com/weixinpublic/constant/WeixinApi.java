package com.weixinpublic.constant;

public class WeixinApi {
     /**
     * 获取access_token的接口地址（GET） ,不再使用，请使用ACCESS_TOKEN_STABLE_URL
     */
    public static final String ACCESS_TOKEN_URL = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=APPSECRET";
    /**
     * 获取稳定版接口调用凭据
     * https://developers.weixin.qq.com/doc/offiaccount/Basic_Information/getStableAccessToken.html
     * POST JSON 形式的调用,参数:grant_type, appid, secret,force_refresh
     *
     */
    public static final String ACCESS_TOKEN_STABLE_URL = "https://api.weixin.qq.com/cgi-bin/stable_token";

    /**
     * 自定义菜单删除接口
     */
    public static final String MENU_DELETE_URL = "https://api.weixin.qq.com/cgi-bin/menu/delete?access_token=ACCESS_TOKEN";

    /**
     * 自定义菜单的创建接口
     */
    public static final String MENU_CREATE_URL = "https://api.weixin.qq.com/cgi-bin/menu/create?access_token=ACCESS_TOKEN";

    /**
     * 自定义菜单的查询接口
     */
    public static final String MENU_GET_URL = "https://api.weixin.qq.com/cgi-bin/menu/get?access_token=ACCESS_TOKEN";
    /**
     * 微信接口网络检测
     */
    public static final String NET_CHECK_URL = "https://api.weixin.qq.com/cgi-bin/callback/check?access_token=ACCESS_TOKEN";
}
