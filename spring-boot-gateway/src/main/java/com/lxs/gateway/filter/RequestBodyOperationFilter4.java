package com.lxs.gateway.filter;

import com.alibaba.fastjson.JSON;
import io.netty.buffer.ByteBufAllocator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.NettyDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.nio.charset.StandardCharsets;

/**
 * 获取request body内容方式4
 * 实现方式：在自定义RouteLocator 里面实现前置预言，会对body进行缓存，然后filter中通过exchange.getAttribute("cachedRequestBodyObject"); 获取body体
 *
 * @author lxs
 */
@Component
@Slf4j
public class RequestBodyOperationFilter4 implements GatewayFilter, Ordered {
    
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest serverHttpRequest = exchange.getRequest();
        HttpMethod method = serverHttpRequest.getMethod();
        URI requestUri = serverHttpRequest.getURI();
        if (method == HttpMethod.POST) {
            // 获取body体
            Object object = exchange.getAttribute("cachedRequestBodyObject");
            // todo 可以在这里对请求做各种权限验证，并且修改body
            String jsonString = JSON.toJSONString(object);
            
            //重新 封装request，传给下一级，由于post的body只能订阅一次，所以要再次封装请求到request   才行，不然会报错请求已经订阅过
            ServerHttpRequest request = serverHttpRequest.mutate().uri(requestUri).build();
            DataBuffer bodyDataBuffer = stringBuffer(jsonString);
            Flux<DataBuffer> bodyFlux = Flux.just(bodyDataBuffer);
            request = new ServerHttpRequestDecorator(request) {
                @Override
                public Flux<DataBuffer> getBody() {
                    return bodyFlux;
                }
            };
            // 特别注意，如果上面body长度经过修改，这边需要重新计算长度，以免下游服务获取body不完整
            request.mutate().header(HttpHeaders.CONTENT_LENGTH, Integer.toString(jsonString.length()));
            return chain.filter(exchange.mutate().request(request).build());
        } else if (method == HttpMethod.GET) {
            return chain.filter(exchange);
        }
        return chain.filter(exchange);
    }
    
    @Override
    public int getOrder() {
        return -1;
    }
    
    private DataBuffer stringBuffer(String value) {
        byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
        NettyDataBufferFactory nettyDataBufferFactory = new NettyDataBufferFactory(ByteBufAllocator.DEFAULT);
        DataBuffer buffer = nettyDataBufferFactory.allocateBuffer(bytes.length);
        buffer.write(bytes);
        return buffer;
    }
}
