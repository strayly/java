package com.weixinpublic.service.impl;

import com.weixinpublic.WeixinPublicApplication;
import com.weixinpublic.config.weixin.WeixinConfig;
import com.weixinpublic.entity.dto.weixin.AccessTokenDTO;
import com.weixinpublic.util.WeixinUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.annotation.Resource;

import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @description：TODO
 */
@SpringBootTest(classes = WeixinPublicApplication.class)
@ActiveProfiles("dev")
@RunWith(SpringRunner.class)
public class WeixinMenuDTOServiceImplTest {
    @Resource
    private WeixinConfig weixinConfig;
    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void createMenu() {
    }

    @Test
    public void deleteMenu() {
    }
    @Test
    public void initMenu(){
        //https://api.weixin.qq.com/cgi-bin/callback/check?access_token=
        WeixinMenuServiceImpl weixinMenuService = new WeixinMenuServiceImpl();
        AccessTokenDTO at = WeixinUtils.accessToken(weixinConfig);
        weixinMenuService.getMenuInfo(at.getAccessToken());
        //
        weixinMenuService.createMenu(at.getAccessToken());
    }
    /**
     * 创建自定义菜单，目前先直接使用json文件 ，暂不使用代码程序生成
    private WeixinMenuDTO getMenu(){
        WeixinMenuDTO menu = new WeixinMenuDTO();
        // 子按钮（菜单）
        ViewButtonDTO btn11 = new ViewButtonDTO();
        btn11.setName("");
        btn11.setType("view");
        btn11.setUrl("");

        ClickButtonDTO btn12 = new ClickButtonDTO();
        btn12.setName("");
        btn12.setType("miniprogram");
        btn12.setPagePath("");
        btn12.setAppid("");

        ViewButtonDTO btn13 = new ViewButtonDTO();
        btn13.setName("");
        btn13.setType("view");
        btn13.setUrl("");

        ViewButtonDTO btn14 = new ViewButtonDTO();
        btn14.setName("好文");
        btn14.setType("view");
        btn14.setUrl("");

        //
        ViewButtonDTO btn21 = new ViewButtonDTO();
        btn21.setName("APP下载");
        btn21.setType("view");
        btn21.setUrl("");



        ViewButtonDTO btn31 = new ViewButtonDTO();
        btn31.setName("绑定账号");
        btn31.setType("view");
        btn31.setUrl("");

        ClickButtonDTO btn32 = new ClickButtonDTO();
        btn32.setName("反馈");
        btn32.setPagePath("");
        btn32.setAppid("");

        ClickButtonDTO btn33 = new ClickButtonDTO();
        btn33.setName("我的");
        btn33.setPagePath("");
        btn33.setAppid("");

        ViewButtonDTO btn34 = new ViewButtonDTO();
        btn34.setName("合作");
        btn34.setType("view");
        btn34.setUrl("");

        // 一级菜单
        ComplexButtonDTO mainBtn1 = new ComplexButtonDTO();
        mainBtn1.setName("");
        mainBtn1.setSubButton(new BasicButtonDTO[]{btn11, btn12, btn13, btn14});

        ViewButtonDTO mainBtn2 = new ViewButtonDTO();
        mainBtn2.setName("");
        mainBtn2.setType("view");
        mainBtn2.setUrl("");


        ComplexButtonDTO mainBtn3 = new ComplexButtonDTO();
        mainBtn3.setName("我的");
        mainBtn3.setSubButton(new BasicButtonDTO[]{btn31, btn32, btn33, btn34});


        menu.setButton(new BasicButtonDTO[]{mainBtn1, mainBtn2, mainBtn3});
        return menu;
    }
    */

}