package com.lxs.bigdata.es.dto;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * elasticsearch 批量操作条件
 *
 * @author lxs
 */
@Data
@Slf4j
public class ESBulkConditionDTO {

    private String index;

    private String type;

    /**
     * 操作类型，0：add，1：update，2：delete
     */
    private Integer operationType;

    private String json;
}
