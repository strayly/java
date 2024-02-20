package com.weixinpublic.entity.bo;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;


//微信关注用户数据表
@TableName(value = "weixin_user")
@Data
public class WxPublicUser {

    @TableId(type = IdType.INPUT, value = "ID")
    private Long id;

    /**
     * 微信openid
     */
    @TableField("OPENID")
    private String openid;

    /**
     * 事件时间
     */
    @TableField("CREATETIME")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField("UPDATETIME")
    private LocalDateTime updateTime;


    /**
     * 状态1 关注 2 取消
     */
    @TableField("STATUS")
    private String status;




}
