package com.lxs.gateway.filter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.netty.buffer.ByteBufAllocator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.NettyDataBufferFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.nio.charset.StandardCharsets;

/**
 * 获取request body内容的方式3
 *
 * @author lxs
 * 缺点；原来body超过1024b会被截取，受版本限制
 */
@Slf4j
@Component
public class RequestBodyOperationFilter3 implements GlobalFilter, Ordered {
    
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        if (request.getMethod() != HttpMethod.POST) {
            return chain.filter(exchange);
        }
        ServerWebExchange serverWebExchange = requestExchange(exchange);
        return chain.filter(serverWebExchange);
    }
    
    @Override
    public int getOrder() {
        return -1;
    }
    
    private ServerWebExchange requestExchange(ServerWebExchange exchange) {
        ServerHttpRequest serverHttpRequest = exchange.getRequest();
        URI requestUri = serverHttpRequest.getURI();
        URI ex = UriComponentsBuilder.fromUri(requestUri).build(true).toUri();
        ServerHttpRequest newRequest = serverHttpRequest.mutate().uri(ex).build();
        
        // 获取body内容
        Flux<DataBuffer> body = serverHttpRequest.getBody();
        StringBuilder sb = new StringBuilder();
        body.subscribe(dataBuffer -> {
            byte[] bytes = new byte[dataBuffer.readableByteCount()];
            dataBuffer.read(bytes);
            DataBufferUtils.release(dataBuffer);
            String bodyString = new String(bytes, StandardCharsets.UTF_8);
            sb.append(bodyString);
        });
        
        String bodyStr = sb.toString();
        // 对原先的body进行修改操作
        JSONObject jsonObject = JSON.parseObject(bodyStr);
        //todo 修改或者取值
        String result = jsonObject.toJSONString();
        //转成字节
        byte[] bytes = result.getBytes(StandardCharsets.UTF_8);
        NettyDataBufferFactory nettyDataBufferFactory = new NettyDataBufferFactory(ByteBufAllocator.DEFAULT);
        DataBuffer bodyDataBuffer = nettyDataBufferFactory.allocateBuffer(bytes.length);
        bodyDataBuffer.write(bytes);
        
        Flux<DataBuffer> bodyFlux = Flux.just(bodyDataBuffer);
        newRequest = new ServerHttpRequestDecorator(newRequest) {
            @Override
            public Flux<DataBuffer> getBody() {
                return bodyFlux;
            }
        };
        return exchange.mutate().request(newRequest).build();
    }
    
}
