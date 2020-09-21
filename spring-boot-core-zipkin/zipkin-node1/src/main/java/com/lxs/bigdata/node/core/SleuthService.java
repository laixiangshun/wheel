package com.lxs.bigdata.node.core;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient(name = "zipkin-node1", url = "http://localhost:8083")
public interface SleuthService {

    @RequestMapping("/sayHello/{name}")
    String sayHello(@PathVariable(name = "name") String name);
}
