package com.lxs.ioc.config;

import com.lxs.ioc.annotation.XBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.io.IOException;
import java.util.Set;

/**
 * 自定义bean 扫描qi
 * 扫描器来注册被标记的Bean
 *
 * @author lxs
 */
@Slf4j
public class XBeanDefinitionScanner extends ClassPathBeanDefinitionScanner {
    
    public XBeanDefinitionScanner(BeanDefinitionRegistry registry) {
        super(registry);
    }
    
    public XBeanDefinitionScanner(BeanDefinitionRegistry registry, boolean useDefaultFilters) {
        super(registry, useDefaultFilters);
    }
    
    public XBeanDefinitionScanner(BeanDefinitionRegistry registry, boolean useDefaultFilters, Environment environment) {
        super(registry, useDefaultFilters, environment);
    }
    
    public XBeanDefinitionScanner(BeanDefinitionRegistry registry, boolean useDefaultFilters, Environment environment, ResourceLoader resourceLoader) {
        super(registry, useDefaultFilters, environment, resourceLoader);
    }
    
    @Override
    protected Set<BeanDefinitionHolder> doScan(String... basePackages) {
        //todo 自定义bean特殊处理
        return super.doScan(basePackages);
    }
    
    @Override
    protected boolean isCandidateComponent(MetadataReader metadataReader) throws IOException {
        return super.isCandidateComponent(metadataReader) || metadataReader.getAnnotationMetadata().hasAnnotation(XBean.class.getName());
    }
    
    @Override
    protected void registerDefaultFilters() {
        this.addIncludeFilter(new AnnotationTypeFilter(XBean.class));
    }
    
}
