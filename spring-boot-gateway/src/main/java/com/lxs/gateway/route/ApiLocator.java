package com.lxs.gateway.route;

import com.lxs.gateway.filter.RequestBodyOperationFilter4;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

/**
 * 自定义路由
 *
 * @author lxs
 */
@Configuration
@Slf4j
public class ApiLocator {
    
    @Autowired
    private RequestBodyOperationFilter4 requestFilter;
    
    @Value("${server.context.path:'/'}")
    private String contextPath;
    
    /**
     * route1 是get请求，get请求使用readBody会报错
     * route2 是post请求，Content-Type是application/x-www-form-urlencoded，readbody为String.class
     * route3 是post请求，Content-Type是application/json,readbody为Object.class
     */
    @Bean
    public RouteLocator myRoutes(RouteLocatorBuilder builder) {
        RouteLocatorBuilder.Builder routes = builder.routes();
        RouteLocatorBuilder.Builder serviceProvider = routes
                .route("route1", r -> r
                        .method(HttpMethod.GET)
                        .and()
                        .path(contextPath.concat("/test/**"))
                        .filters(f -> {
                            f.filter(requestFilter);
                            return f;
                        })
                        .uri("lb://".concat("ORDER_SERVICE")))
                .route("route2", r -> r
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                        .and()
                        .method(HttpMethod.POST)
                        .and()
                        .readBody(String.class, readBody -> {
                            // 这里 r.readBody做了一个前置语言，这样就可以在filter中通过exchange.getAttribute("cachedRequestBodyObject"); 获取body体
                            log.info("request method POST, Content-Type is application/x-www-form-urlencoded, body  is:{}", readBody);
                            return true;
                        })
                        .and()
                        .path(contextPath.concat("/test/**"))
                        .filters(f -> {
                            f.filter(requestFilter);
                            return f;
                        })
                        .uri("lb://".concat("ORDER_SERVICE")))
                .route("route3", r -> r
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .and()
                        .method(HttpMethod.POST)
                        .and()
                        .readBody(Object.class, readBody -> {
                            log.info("request method POST, Content-Type is application/json, body  is:{}", readBody);
                            return true;
                        })
                        .and()
                        .path(contextPath.concat("/test/**"))
                        .filters(f -> {
//                            f.stripPrefix(1)
//                                    .requestRateLimiter(c->c.setRateLimiter())
                            f.filter(requestFilter);
                            return f;
                        })
                        .uri("lb://".concat("ORDER_SERVICE")));
        RouteLocator routeLocator = serviceProvider.build();
        log.info("custom RouteLocator is loading ... {}", routeLocator);
        return routeLocator;
    }
}
