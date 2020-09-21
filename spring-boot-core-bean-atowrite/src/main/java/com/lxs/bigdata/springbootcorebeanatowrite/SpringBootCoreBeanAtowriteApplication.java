package com.lxs.bigdata.springbootcorebeanatowrite;

import com.lxs.bigdata.springbootcorebeanatowrite.service.UserService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;

/**
 * 实现springboot的动态注入
 */
@SpringBootApplication
public class SpringBootCoreBeanAtowriteApplication implements ApplicationContextAware {

    private static ApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context=applicationContext;
    }

    public static void main(String[] args) {
        SpringApplication.run(SpringBootCoreBeanAtowriteApplication.class, args);
    }

    /**
     * 动态注入bean
     */
    @Bean
    public UserService userService(){
        DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) context.getAutowireCapableBeanFactory();
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(UserService.class);
        builder.addPropertyValue("name", "张三");
        beanFactory.registerBeanDefinition("userService", builder.getBeanDefinition());
        UserService userService = context.getBean(UserService.class);
        userService.print();
        return userService;
    }
}
