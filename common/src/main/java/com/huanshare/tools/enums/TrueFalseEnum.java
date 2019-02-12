package com.huanshare.tools.enums;

public enum TrueFalseEnum {
    TRUE(1, "TRUE"),
    FALSE(0, "FALSE"),;

    private int key;

    private String value;

    TrueFalseEnum(int key, String value) {
        this.key = key;
        this.value = value;
    }

    public int getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}
