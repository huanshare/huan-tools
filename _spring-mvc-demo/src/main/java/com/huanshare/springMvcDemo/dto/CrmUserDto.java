package com.huanshare.springMvcDemo.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class CrmUserDto {
    private Long id;

    private Long userId;

    private String userName;

    private String jobNum;

    private String roleCode;

    private String email;

    private String gender;

    private String mobile;

    private Long leadUserId;

    private String leadJobCode;

    private String level;

    private Long createUserId;

    private Date createTime;

    private Date updateTime;

    private String status;

    private Long updateUserId;
}