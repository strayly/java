package com.weixinpublic.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.weixinpublic.config.weixin.WeixinConfig;
import com.weixinpublic.entity.dto.weixin.AccessTokenDTO;
import com.weixinpublic.constant.WeixinApi;
import com.weixinpublic.service.WeixinMenuService;
import lombok.extern.slf4j.Slf4j;
import com.weixinpublic.util.WeixinUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;

/**
 * @description：TODO
 * 微信自定义菜单核心服务实现类
 */
@Service
@Slf4j
public class WeixinMenuServiceImpl implements WeixinMenuService {
    @Resource
    private WeixinConfig weixinConfig;
    @Override
    public int createMenu(String accessToken) {
        log.info("开始创建菜单");
        int result = 0;
        // MENU_CREATE_URL菜单创建（POST） 限100（次/天）
        // 拼装创建菜单的url
        String url = WeixinApi.MENU_CREATE_URL.replace("ACCESS_TOKEN", accessToken);
        ObjectMapper objectMapper = new ObjectMapper();
        String json = null;

        //直接使用json文件创建菜单
        json = readJsonFromFile();
        log.info("通过文件创建的菜单json :{}", json);
        //提交到微信接口
        String postResult = WeixinUtils.urlPost(url,json);
        log.info("创建菜单结果 :{}", postResult);

        return result;
    }
    private String readJsonFromFile(){
        //从json文件读取数据
        try {
            InputStream inputStream = WeixinMenuServiceImpl.class.getClassLoader().getResourceAsStream("weixin_menu.json");
            byte[] bytes =  new byte[inputStream.available()];
            inputStream.read(bytes);
            return new String(bytes);
        } catch (IOException e) {
            log.error(" 获取json文件数据失败： msg={}", e.getMessage());
        }
        return null;
    }
    @Override
    public void getMenuInfo( String accessToken) {
        // MENU_CREATE_URL菜单创建（POST） 限100（次/天）
        // 拼装创建菜单的url
        String url = WeixinApi.MENU_GET_URL.replace("ACCESS_TOKEN", accessToken);
        ObjectMapper objectMapper = new ObjectMapper();
        String result = WeixinUtils.urlRequest(url);
        log.info("创建菜单结果 :{}", result);

    }
    /**
     * 删除菜单
     * 对应创建接口，正确的Json返回结果:
     * {"errcode":0,"errmsg":"ok"}
     *
     * @param accessToken 有效的access_token
     * @return 0表示成功，其他值表示失败
     */
    @Override
    public int deleteMenu(String accessToken) {
        int result = 0;
        String url = WeixinApi.MENU_DELETE_URL.replace("ACCESS_TOKEN", accessToken);
        String deleteResult = WeixinUtils.urlRequest(url);
        log.error("deleteResult： msg={}", deleteResult);

        return result;
    }
    /**
     * 微信接口通信网络检测
     * 对应创建接口，正确的Json返回结果:
     * {"errcode":0,"errmsg":"ok"}
     * @return 返回的json数据
     */
    @Override
    public String checkNet() {
        String accessToken = getAccesstoken();
        String result = "";
        String url = WeixinApi.NET_CHECK_URL.replace("ACCESS_TOKEN", accessToken);
        String json = "{\"action\": \"all\", \"check_operator\": \"DEFAULT\"}";
        result = WeixinUtils.urlPost(url,json);
        log.info("weixin net check： msg={}", result);
        return result;
    }
    //获取accesstoken
    private String getAccesstoken(){
        AccessTokenDTO at = WeixinUtils.accessToken(weixinConfig);
        String accessToken = at.getAccessToken();
        log.info("weixin accessToken： msg={}", accessToken);
        return accessToken;
    }

}
