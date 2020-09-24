package com.lxs.queue.enums;

import lombok.Getter;

/**
 * 消息发送策略
 *
 * @author lxs
 */
@Getter
public enum SendStrategyEn {
    SYNC("同步发送"),
    ASYNC("异步发送");
    
    private String desc;
    
    SendStrategyEn(String desc) {
        this.desc = desc;
    }
}
