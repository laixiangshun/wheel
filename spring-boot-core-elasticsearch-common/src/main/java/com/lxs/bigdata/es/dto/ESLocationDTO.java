package com.lxs.bigdata.es.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 根据位置信息（经纬度坐标）搜索
 *
 * @author lxs
 */
@Data
@ApiModel(value = "ESLocationDTO", description = "位置信息")
public class ESLocationDTO {

    /**
     * 纬度
     */
    @ApiModelProperty(value = "lat", name = "纬度")
    @NotNull(message = "纬度不能为空")
    private Double lat;

    /**
     * 经度
     */
    @ApiModelProperty(value = "lon", name = "经度")
    @NotNull(message = "经度不能为空")
    private Double lon;

    /**
     * 距离
     */
    @ApiModelProperty(value = "distance", name = "距离")
    private Double distance;

    /**
     * 距离字段
     */
    @ApiModelProperty(value = "distanceFiled", name = "距离字段")
    @NotBlank(message = "距离字段不能为空")
    private String distanceFiled;

    /**
     * 距离单位
     */
    @ApiModelProperty(value = "unit", name = "距离单位，支持米：m，千米：km,默认为千米")
    private String unit;

    /**
     * 排序方式
     */
    @ApiModelProperty(value = "sortOrder", name = "排序方式，支持：asc升序，desc代表降序,默认为升序")
    private String sortOrder;
}
