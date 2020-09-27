package com.lxs.ioc.config;

import com.lxs.ioc.annotation.EnableMapperScan;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * 自定义注册器
 *
 * @author lxs
 */
public class XBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar, ResourceLoaderAware {
    
    private ResourceLoader resourceLoader;
    
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        String name = EnableMapperScan.class.getName();
        AnnotationAttributes annotationAttributes = AnnotationAttributes.fromMap(importingClassMetadata.getAnnotationAttributes(name, true));
        Assert.notNull(annotationAttributes, () -> "No auto-configuration attributes found. Is " + importingClassMetadata.getClassName()
                + " annotated with " + ClassUtils.getShortName(name) + "?");
        
        XBeanDefinitionScanner scanner = new XBeanDefinitionScanner(registry);
        if (!ObjectUtils.isEmpty(resourceLoader)) {
            scanner.setResourceLoader(resourceLoader);
        }
        Set<String> backageList = new HashSet<>(annotationAttributes.size());
        String[] valueList = annotationAttributes.getStringArray("value");
        backageList.addAll(Arrays.asList(valueList));
        String[] basePackages = annotationAttributes.getStringArray("basePackage");
        backageList.addAll(Arrays.asList(basePackages));
        scanner.doScan(backageList.toArray(new String[]{}));
    }
    
    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }
}
