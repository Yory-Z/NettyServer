package com.yoryz.netty.service;

import com.yoryz.netty.entity.base.User;
import com.yoryz.netty.entity.vo.UserVO;

/**
 * TODO
 *
 * @author Yory
 * @version 1.0
 * @date 2019/5/16 16:20
 */
public interface UserService {

    /**
     * get user information by username
     *
     * @param name the user's name
     * @return
     */
    UserVO getUserInfo(String name);

    /**
     * check whether this username exist or not
     *
     * @param username the username need to check
     * @return true if exist
     */
    boolean queryUsernameIsExist(String username);

    /**
     * check whether this user exist or not
     *
     * @param username the username need to check
     * @param pwd the user's password
     * @return User if exist
     */
    User userLogin(String username, String pwd);
}
