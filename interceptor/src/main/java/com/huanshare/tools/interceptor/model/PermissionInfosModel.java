package com.huanshare.tools.interceptor.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by te.huang on 2018/11/14.
 */
public class PermissionInfosModel {
    private String applicationName = "";
    private String applicationCode = "";
    private List<PermissionModel> permissionList = new ArrayList<>();

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public String getApplicationCode() {
        return applicationCode;
    }

    public void setApplicationCode(String applicationCode) {
        this.applicationCode = applicationCode;
    }

    public List<PermissionModel> getPermissionList() {
        return permissionList;
    }

    public void setPermissionList(List<PermissionModel> permissionList) {
        this.permissionList = permissionList;
    }
}
