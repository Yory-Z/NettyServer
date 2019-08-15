package com.yoryz.netty.controller;

import com.yoryz.netty.annotation.Controller;
import com.yoryz.netty.annotation.Get;
import com.yoryz.netty.annotation.Inject;
import com.yoryz.netty.annotation.Post;
import com.yoryz.netty.entity.base.User;
import com.yoryz.netty.entity.vo.UserVO;
import com.yoryz.netty.service.UserService;
import com.yoryz.netty.util.MyResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * TODO
 *
 * @author Yory
 * @version 1.0
 * @date 2019/5/16 16:11
 */
@Controller("/user")
public class UserController {

    private final Logger logger = LogManager.getLogger(UserController.class);

    @Inject
    private UserService userService;

    @Post("/userLogin")
    public MyResponse userLogin(String userName, String password) {
        User user = userService.userLogin(userName, password);
        if (user != null) {
            return MyResponse.ok(user, "User login successfully.");
        }
        return MyResponse.okNoData("UserName or password wrong.");
    }

    @Get("/getUserInfo")
    public MyResponse getUserInfo(String userName) {
        UserVO userVO = userService.getUserInfo(userName);
        if (userVO != null) {
            return MyResponse.ok(userVO, "Get user info successfully.");
        }
        return MyResponse.okNoData("UserName doesn't exist.");
    }

}
