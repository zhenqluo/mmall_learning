package com.mmall.service;

import com.mmall.commom.ServerResponse;
import com.mmall.pojo.User;

/**
 * Created by wuyuanyan on 2019/9/24.
 */
public interface IUserService {

    ServerResponse<User> login(String name, String password);

    ServerResponse<String> register(User user);

    ServerResponse<String> checkValid(String str,String type);

    ServerResponse<String> selectQuestion(String username);

    ServerResponse<String> checkAnswer(String username,String question,String answer);

    ServerResponse<String> forgetResetPassword(String userName,String newPassword,String forgetoken);

    ServerResponse<String> resetPassword(User user,String passwordOld,String passwordNew);

    ServerResponse<User> updateInformation(User user);

    ServerResponse<User> getInformation(int userId);

    ServerResponse checkAdminRole(User user);
}
