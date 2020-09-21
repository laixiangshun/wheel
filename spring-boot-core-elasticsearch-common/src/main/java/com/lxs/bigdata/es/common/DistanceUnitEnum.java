package com.lxs.bigdata.es.common;

import lombok.Getter;

import java.io.Serializable;
import java.util.Locale;

/**
 * 距离单位枚举
 *
 * @author lxs
 */
@Getter
public enum DistanceUnitEnum implements Serializable {

    /**
     * 米
     */
    M("m", "米"),
    /**
     * 千米
     */
    KM("km", "千米");

    private String unit;

    private String name;

    DistanceUnitEnum(String unit, String name) {
        this.unit = unit;
        this.name = name;
    }

    public static DistanceUnitEnum fromString(String op) {
        return valueOf(op.toUpperCase(Locale.ROOT));
    }
}

