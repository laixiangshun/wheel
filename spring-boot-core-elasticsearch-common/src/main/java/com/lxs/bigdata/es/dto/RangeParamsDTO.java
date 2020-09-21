package com.lxs.bigdata.es.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "RangeParams", description = "范围查询字段信息")
public class RangeParamsDTO {

    @ApiModelProperty(value = "rangeField", name = "范围查询字段")
    private String rangeField;

    @ApiModelProperty(value = "start", name = "范围查询字段开始值")
    private Object start;

    @ApiModelProperty(value = "includeLower", name = "范围查询字段是否包含小写")
    private boolean includeLower = false;

    @ApiModelProperty(value = "end", name = "范围查询字段结束值")
    private Object end;

    @ApiModelProperty(value = "includeUpper", name = "范围查询字段是否包含大写")
    private boolean includeUpper = false;

    @ApiModelProperty(value = "format", name = "范围查询字段格式")
    private String format;
}
