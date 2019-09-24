package com.mmall.service.impl;

import com.mmall.commom.ServerResponse;
import com.mmall.dao.UserMapper;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by wuyuanyan on 2019/9/24.
 */
@Service("iUserService")
public class UserServiceImpl implements IUserService {
    @Autowired
    UserMapper userMapper;

    @Override
    public ServerResponse<User> login(String userName, String password) {
        int resultCount=userMapper.checkUserName(userName);
        if(resultCount==0){
            return ServerResponse.createByErrorMessage("用户不存在");
        }
        //todo MD5加密
        User user=userMapper.selectLogin(userName,password);
        if(user==null){
            return ServerResponse.createByErrorMessage("密码不正确");
        }
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess("登录成功",user);
    }

}
