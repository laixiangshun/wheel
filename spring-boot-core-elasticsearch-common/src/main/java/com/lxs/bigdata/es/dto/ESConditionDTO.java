package com.lxs.bigdata.es.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "ESCondition", description = "搜索条件")
public class ESConditionDTO {
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
    /**
     * 范围查询
     */
    @ApiModelProperty(value = "rangeList", name = "范围查询")
    private List<RangeParamsDTO> rangeList;
    /**
     * 指定查询哪些字段（用户非聚合查询）
     * 格式：field1,field2
     */
    @ApiModelProperty(value = "searchFields", name = "指定查询哪些字段,格式：field1,field2")
    private String searchFields;
    /**
     * 聚合统计
     * 格式：field1:value2,field2:value2
     */
    @ApiModelProperty(value = "countFields", name = "聚合统计字段,格式：field1:value2,field2:value2")
    private String countFields;
    /**
     * 聚合求和
     * 格式：field1:value2,field2:value2
     */
    @ApiModelProperty(value = "sumFields", name = "聚合求和字段,格式：field1:value2,field2:value2")
    private String sumFields;
    /**
     * 最大字段
     * 格式：field1:value2,field2:value2
     */
    @ApiModelProperty(value = "maxFields", name = "最大字段,格式：field1:value2,field2:value2")
    private String maxFields;
    /**
     * 最小字段
     * 格式：field1:value2,field2:value2
     */
    @ApiModelProperty(value = "minFields", name = "最小字段,格式：field1:value2,field2:value2")
    private String minFields;
    /**
     * 求均值字段
     * 格式：field1:value2,field2:value2
     */
    @ApiModelProperty(value = "avgFields", name = "求均值字段,格式：field1:value2,field2:value2")
    private String avgFields;
    /**
     * 精确查询
     * 格式：field1:value1,field2:value2
     */
    @ApiModelProperty(value = "matchStr", name = "精确查询字段,格式：field1:value2,field2:value2")
    private String matchStr;
    /**
     * 模糊查询
     */
    @ApiModelProperty(value = "wildcardStr", name = "模糊查询,格式：field1:value2,field2:value2")
    private String wildcardStr;
    /**
     * 查询条件里面某个字段为空并且不为空串筛选
     */
    @ApiModelProperty(value = "nullStr", name = "查询条件里面某个字段为空并且不为空串筛选")
    private String nullStr;
    /**
     * 查询条件里面某个字段不为空
     */
    @ApiModelProperty(value = "notNullStr", name = "查询条件里面某个字段不为空")
    private String notNullStr;
    /**
     * in查询字段
     */
    @ApiModelProperty(value = "inField", name = "in查询字段")
    private Map<String, List<String>> inField;
    /**
     * 分组字段
     * 格式：field1:value1,field2:value2
     */
    @ApiModelProperty(value = "groupFields", name = "分组字段,格式：field1:value1,field2:value2")
    private String groupFields;
    /**
     * 排序
     */
    @ApiModelProperty(value = "sortList", name = "排序字段")
    private List<SortParamsDTO> sortList;
    /**
     * 查询数据大小
     * 查询数据大小
     */
    @ApiModelProperty(value = "size", name = "查询数据大小，查询数据大小")
    private Integer size = 10000;

    /**
     * 开始位置
     */
    @ApiModelProperty(value = "from", name = "分页开始位置")
    private Integer from;
    /**
     * 需要进行正则匹配的字段
     */
    @ApiModelProperty(value = "regexList", name = "需要进行正则匹配的字段")
    private List<RegexParamsDTO> regexList;

    /**
     * 去重字段
     * 格式 ：去重字段1,去重字段2
     */
    @ApiModelProperty(value = "distinctFields", name = "去重字段，格式 ：去重字段1,去重字段2")
    private String distinctFields;

    @ApiModelProperty(value = "location", name = "位置信息，包含经纬度")
    private ESLocationDTO location;
}
