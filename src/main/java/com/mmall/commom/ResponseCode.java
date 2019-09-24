package com.mmall.commom;

/**
 * Created by wuyuanyan on 2019/9/24.
 */
public enum ResponseCode {
    SUCCESS(0,"SUCCESS"),
    ERROR(1,"ERROR"),
    NEED_LOGIN(10,"NEED_LOGIN"),
    ILLEGAL_ARGUMENT(2,"ILLEGAL_ARGUMENT");

    private final int code;
    private final String desc;

    public int getCode(){
        return this.code;
    }
    public String getDesc(){
        return this.desc;
    }
    private ResponseCode(int code,String desc){
        this.code=code;
        this.desc=desc;
    }
}
