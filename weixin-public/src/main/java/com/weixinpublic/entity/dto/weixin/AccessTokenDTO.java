package com.weixinpublic.entity.dto.weixin;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @description：TODO

 * 微信acces_token 返回的数据
 */
@Data
public class AccessTokenDTO {
    //微信返回的access_token
    @JsonProperty("access_token")
    private String accessToken;
    //有效期
    @JsonProperty("expires_in")
    private String expiresIn;
}
