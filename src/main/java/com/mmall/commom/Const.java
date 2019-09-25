package com.mmall.commom;

/**
 * Created by wuyuanyan on 2019/9/25.
 */
public class Const {
    public static final String CURRENT_USER="currentUser";
    public static final String EMAIL="email";
    public static final String USERNAME="username";

    //用枚举过于繁重,通过内部的接口来对常量进行分组,而且接口里定义的都是常量
    public interface Role{
        int ROLE_CUSTOMER=0;//普通用户
        int ROLE_ADMIN= 1; //管理员
    }
}
