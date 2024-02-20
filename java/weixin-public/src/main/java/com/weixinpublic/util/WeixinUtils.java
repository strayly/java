package com.weixinpublic.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.weixinpublic.config.weixin.WeixinConfig;
import com.weixinpublic.constant.WeixinApi;
import com.weixinpublic.entity.dto.weixin.AccessTokenDTO;
import com.qq.weixin.mp.aes.AesException;
import com.qq.weixin.mp.aes.WXBizMsgCrypt;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Arrays;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;


/**
 * @description：TODO
 */
@Slf4j
public class WeixinUtils {
    /**
     * 判断签名是否正确
     * @param token token 后台配置
     * @param signature 微信传参
     * @param timestamp 时间戳 微信传参
     * @param nonce 随机字符串 微信传参
     * @return
     */
    public static boolean  checkSignature(String token,String signature,String timestamp,String nonce)  {
        String checktext = null;
        if (null != signature) {
            checktext = signature(token,timestamp,nonce);
        }
        if( checktext.equals(signature.toUpperCase())){
            return true;
        }
        log.info("checktext:{},signature:{}",checktext,signature);
        return false;
    }
    /**
     * 根据token  时间戳 随机字符串 生成签名
     * @param items: token timestamp nonce
     * @return 返回签名字符串
     */
    public static String signature(String... items) {
        Arrays.sort(items, String.CASE_INSENSITIVE_ORDER);
        StringBuilder temp = new StringBuilder();
        for (String item : items) {
            temp.append(item);
        }
        return DigestUtils.sha1Hex(temp.toString()).toUpperCase();
    }

    /**
     * 将字节数组转化我16进制字符串
     * @param byteArrays 字符数组
     * @return 字符串
     */
    private static String byteToStr(byte[] byteArrays){
        String str = "";
        for (int i = 0; i < byteArrays.length; i++) {
            str += byteToHexStr(byteArrays[i]);
        }
        return str;
    }

    /**
     *  将字节转化为十六进制字符串
     * @param myByte 字节
     * @return 字符串
     */
    private static String byteToHexStr(byte myByte) {
        char[] Digit = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
        char[] tampArr = new char[2];
        tampArr[0] = Digit[(myByte >>> 4) & 0X0F];
        tampArr[1] = Digit[myByte & 0X0F];
        String str = new String(tampArr);
        return str;
    }
    //微信消息解密
    /*
    //微信返回的get 参数
    msgSignature 签名
    timestamp 时间戳
    nonce 随机字符串
     */
    public static String msgDecrypt(WeixinConfig weixinConfig, String msgSignature, String timestamp, String nonce, String encryptData) {
        String encodingAesKey = weixinConfig.getWxEncodingAESKey();
        String result = "";
        try {
            String toUser = weixinConfig.getWxAppName();
            String format = "<xml><ToUserName><![CDATA["+toUser+"]]></ToUserName><Encrypt><![CDATA[%1$s]]></Encrypt></xml>";
            String fromXML = String.format(format, encryptData);
            WXBizMsgCrypt pc = new WXBizMsgCrypt(weixinConfig.getWxToken(), encodingAesKey, weixinConfig.getWxAppId());
            result = pc.decryptMsg(msgSignature, timestamp, nonce, fromXML);
        }
        catch (AesException e) {
            log.error("消息解密异常:nonce={}，timestamp={}，nonce={}，msg_signature={},content={}\n异常：{}",nonce,timestamp,nonce,msgSignature,encryptData,e.getMessage());
        }
        //System.out.println("解密后明文: " + result);
        return result;
    }

