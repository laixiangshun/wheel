package com.lxs.queue.spring;

import com.lxs.queue.wrapper.QueueWrapper;
import com.lxs.queue.wrapper.QueueWrapperFactory;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.FactoryBean;

/**
 * 队列Interface代理的实现工厂
 *
 * @param <T>
 * @author lxs
 */
@Getter
@Slf4j
public class QueueFactoryBean<T> implements FactoryBean<T> {
    
    private String interfaceClassName;
    
    @Override
    public T getObject() throws Exception {
        Class<?> aClass = Class.forName(interfaceClassName);
        if (aClass.isInterface()) {
            QueueWrapper queueWrapper = QueueWrapperFactory.INSTANCE.instance(aClass);
            return queueWrapper.getInstance((Class<T>) aClass);
        } else {
            throw new Exception("The Class [" + this.interfaceClassName + "] is not a interface, please check your code.");
        }
    }
    
    @Override
    public Class<?> getObjectType() {
        try {
            if (StringUtils.isBlank(this.interfaceClassName)) {
                return null;
            }
            return Class.forName(this.interfaceClassName);
        } catch (ClassNotFoundException e) {
            log.error("Class [" + this.interfaceClassName + "] is not found.", e);
            return null;
        }
    }
    
    @Override
    public boolean isSingleton() {
        return true;
    }
}
