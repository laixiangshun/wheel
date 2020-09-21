package com.lxs.websocket.model;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 连接的用户信息
 *
 * @author lxs
 */
@Data
public class UserMsg implements Serializable {
    
    private static final long serialVersionUID = -2894370175857209820L;
    
    private String id;
    
    private String name;
    
    private String msg;
    
    private Date createTime;
    
    private Date updateTime;
}