    /**
     * 微信消息加密
     * @param weixinConfig 微信配置包含token，token，appId，encodingAesKey
     * @param content 待加密的字符串
     * @param timestamp 时间戳 传参
     * @param nonce 随机字符串 传参
     * @return 返回加密后的字符串
     */
    public static String msgCrypt(WeixinConfig weixinConfig,String content,String timestamp,String nonce) {

        String encodingAesKey = weixinConfig.getWxEncodingAESKey();
        String token = weixinConfig.getWxToken();
        String appId = weixinConfig.getWxAppId();
        String encrypt = "";
        try {
            WXBizMsgCrypt pc = new WXBizMsgCrypt(token, encodingAesKey, appId);
            encrypt = pc.encryptMsg(content, timestamp, nonce);
        } catch (AesException e) {
            log.error("消息加密异常:timestamp={}，nonce={}，content={}\n异常：{}",timestamp,nonce,content,e.getMessage());
        }
        return encrypt;
    }

    /**
     * 获取稳定版接口调用凭据
     * 参考 https://developers.weixin.qq.com/doc/offiaccount/Basic_Information/getStableAccessToken.html
     * @param weixinConfig
     * @return 返回access_token
     */
    public static AccessTokenDTO accessTokenStable(WeixinConfig weixinConfig){
        AccessTokenDTO accessToken = null;
        Map<String, String> params = new HashMap<>();
        params.put("grant_type","client_credential");
        params.put("appid",weixinConfig.getWxAppId());
        params.put("secret",weixinConfig.getWxAppsecret());
        //params.put("force_refresh","false");
        String json = null;
        try {
            json = new ObjectMapper().writeValueAsString(params);
            String accessResult = urlPost(WeixinApi.ACCESS_TOKEN_STABLE_URL,json);
            log.info("token info:{}",accessResult);
            accessToken = new ObjectMapper().readValue(accessResult, AccessTokenDTO.class);

        } catch (JsonProcessingException e) {
            log.error("微信access_token 获取json数据失败： msg={}", e.getMessage());
        }
        return accessToken;
    }
    /** 使用accessTokenStable()
     * 获取微信access_token
     * @param weixinConfig
     * @return 返回access_token
     */
    @Deprecated
    public static AccessTokenDTO accessToken(WeixinConfig weixinConfig){
        AccessTokenDTO accessToken = null;
        accessToken = accessTokenStable(weixinConfig);

        return accessToken;
    }
    /**
     * GET请求微信 https api 并获取结果
     * @param requestUrl
     * @return 返回URL响应结果
     */
    public static String urlRequest(String requestUrl){
        URL url = null;
        try {
            url = new URL(requestUrl);
            HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
            httpConnection.connect();
            InputStreamReader isr = new InputStreamReader(httpConnection.getInputStream());
            BufferedReader reader = new BufferedReader(isr);
            StringBuffer buffer = new StringBuffer();
            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }
            reader.close();
            isr.close();
            httpConnection.disconnect();
            return buffer.toString();
        }
        catch (IOException e) {
            log.error("微信api get 失败： msg={}", e.getMessage());
        }
        return null;
    }

    /**
     * url POST请求 post数据为json格式
     * @param requestUrl
     * @param json
     * @return 返回URL响应结果
     */
    public static String urlPost(String requestUrl,String json){
        URL url = null;
        try {
            url = new URL(requestUrl);
            HttpURLConnection  httpConnection = (HttpURLConnection) url.openConnection();
            httpConnection.setDoInput(true);
            httpConnection.setRequestMethod("POST");
            httpConnection.setRequestProperty("Charset", "UTF-8");
            httpConnection.setRequestProperty("Connection","keep-Alive");
            httpConnection.setRequestProperty("Content-Type", "application/json");
            httpConnection.setDoOutput(true);
            httpConnection.setDoInput(true);
            httpConnection.connect();

            PrintWriter pw = new PrintWriter(new BufferedOutputStream(httpConnection.getOutputStream()));
            pw.write(json);
            pw.flush();
            pw.close();

            InputStreamReader isr = new InputStreamReader(httpConnection.getInputStream());
            BufferedReader reader = new BufferedReader(isr);
            StringBuffer buffer = new StringBuffer();
            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }
            reader.close();
            isr.close();
            httpConnection.disconnect();
            return buffer.toString();

        }
        catch (IOException e) {
            log.error("微信POST 失败： msg={}", e.getMessage());
        }
        return null;
    }

}
