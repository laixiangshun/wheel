package com.lxs.bigdata.es.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "RegexParams", description = "正则匹配字段信息")
public class RegexParamsDTO {

    @ApiModelProperty(value = "regexfiled", name = "正则匹配字段")
    String regexfiled;

    @ApiModelProperty(value = "regex", name = "正则匹配表达式")
    String regex;
}
