package com.yoryz.netty.service.impl;

import com.yoryz.netty.annotation.Inject;
import com.yoryz.netty.annotation.Service;
import com.yoryz.netty.dao.UserDao;
import com.yoryz.netty.entity.base.User;
import com.yoryz.netty.entity.vo.UserVO;
import com.yoryz.netty.service.UserService;
import com.yoryz.netty.util.EncoderUtils;

import java.security.NoSuchAlgorithmException;

/**
 * TODO
 *
 * @author Yory
 * @version 1.0
 * @date 2019/5/16 16:36
 */
@Service
public class UserServiceImpl implements UserService {

    @Inject
    private UserDao userDao;

    @Override
    public UserVO getUserInfo(String name) {
        return userDao.getUserInfo(name);
    }

    @Override
    public boolean queryUsernameIsExist(String username) {
        return userDao.checkByUsername(username) != null;
    }

    @Override
    public User userLogin(String username, String pwd) {
        try {
            return userDao.selectByUsernamePassword(
                    new User(username, EncoderUtils.getMD5Str(pwd)));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }
}
