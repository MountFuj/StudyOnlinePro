package com.zy.base.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageParams {

    @ApiModelProperty("当前页码默认值")
    private long pageNo = 1L;

    @ApiModelProperty("每页记录数默认值")
    private long pageSize = 10L;
}
