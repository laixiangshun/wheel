package com.lxs.websocket.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * netty配置属性
 *
 * @author lxs
 */
@Data
@ConfigurationProperties(prefix = "netty")
public class NettyConfigProperties {
    
    private Integer port;
    
    private Integer bossThread;
    
    private Integer workerThread;
    
    private Boolean keepalive;
    
    private Integer backlog;
}
