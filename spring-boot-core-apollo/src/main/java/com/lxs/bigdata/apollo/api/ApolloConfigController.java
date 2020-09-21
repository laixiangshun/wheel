package com.lxs.bigdata.apollo.api;

import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.ConfigService;
import com.ctrip.framework.apollo.model.ConfigChangeEvent;
import com.ctrip.framework.apollo.spring.annotation.ApolloConfig;
import com.ctrip.framework.apollo.spring.annotation.ApolloConfigChangeListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
public class ApolloConfigController {

    @ApolloConfig
    private Config config;

    @Value("${name}")
    private String name;

    @RequestMapping("/apollo")
    public String getConfig() {
        return name;
    }

    @ApolloConfigChangeListener
    public void onChange(ConfigChangeEvent event) {
        Set<String> changedKeys = event.changedKeys();
        if (!CollectionUtils.isEmpty(changedKeys)) {
            for (String key : changedKeys) {
                System.out.println("key：" + key + "配置变化");
            }
        }
        Config appConfig = ConfigService.getAppConfig();
        String name = appConfig.getProperty("name", "");
        System.out.println(name);
    }
}
