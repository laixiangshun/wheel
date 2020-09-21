package com.lxs.websocket.model;

import lombok.Data;

/**
 * 服务端和客户端通信实体
 *
 * @author lxs
 */
@Data
public class ProtobufMsg {
    
    private String id;
    
    /**
     * 返回data对象的json字符串
     */
    private String body;
    
    private String msg;
}
