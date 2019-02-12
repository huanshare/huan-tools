package com.huanshare.springMvcDemo.dto;

import com.huanshare.tools.mvc.dto.RequestBaseDto;
import com.huanshare.tools.valid.validtor.NotNullValid;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by liuhuan on 2018/7/20 13:44.
 */
@Getter
@Setter
@ApiModel(description = "获取用户信息请求Dto")
public class GetPageRequestDto extends RequestBaseDto {
    @ApiModelProperty(value = "关键字")
    @NotNullValid(message = "关键字不能为空")
    private String userKeyword;

    @NotNullValid(message = "page不能为空")
    private Integer page;

    @NotNullValid(message = "pageSize不能为空")
    private Integer pageSize;

}
