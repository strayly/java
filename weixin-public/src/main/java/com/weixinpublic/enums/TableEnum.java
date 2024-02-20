package com.weixinpublic.enums;

import lombok.Getter;


@Getter
public enum TableEnum implements com.job51.dev.common.enums.ErrorEnum {
    /**
     * 数据表 中的一些枚举
     * @param
     * @return
     * @author ruiguo.dong
     */
    WX_USER_SUBSCRIBE(1,"已关注"),
    WX_USER_UNSUBSCRIBE(2,"取消关注!")
    ;

    private Integer code;

    private String msg;

    @Override
    public String getCodeStr(){
        return this.code.toString();
    }

    TableEnum(Integer code, String msg){
        this.code = code;
        this.msg = msg;
    }
}
