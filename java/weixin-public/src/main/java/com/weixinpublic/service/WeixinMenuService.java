package com.weixinpublic.service;

/**
 * @description：TODO
 * 微信公众号菜单
 */
public interface WeixinMenuService {
    //创建菜单
    int createMenu(String accessToken);
    //删除菜单
    int deleteMenu(String accessToken) ;
    //获取公众号菜单
    void getMenuInfo(String accessToken) ;
    String checkNet();
}
