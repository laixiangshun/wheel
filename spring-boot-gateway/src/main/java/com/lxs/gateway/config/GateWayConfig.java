package com.lxs.gateway.config;

import com.lxs.gateway.resolver.CustomeKeyResolver;
import com.lxs.gateway.resolver.HostAddrKeyResolver;
import com.lxs.gateway.resolver.UriKeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 网关配置
 *
 * @author lxs
 */
@Configuration
public class GateWayConfig {
    
    @Bean
    public HostAddrKeyResolver hostAddrKeyResolver() {
        return new HostAddrKeyResolver();
    }
    
    @Bean
    public UriKeyResolver uriKeyResolver() {
        return new UriKeyResolver();
    }
    
    @Bean
    public CustomeKeyResolver customeKeyResolver() {
        return new CustomeKeyResolver();
    }
}
