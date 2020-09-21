package com.lxs.bigdata.es.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "SortParams", description = "排序字段信息")
public class SortParamsDTO {

    @ApiModelProperty(value = "sortField", name = "排序字符")
    String sortField;

    @ApiModelProperty(value = "sortOrder", name = "排序方式")
    String sortOrder;
}
