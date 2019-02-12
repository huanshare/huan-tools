package com.huanshare.springMvcDemo.dto;

import com.huanshare.tools.mvc.dto.ResponseBaseDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuhuan on 2018/7/20 13:44.
 */
@Getter
@Setter
@ApiModel(description = "获取用户信息返回Dto")
public class GetUserInfoResponseDto extends ResponseBaseDto {
    @ApiModelProperty(value = "用户信息列表")
    private List<UserInfoDto> userInfoList=new ArrayList<>();
}
