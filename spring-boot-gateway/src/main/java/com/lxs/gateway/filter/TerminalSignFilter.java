package com.lxs.gateway.filter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lxs.gateway.utils.AESEncryptUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.support.BodyInserterContext;
import org.springframework.cloud.gateway.support.CachedBodyOutputMessage;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.HandlerStrategies;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Objects;

/**
 * 接口验签，解密
 *
 * @author lxs
 */
@Slf4j
@Component
public class TerminalSignFilter implements GatewayFilter, Ordered {
    
    private static final String AES_SECURTY = "XXX";
    
    private static final String MD5_SALT = "XXX";
    
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        exchange.getAttributes().put("startTime", System.currentTimeMillis());
        HttpMethod method = exchange.getRequest().getMethod();
        if (method == HttpMethod.POST) {
            //重新构造request，参考ModifyRequestBodyGatewayFilterFactory
            ServerRequest serverRequest = ServerRequest.create(exchange, HandlerStrategies.withDefaults().messageReaders());
            MediaType mediaType = exchange.getRequest().getHeaders().getContentType();
            //重点
            //因为约定了终端传参的格式，所以只考虑json的情况，如果是表单传参，请自行发挥
            Mono modifiedBody;
            if (MediaType.APPLICATION_JSON.isCompatibleWith(mediaType) || MediaType.APPLICATION_JSON_UTF8.isCompatibleWith(mediaType)) {
                modifiedBody = serverRequest.bodyToMono(String.class).flatMap(body -> {
                    JSONObject jsonObject = JSON.parseObject(body);
                    String paramStr = jsonObject.getString("param");
                    String newBody;
                    try {
                        newBody = verifySignature(paramStr);
                    } catch (Exception e) {
                        return processError(e.getMessage());
                    }
                    return Mono.just(newBody);
                });
            } else if (MediaType.APPLICATION_FORM_URLENCODED.isCompatibleWith(mediaType)) {
                modifiedBody = serverRequest.bodyToMono(Object.class).flatMap(body -> {
                    String bodyStr = JSON.toJSONString(body);
                    JSONObject jsonObject = JSON.parseObject(bodyStr);
                    String paramStr = jsonObject.getString("param");
                    String newBody;
                    try {
                        newBody = verifySignature(paramStr);
                    } catch (Exception e) {
                        return processError(e.getMessage());
                    }
                    return Mono.just(newBody);
                });
            } else {
                modifiedBody = Mono.empty();
            }
            BodyInserter bodyInserter = BodyInserters.fromPublisher(modifiedBody, String.class);
            HttpHeaders headers = new HttpHeaders();
            headers.putAll(exchange.getRequest().getHeaders());
            //猜测这个就是之前报400错误的元凶，之前修改了body但是没有重新写content length
            headers.remove("Content-Length");
            CachedBodyOutputMessage outputMessage = new CachedBodyOutputMessage(exchange, headers);
            return bodyInserter.insert(outputMessage, new BodyInserterContext()).then(Mono.defer(() -> {
                ServerHttpRequest decorator = this.decorate(exchange, headers, outputMessage);
                return returnMono(chain, exchange.mutate().request(decorator).build());
            }));
        } else {
            //GET 验签
            MultiValueMap<String, String> map = exchange.getRequest().getQueryParams();
            if (!CollectionUtils.isEmpty(map)) {
                String paramStr = map.getFirst("param");
                try {
                    verifySignature(paramStr);
                } catch (Exception e) {
                    return processError(e.getMessage());
                }
            }
            return returnMono(chain, exchange);
        }
    }
    
    @Override
    public int getOrder() {
        return 0;
    }
    
    private Mono<Void> returnMono(GatewayFilterChain chain, ServerWebExchange exchange) {
        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            Long startTime = exchange.getAttribute("startTime");
            if (startTime != null) {
                long executeTime = (System.currentTimeMillis() - startTime);
                log.info("耗时：{}ms", executeTime);
                log.info("状态码：{}", Objects.requireNonNull(exchange.getResponse().getStatusCode()).value());
            }
        }));
    }
    
    private String verifySignature(String paramStr) throws Exception {
        log.info("密文{}", paramStr);
        String dParamStr;
        try {
            dParamStr = AESEncryptUtil.decryptStr(paramStr, AES_SECURTY);
        } catch (Exception e) {
            throw new Exception("解密失败！");
        }
        log.info("解密得到字符串{}", dParamStr);
//        String signature = SignUtil.sign(dParamStr, MD5_SALT);
        String signature = "";
        log.info("重新加密得到签名{}", signature);
        JSONObject jsonObject1 = JSON.parseObject(dParamStr);
        if (!jsonObject1.getString("signature").equals(signature)) {
            throw new Exception("签名不匹配！");
        }
        return jsonObject1.toJSONString();
    }
    
    
    private Mono processError(String message) {
            /*exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();*/
        log.error(message);
        return Mono.error(new Exception(message));
    }
    
    private ServerHttpRequestDecorator decorate(ServerWebExchange exchange, HttpHeaders headers, CachedBodyOutputMessage outputMessage) {
        return new ServerHttpRequestDecorator(exchange.getRequest()) {
            public HttpHeaders getHeaders() {
                long contentLength = headers.getContentLength();
                HttpHeaders httpHeaders = new HttpHeaders();
                httpHeaders.putAll(super.getHeaders());
                if (contentLength > 0L) {
                    httpHeaders.setContentLength(contentLength);
                } else {
                    httpHeaders.set("Transfer-Encoding", "chunked");
                }
                return httpHeaders;
            }
            
            public Flux<DataBuffer> getBody() {
                return outputMessage.getBody();
            }
        };
    }
}
