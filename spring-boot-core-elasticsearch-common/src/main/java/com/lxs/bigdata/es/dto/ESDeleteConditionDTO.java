package com.lxs.bigdata.es.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "ESDeleteConditionDTO", description = "elasticsearch删除入参")
public class ESDeleteConditionDTO {

    /**
     * 索引
     */
    @ApiModelProperty(value = "index", name = "索引名称", required = true)
    @NotBlank(message = "索引不能为空")
    private String index;
    /**
     * 类型
     */
    @ApiModelProperty(value = "type", name = "类型", required = true)
    @NotBlank(message = "索引类型不能为空")
    private String type;

    @ApiModelProperty(value = "idList", name = "id集合", required = true)
    @NotEmpty(message = "id不能为空")
    private List<String> idList;
}
