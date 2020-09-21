package com.lxs.gateway.rateLimiter;

import org.springframework.cloud.gateway.filter.ratelimit.RateLimiter;
import reactor.core.publisher.Mono;

import java.util.Map;

public class HostNameRateLimiter implements RateLimiter {
    
    @Override
    public Mono<Response> isAllowed(String routeId, String id) {
        return null;
    }
    
    @Override
    public Map getConfig() {
        return null;
    }
    
    @Override
    public Class getConfigClass() {
        return null;
    }
    
    @Override
    public Object newConfig() {
        return null;
    }
}
