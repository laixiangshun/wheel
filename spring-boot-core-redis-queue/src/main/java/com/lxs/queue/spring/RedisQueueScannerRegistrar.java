package com.lxs.queue.spring;

import cn.hutool.core.util.ObjectUtil;
import com.lxs.queue.anotation.RedisQueueScanner;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 自动扫描自定义的自动装载注解
 *
 * @author lxs
 */
@Slf4j
public class RedisQueueScannerRegistrar implements ImportBeanDefinitionRegistrar, ResourceLoaderAware {
    
    private ResourceLoader resourceLoader;
    
    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }
    
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        AnnotationAttributes annotationAttributes = AnnotationAttributes.fromMap(importingClassMetadata.getAnnotationAttributes(RedisQueueScanner.class.getName()));
        if (ObjectUtil.isNull(annotationAttributes)) {
            throw new RuntimeException("RedisQueueScanner set basePackages is null");
            
        }
        ClassPathRedisQueueScanner scanner = new ClassPathRedisQueueScanner(registry);
        if (ObjectUtil.isNotNull(resourceLoader)) {
            scanner.setResourceLoader(resourceLoader);
        }
        List<String> basePackages = new ArrayList<>();
        String[] basePackagePaths = annotationAttributes.getStringArray("value");
        Arrays.stream(basePackagePaths).forEach(path -> {
            if (StringUtils.hasText(path)) {
                basePackages.add(path);
            }
        });
        basePackagePaths = annotationAttributes.getStringArray("basePackages");
        Arrays.stream(basePackagePaths).forEach(path -> {
            if (StringUtils.hasText(path)) {
                basePackages.add(path);
            }
        });
        scanner.doScan(StringUtils.toStringArray(basePackages));
    }
}
