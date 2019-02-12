package com.huanshare.springMvcDemo.controller;

import com.huanshare.springMvcDemo.biz.UserBiz;
import com.huanshare.springMvcDemo.dto.*;
import com.huanshare.springMvcDemo.entity.CrmUser;
import com.huanshare.tools.mapper.utils.MapperUtils;
import com.huanshare.tools.mvc.annotation.limited.LimitedIdempotent;
import com.huanshare.tools.mvc.annotation.rest.RestLevel;
import com.huanshare.tools.mvc.dto.ResponseBaseDto;
import com.huanshare.tools.mybatis.page.Page;
import com.huanshare.tools.valid.validtor.ParamsValid;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/test")
@Api(tags = "查询个人信息", description = "查询个人汇总")
public class UserController {

    @Autowired
    private UserBiz userBiz;

    @ApiOperation(notes = "根据姓名模糊匹配用户", value = "根据姓名模糊匹配用户")
    @RequestMapping(value = "/getUserInfo", method = RequestMethod.POST)
    public ResponseEntity<GetUserInfoResponseDto> openAccount(@RequestBody GetUserInfoRequestDto requestDto) {
        GetUserInfoResponseDto responseDto=new GetUserInfoResponseDto();
        List<UserInfoDto> userInfoList=new ArrayList<>();
        UserInfoDto userInfoDto=null;
        for (int i=0;i<102;i++){
            userInfoDto=new UserInfoDto();
            userInfoDto.setId(String.valueOf(i));
            userInfoDto.setUserName("huan"+i);
            userInfoDto.setUserAge(String.valueOf(10+i+1));
            userInfoDto.setUserHeight(String.valueOf(160+i));
            userInfoDto.setUserWeight(String.valueOf(60+i));
            userInfoDto.setCreateTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            userInfoList.add(userInfoDto);
        }
        responseDto.setUserInfoList(userInfoList);
        return ResponseEntity.ok(responseDto);
    }

    @ApiOperation(notes = "获取分页用户列表", value = "获取分页用户列表")
    @RequestMapping(value = "/getUserPageList", method = RequestMethod.POST)
    @LimitedIdempotent(message = "访问太频繁",timeout=3)
    @RestLevel(level = RestLevel.LEVEL_ONE)
    @ResponseBody
    public ResponseBaseDto getUserPageList(@RequestBody @ParamsValid GetPageRequestDto requestDto) {
        Page<CrmUser> pageList= userBiz.getUserPageList(requestDto);
        if(null!=pageList&&pageList.getTotal()>0){
            GetPageResponseDto responseDto=new GetPageResponseDto();
            responseDto.setTotal(pageList.getTotal());
            responseDto.setTotalPage(pageList.getTotalPages());
            responseDto.setUserInfoList(MapperUtils.mapper(pageList.getContent(),CrmUserDto.class));
            return responseDto;
        }
        return new GetPageResponseDto();
    }
}