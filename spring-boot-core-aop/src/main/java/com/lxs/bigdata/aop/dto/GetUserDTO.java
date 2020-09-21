package com.lxs.bigdata.aop.dto;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "GetUserDTO", description = "获取用户DTO")
public class GetUserDTO {

    @ApiModelProperty(value = "userId", required = true)
    @NotBlank(message = "用户Id不能为空")
    private String userId;
}
