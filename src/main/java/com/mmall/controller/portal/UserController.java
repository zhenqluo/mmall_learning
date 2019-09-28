package com.mmall.controller.portal;

import com.mmall.commom.Const;
import com.mmall.commom.ResponseCode;
import com.mmall.commom.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

/**
 * Created by wuyuanyan on 2019/9/24.
 */
@RestController
@RequestMapping("/user/")
//Controller接收用户请求,调用service层方法获取响应数据
public class UserController {

    @Autowired
    private IUserService iUserService;

    @RequestMapping(value = "login.do",method = RequestMethod.POST)
    //@ResponseBody
    public ServerResponse<User> login(String userName, String password, HttpSession session){
        //service->mybtis->dao
        ServerResponse<User> response = iUserService.login(userName,password);
        if(response.isSuccess()){
            session.setAttribute(Const.CURRENT_USER,response.getData());
        }
        return response;
    }

    @RequestMapping(value="logout.do",method = RequestMethod.GET)
    //@ResponseBody
    public ServerResponse<String> logout(HttpSession session){
        session.removeAttribute(Const.CURRENT_USER);
        return ServerResponse.createBySuccess();
    }

    @RequestMapping(value="register.do",method = RequestMethod.POST)
    //@ResponseBody
    public ServerResponse<String> register(User user){
        return iUserService.register(user);
    }

    @RequestMapping(value="check_valid.do",method = RequestMethod.POST)
    //@ResponseBody
    public ServerResponse<String> chekValid(String str,String type){
        return iUserService.checkValid(str,type);
    }

    @RequestMapping(value="get_user_info.do",method = RequestMethod.POST)
    //@ResponseBody
    public ServerResponse<User> getUserInfo(HttpSession session){
        User user=(User) session.getAttribute(Const.CURRENT_USER);
        if(user!=null){
            return ServerResponse.createBySuccess(user);
        }
        return ServerResponse.createByErrorMessage("用户未登录,无法获取当前用户的信息");
    }

    @RequestMapping(value="forget_get_question.do",method = RequestMethod.POST)
    //@ResponseBody
    public ServerResponse<String> forgetGetQuestion(String userName){
        return iUserService.selectQuestion(userName);
    }

    @RequestMapping(value="forget_check_answer.do",method = RequestMethod.POST)
    //@ResponseBody
    public  ServerResponse<String> forgetCheckAnswer(String name,String question,String answer){
        return iUserService.checkAnswer(name,question,answer);
    }

    @RequestMapping(value="forget_reset_password.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgetResetPassword(String userName,String newPassword,String forgetToken){
        return iUserService.forgetResetPassword(userName,newPassword,forgetToken);
    }

    @RequestMapping(value="reset_password.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> resetPassword(HttpSession session,String passwordOld,String passwordNew){
        User user=(User) session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServerResponse.createByErrorMessage("用户未登录");
        }
        return iUserService.resetPassword(user,passwordOld,passwordNew);
    }

    @RequestMapping(value="update_information.do",method = RequestMethod.POST)
    //@ResponseBody
    //更新完用户信息后,我们要把新的用户信息放到session,同时返回给前端进行展示,所以泛型使用User
    public ServerResponse<User> update_information(HttpSession session,User user){
        User currentUser=(User) session.getAttribute(Const.CURRENT_USER);
        if(currentUser==null){
            return ServerResponse.createByErrorMessage("用户未登录");
        }
        //用户id和用户名都是从currentUser当前用户获取的,防止用户修改
        user.setId(currentUser.getId());
        user.setUsername(currentUser.getUsername());
        ServerResponse<User> response=iUserService.updateInformation(user);
        if(response.isSuccess()){
            session.setAttribute(Const.CURRENT_USER,response.getData());
        }
        return response;
    }

    @RequestMapping(value="get_information.do",method = RequestMethod.POST)
    //@ResponseBody
    //如果调用这个接口没有登录,要进行强制登录,跟前端约定,code=10的时候,前端就要强制登录
    public ServerResponse<User> get_information(HttpSession session){
        User currentUser=(User) session.getAttribute(Const.CURRENT_USER);
        if(currentUser==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"未登录,需要强制登录code=10");
        }
        return iUserService.getInformation(currentUser.getId());
    }
























}
