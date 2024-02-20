package com.weixinpublic.config.weixin;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 微信配置类
 * 从nacos读取配置
 */
@Configuration
@Slf4j
public class WeixinConfig {
    //微信公众号appid
    @Value("${wx.public.appid}")
    private String wxAppId;
    //微信公众号名称
    @Value("${wx.public.appname}")
    private String wxAppName;
    //微信公众号token
    @Value("${wx.public.token}")
    private String wxToken;
    //微信公众号EncodingAESKey
    @Value("${wx.public.encodingAESKey}")
    private String wxEncodingAESKey;
    //微信公众号secret
    @Value("${wx.public.appsecret}")
    private String wxAppsecret;
    @Bean
    public String getWxAppId() {
        return wxAppId;
    }
    @Bean
    public String getWxAppName() {
        return wxAppName;
    }
    @Bean
    public String getWxToken() {
        return wxToken;
    }
    @Bean
    public String getWxEncodingAESKey() {
        return wxEncodingAESKey;
    }
    @Bean
    public String getWxAppsecret() {
        return wxAppsecret;
    }


}

