package com.mmall.controller.portal;

import com.mmall.common.Constant;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/user/")
public class UserController {

    @Autowired
    private IUserService iUserService;

    /**
     * 用户登录
     *
     * @param username
     * @param password
     * @param session
     * @return
     */
    @RequestMapping(value = "login", method = RequestMethod.POST)
    public ServerResponse<User> login(String username, String password, HttpSession session) {
        ServerResponse<User> response = iUserService.login(username, password);
        if (response.isSuccess()) {
            session.setAttribute(Constant.CURRENT_USER, response.getData());
        }
        return response;
    }

    /**
     * 注销
     *
     * @param session
     * @return
     */
    @RequestMapping(value = "logout", method = RequestMethod.POST)
    public ServerResponse<String> logout(HttpSession session) {
        session.removeAttribute(Constant.CURRENT_USER);
        return ServerResponse.createBySuccess();
    }

    /**
     * 用户注册
     *
     * @param user
     * @return
     */
    @RequestMapping(value = "register", method = RequestMethod.POST)
    public ServerResponse<String> register(User user) {
        return iUserService.register(user);
    }

    /**
     * 检查用户名是否有效
     *
     * @param str
     * @param type
     * @return
     */
    @RequestMapping(value = "check_valid", method = RequestMethod.POST)
    public ServerResponse<String> checkValid(String str, String type) {

        return iUserService.checkValid(str, type);
    }

    /**
     * 获取登录用户信息
     *
     * @param session
     * @return
     */
    @RequestMapping(value = "get_user_info", method = RequestMethod.POST)
    public ServerResponse<User> getUserInfo(HttpSession session) {
        User user = (User) session.getAttribute(Constant.CURRENT_USER);
        if (user != null) {
            return ServerResponse.createBySuccess(user);
        }
        return ServerResponse.createByErrorMessage("用户未登录，无法获取当前用户的信息");
    }

    /**
     * 忘记密码问题
     *
     * @param username
     * @return
     */
    @RequestMapping(value = "forget_get_question", method = RequestMethod.POST)
    public ServerResponse<String> forgetGetQuestion(String username) {
        return iUserService.selectQuestion(username);
    }

    /**
     * 提交问题答案
     *
     * @param username
     * @param question
     * @param answer
     * @return
     */
    @RequestMapping(value = "forget_check_answer", method = RequestMethod.POST)
    public ServerResponse<String> forgetCheckAnswer(String username, String question, String answer) {
        return iUserService.checkAnswer(username, question, answer);
    }

    /**
     * 忘记密码的重置密码
     *
     * @param username
     * @param passwordNew
     * @param forgetToken
     * @return
     */
    @RequestMapping(value = "forget_reset_password", method = RequestMethod.POST)
    public ServerResponse<String> forgetResetPassword(String username, String passwordNew, String forgetToken) {
        return iUserService.forgetResetPassword(username, passwordNew, forgetToken);
    }

    /**
     * 登录状态中重置密码
     *
     * @param passwordOld
     * @param passwordNew
     * @return
     */
    @RequestMapping(value = "reset_password", method = RequestMethod.POST)
    public ServerResponse<String> resetPassword(HttpSession session, String passwordOld, String passwordNew) {
        User user = (User) session.getAttribute(Constant.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorMessage("用户未登录");
        }
        return iUserService.resetPassword(passwordOld,passwordNew,user);
    }

    /**
     * 登录状态更新个人信息
     * @param session
     * @param user
     * @return
     */
    @RequestMapping(value = "update_information", method = RequestMethod.POST)
    public ServerResponse<User> updateInformation(HttpSession session,User user){
        User currentUser = (User) session.getAttribute(Constant.CURRENT_USER);
        if (currentUser == null) {
            return ServerResponse.createByErrorMessage("用户未登录");
        }
        user.setId(currentUser.getId());
        user.setUsername(currentUser.getUsername());
        ServerResponse<User> response = iUserService.updateInformation(user);
        if (response.isSuccess()){
            response.getData().setUsername(currentUser.getUsername());
            session.setAttribute(Constant.CURRENT_USER,response.getData());
        }
        return response;
    }

    /**
     * 获取当前登录用户的详细信息，并强制登录
     * @param session
     * @return
     */
    @RequestMapping(value = "get_information", method = RequestMethod.POST)
    public ServerResponse<User> getInformation(HttpSession session){
        User currentUser = (User) session.getAttribute(Constant.CURRENT_USER);
        if (currentUser == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"未登录，需要强制登录");
        }
        return iUserService.getInformation(currentUser.getId());
    }
}
