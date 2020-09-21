package com.lxs.bigdata.node.core;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
public class SleuthController {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private SleuthService sleuthService;

    @ResponseBody
    @RequestMapping(value = "/restHello/{name}", method = RequestMethod.GET)
    public String restHello(@PathVariable(name = "name") String name) {
        return restTemplate.getForObject("http://localhost:8083/sayHello/" + name, String.class);
    }

    @ResponseBody
    @RequestMapping("/feignHello/{name}")
    public String feignHello(@PathVariable String name) {
        return "feign " + sleuthService.sayHello(name);
    }
}
