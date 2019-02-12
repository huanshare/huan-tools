package com.huanshare.tools.interceptor.enums;

/**
 * Created by junjun.yang on 2018/11/14.
 */
public enum PermissionTypeEnum {
    MENU(1, "MENU", "菜单"),
    FUNCTION(2, "FUNCTION", "功能");

    private Integer key;
    private String code;
    private String value;

    PermissionTypeEnum(Integer key, String code, String value) {
        this.key = key;
        this.code = code;
        this.value = value;
    }

    public Integer getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public String getCode() {
        return code;
    }
}
