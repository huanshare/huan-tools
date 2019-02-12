package com.huanshare.tools.interceptor.model;

import java.util.Map;

/**
 *  2018/11/16.
 */
public class UserInfoModel {
    private String id;
    private String userAccount;
    private String userName;
    private String userMobilePhone;
    private String remark;
    private Map extraMap;

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(String userAccount) {
        this.userAccount = userAccount;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserMobilePhone() {
        return userMobilePhone;
    }

    public void setUserMobilePhone(String userMobilePhone) {
        this.userMobilePhone = userMobilePhone;
    }

    public Map getExtraMap() {
        return extraMap;
    }

    public void setExtraMap(Map extraMap) {
        this.extraMap = extraMap;
    }
}
