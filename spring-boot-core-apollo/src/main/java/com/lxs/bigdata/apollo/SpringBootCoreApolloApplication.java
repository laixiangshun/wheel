package com.lxs.bigdata.apollo;

import com.ctrip.framework.apollo.spring.annotation.EnableApolloConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableApolloConfig
public class SpringBootCoreApolloApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootCoreApolloApplication.class, args);
    }

}
