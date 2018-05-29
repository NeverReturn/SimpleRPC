package com.huanhuan.rpc.model;

/**
 * Created by huanhuanjin on 2018/5/25.
 */
public enum SerialTypeEnum {
    INVALID("invalid", -1),
    HESSIAN2("hessian2", 1),
    KYRO("kyro", 1);

    String name;
    int code;

    SerialTypeEnum(String name, int code) {
        this.name = name;
        this.code = code;
    }

    public static SerialTypeEnum codeOf(int code) {
        for (SerialTypeEnum typeEnum : SerialTypeEnum.values()) {
            if (typeEnum.code == code) {
                return typeEnum;
            }
        }
        return INVALID;
    }

    public static SerialTypeEnum nameOf(String name) {
        for (SerialTypeEnum typeEnum : SerialTypeEnum.values()) {
            if (typeEnum.name.equalsIgnoreCase(name)) {
                return typeEnum;
            }
        }
        return INVALID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
