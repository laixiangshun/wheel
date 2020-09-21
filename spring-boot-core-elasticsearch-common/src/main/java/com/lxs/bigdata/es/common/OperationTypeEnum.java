package com.lxs.bigdata.es.common;

import lombok.Getter;
import org.springframework.util.CollectionUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

/**
 * 操作类型枚举
 *
 * @author lxs
 */
@Getter
public enum OperationTypeEnum {

    /**
     * 添加操作
     */
    ADD(0, "添加"),
    /**
     * 更新操作
     */
    UPDATE(1, "更新"),
    /**
     * 删除操作
     */
    DELETE(2, "删除");

    private Integer code;

    private String desc;

    OperationTypeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
        Cache.cacheMap.put(code, this);
    }

    private static class Cache {
        private static final Map<Integer, OperationTypeEnum> cacheMap = new ConcurrentHashMap<>();
    }

    public static OperationTypeEnum getOperationType(Integer code) {
        AtomicReference<OperationTypeEnum> operationType = new AtomicReference<>();
        Map<Integer, OperationTypeEnum> cacheMap = Cache.cacheMap;
        if (!CollectionUtils.isEmpty(cacheMap)) {
            operationType.set(cacheMap.getOrDefault(code, null));
        }
        if (operationType.get() == null) {
            Stream.of(OperationTypeEnum.values()).forEach(type -> {
                Integer typeCode = type.getCode();
                if (code.equals(typeCode)) {
                    operationType.set(type);
                }
            });
        }
        return operationType.get();
    }
}
