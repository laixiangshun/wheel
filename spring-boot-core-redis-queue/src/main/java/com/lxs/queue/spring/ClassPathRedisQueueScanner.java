package com.lxs.queue.spring;

import com.lxs.queue.anotation.RedisQueue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.util.Arrays;
import java.util.Set;

/**
 * 自定义bean 扫描器
 * 限定扫描带有RedisQueue.class annotation 的类
 */
@Slf4j
public class ClassPathRedisQueueScanner extends ClassPathBeanDefinitionScanner {
    
    public ClassPathRedisQueueScanner(BeanDefinitionRegistry registry) {
        super(registry);
    }
    
    public ClassPathRedisQueueScanner(BeanDefinitionRegistry registry, boolean useDefaultFilters) {
        super(registry, useDefaultFilters);
    }
    
    public ClassPathRedisQueueScanner(BeanDefinitionRegistry registry, boolean useDefaultFilters, Environment environment) {
        super(registry, useDefaultFilters, environment);
    }
    
    public ClassPathRedisQueueScanner(BeanDefinitionRegistry registry, boolean useDefaultFilters, Environment environment, ResourceLoader resourceLoader) {
        super(registry, useDefaultFilters, environment, resourceLoader);
    }
    
    @Override
    protected Set<BeanDefinitionHolder> doScan(String... basePackages) {
        Set<BeanDefinitionHolder> beanDefinitionHolders = super.doScan(basePackages);
        if (beanDefinitionHolders.isEmpty()) {
            logger.warn("No Redis Queues was found in '" + Arrays.toString(basePackages) + "' package. Please check your configuration.");
        } else {
            this.processBeanDefinitions(beanDefinitionHolders);
        }
        return beanDefinitionHolders;
    }
    
    private void processBeanDefinitions(Set<BeanDefinitionHolder> beanDefinitionHolders) {
        GenericBeanDefinition genericBeanDefinition;
        for (BeanDefinitionHolder beanDefinitionHolder : beanDefinitionHolders) {
            BeanDefinition beanDefinition = beanDefinitionHolder.getBeanDefinition();
            genericBeanDefinition = (GenericBeanDefinition) beanDefinition;
            MutablePropertyValues propertyValues = genericBeanDefinition.getPropertyValues();
            //注入interface时确保注入的是代理
            propertyValues.add("interfaceClassName", genericBeanDefinition.getBeanClassName());
            //通过factorybean来生成代理对象
            genericBeanDefinition.setBeanClass(QueueFactoryBean.class);
        }
    }
    
    @Override
    protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
        return super.isCandidateComponent(beanDefinition) || beanDefinition.getMetadata().hasAnnotation(RedisQueue.class.getName());
    }
    
    @Override
    protected void registerDefaultFilters() {
        //添加需要取的注解类
        this.addExcludeFilter(new AnnotationTypeFilter(RedisQueue.class));
    }
}
