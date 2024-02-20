package com.weixinpublic.mapper.mysql;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.weixinpublic.entity.bo.WxPublicUser;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

//微信用户表，保存微信openid和关注状态

@Mapper
public interface WxPublicUserMapper extends BaseMapper<WxPublicUser> {
    //根据openid查询用户信息
    @Select("SELECT * FROM weixin_user WHERE OPENID = #{openid}")
	WxPublicUser getUserInfo(String openid,String product);
    //插入用户信息
    @Insert("INSERT INTO weixin_user(OPENID,CREATETIME,UPDATETIME,STATUS) VALUES(#{openid},#{createTime},#{updateTime},#{status})")
    int insertUser(WxPublicUser user);
    //更新用户信息
    @Update("UPDATE weixin_user SET STATUS = #{status},UPDATETIME=#{updateTime} WHERE OPENID = #{openid}")
    int updateUser(WxPublicUser user);

}
