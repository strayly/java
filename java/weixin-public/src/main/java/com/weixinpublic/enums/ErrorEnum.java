package com.weixinpublic.enums;

import lombok.Getter;

@Getter
public enum ErrorEnum implements com.job51.dev.common.enums.ErrorEnum {
    /**
     * 异常枚举值
     * @param
     * @return
     */
    PARAM_ERROR(0,"参数错误!"),
    //SYSTEM_ERROR(100000, "网络超时，请稍后重试！"),
    PARAM_NULL_ERROR(100001, "参数为空异常！"),
    MYSQL_SESSION_FACTORY_INITIALIZE_ERROR(400003,"构建MySQLSessionFactoryBean失败"),

    WX_CHECK_SIGNATURE_ERROR(731000, "验证失败！"),
    ;

    private Integer code;

    private String msg;

    @Override
    public String getCodeStr(){
        return this.code.toString();
    }

    ErrorEnum(Integer code, String msg){
        this.code = code;
        this.msg = msg;
    }
}
