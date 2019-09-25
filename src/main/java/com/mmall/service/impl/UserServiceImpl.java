package com.mmall.service.impl;

import com.mmall.commom.Const;
import com.mmall.commom.ServerResponse;
import com.mmall.commom.TokenCache;
import com.mmall.dao.UserMapper;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import com.mmall.util.MD5Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

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
        //注册的时候密码是用MD5加密的,这里也要用MD5
        String MD5Password = MD5Util.MD5EncodeUtf8(password);
        User user=userMapper.selectLogin(userName,MD5Password);
        if(user==null){
            return ServerResponse.createByErrorMessage("密码不正确");
        }
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess("登录成功",user);
    }

    public ServerResponse<String> register(User user){
        ServerResponse validResponse=this.checkValid(user.getUsername(),Const.USERNAME);
        if(!validResponse.isSuccess()){
            return validResponse;
        }
        validResponse=this.checkValid(user.getEmail(),Const.EMAIL);
        if(!validResponse.isSuccess()){
            return validResponse;
        }
        user.setRole(Const.Role.ROLE_CUSTOMER);
        //MD5加密
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));

        int resultCount=userMapper.insert(user);
        //可能出现数据库插入异常
        if(resultCount==0){
            return ServerResponse.createByErrorMessage("注册失败");
        }
        return ServerResponse.createBySuccessMessage("注册成功");
    }

    public ServerResponse<String> checkValid(String str,String type){
        if(StringUtils.isNotBlank(type)){
            //type为非空白字符时才开始校验
            if(Const.USERNAME.equals(type)){
                int resultCount=userMapper.checkUserName(str);
                if(resultCount>0){
                    return ServerResponse.createByErrorMessage("用户已存在");
                }
            }
            if(Const.EMAIL.equals(type)){
                int resultCount=userMapper.checkEmail(str);
                if(resultCount>0){
                    return ServerResponse.createByErrorMessage("邮箱已存在");
                }
            }
        }else{
            return ServerResponse.createByErrorMessage("参数错误");
        }
        return ServerResponse.createBySuccessMessage("校验成功");
    }

    public ServerResponse<String> selectQuestion(String username){
        ServerResponse validResponse=this.checkValid(username,Const.USERNAME);
        if(validResponse.isSuccess()){
            return ServerResponse.createByErrorMessage("用户不存在");
        }
        String question = userMapper.selectQuestionByUsername(username);
        if(StringUtils.isNotBlank(question)){
            return ServerResponse.createBySuccess(question);
        }
        return ServerResponse.createByErrorMessage("找回密码的问题是空的");
    }

    public ServerResponse<String> checkAnswer(String username,String question,String answer){
        int resultCount=userMapper.checkAnswer(username,question,answer);
        if(resultCount>0){
            //说明用户的问题和答案匹配
            String forgetToken = UUID.randomUUID().toString();
            //需要把token放在本地缓存中,并设置有效期
            TokenCache.setKey(TokenCache.TOKEN_PREFIX+username,forgetToken);
            return ServerResponse.createBySuccess(forgetToken);
        }
        return ServerResponse.createByErrorMessage("问题的答案错误");
    }



    public ServerResponse<String> forgetResetPassword(String userName,String newPassword,String forgetoken){
        if(StringUtils.isBlank(forgetoken)){
            return ServerResponse.createByErrorMessage("参数错误,token需要传递");
        }
        //需要校验userName,否则到本地缓存取token的时候会得到不是该用户的token
        ServerResponse validResponse=this.checkValid(userName,Const.USERNAME);
        if(validResponse.isSuccess()){
            //用户不存在
            return ServerResponse.createByErrorMessage("用户不存在");
        }
        String token=TokenCache.getKey(TokenCache.TOKEN_PREFIX+userName);
        if(StringUtils.isBlank(token)){
            return ServerResponse.createByErrorMessage("token无效或过期");
        }
        if (StringUtils.equals(forgetoken,token)){
            String md5Password=MD5Util.MD5EncodeUtf8(newPassword);
            int resultCount=userMapper.updatePasswordByUsername(userName,md5Password);
            if(resultCount>0){
                return ServerResponse.createBySuccessMessage("修改密码成功");
            }else {
                return ServerResponse.createByErrorMessage("修改密码失败(数据库可能出现异常)");
            }
        }else{
            return ServerResponse.createByErrorMessage("token错误,请重新获取重置密码的token");
        }
        //return ServerResponse.createByErrorMessage("修改密码失败");
    }

    public ServerResponse<String> resetPassword(User user,String passwordOld,String passwordNew){
        //防止横向越权,要校验一下这个用户的旧密码,一定要指定这个用户,因为我们会查询一个count(1),如果不指定id,那么结果就是true啦count>1
        int resultCount=userMapper.checkPassword(user.getId(),MD5Util.MD5EncodeUtf8(passwordOld));
        if(resultCount==0){
            return ServerResponse.createByErrorMessage("旧密码错误");
        }
        user.setPassword(MD5Util.MD5EncodeUtf8(passwordNew));
        int updateCount=userMapper.updateByPrimaryKeySelective(user);
        if(updateCount>0){
            return ServerResponse.createBySuccessMessage("更新密码成功");
        }
        return ServerResponse.createByErrorMessage("更新密码失败");
    }

    public ServerResponse<User> updateInformation(User user){
        //username不能被更新
        //email也要进行校验,校验新的email是不是已经存在,并且存在的email如果相同的话,不能是我们当前的这个用户的
        int resultCount=userMapper.checkEmailByUserId(user.getEmail(),user.getId());
        if(resultCount>0){
            ServerResponse.createByErrorMessage("email已存在,请更换email再尝试更新");
        }
        User updateUser=new User();
        updateUser.setId(user.getId());
        updateUser.setEmail(user.getEmail());
        updateUser.setPhone(user.getPhone());
        updateUser.setQuestion(user.getQuestion());
        updateUser.setAnswer(user.getAnswer());
        //也需要把userName设置好,设置为当前用户的userName,因为下面更新个人信息成功后需要把updateUser对象传回给前端,否则前端得到的用户信息没有userName字段
        updateUser.setUsername(user.getUsername());
        //不需考虑update_time字段的更新,因为该字段的更新已经交给mysql的now()内置函数了,代码的业务逻辑不用管

        int updateCount=userMapper.updateByPrimaryKeySelective(updateUser);
        if(updateCount>0){
            return ServerResponse.createBySuccess("更新个人信息成功",updateUser); //这里返回updateUser对象给前端好么
        }
        return ServerResponse.createBySuccessMessage("更新个人信息失败");
    }

    public ServerResponse<User> getInformation(int userId){
        User user=userMapper.selectByPrimaryKey(userId);
        if(user==null){
            return ServerResponse.createByErrorMessage("未找到当前用户");
        }
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess(user);
    }



























}
