package com.lxs.gateway.resolver;

import cn.hutool.core.util.StrUtil;
import com.htwz.core.util.crypto.JwtTokenUtil;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;

/**
 * 自定义纬度进度限流
 *
 * @author lxs
 */
public class CustomeKeyResolver implements KeyResolver {
    
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    
    @Override
    public Mono<String> resolve(ServerWebExchange exchange) {
        ServerHttpRequest exchangeRequest = exchange.getRequest();
        HttpHeaders headers = exchangeRequest.getHeaders();
        String token = headers.getFirst("token");
        if (StrUtil.isBlank(token)) {
            token = exchangeRequest.getQueryParams().getFirst("token");
        }
        if (StrUtil.isBlank(token)) {
            return Mono.empty();
        }
        if (exchangeRequest.getMethod().matches(HttpMethod.OPTIONS.name())) {
            return Mono.empty();
        }
        URI uri = exchangeRequest.getURI();
        String path = uri.getPath();
        if (path.contains("swagger") || path.contains("error")) {
            return Mono.empty();
        }
        Claims claims = jwtTokenUtil.getClaimFromToken(token);
        String userId = claims.getIssuer();
        return Mono.just(userId);
    }
}
