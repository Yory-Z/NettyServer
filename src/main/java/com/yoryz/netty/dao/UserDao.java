package com.yoryz.netty.dao;

import com.yoryz.netty.annotation.Dao;
import com.yoryz.netty.entity.base.User;
import com.yoryz.netty.entity.vo.UserVO;
import com.yoryz.netty.util.MyBatisUtils;
import org.apache.ibatis.session.SqlSession;

/**
* TODO:
*
* @author Yory
* @date 2019/5/16 17:35
* @version 1.0
*/
@Dao
public class UserDao {

/*    private static final UserDao INSTANCE = new UserDao();

    public static UserDao getInstance() {
        return INSTANCE;
    }

    private UserDao(){}*/

    private final String nameSpace = "com.yoryz.netty.dao.UserDao.";

    private final SqlSession sqlSession = MyBatisUtils.getSqlSession();

    public int deleteByPrimaryKey(String userId) {
        return sqlSession.delete(nameSpace + "deleteByPrimaryKey", userId);
    }

    public int insert(User record) {
        return sqlSession.insert(nameSpace + "insert", record);
    }

    public User selectByPrimaryKey(String userId) {
        return sqlSession.selectOne(nameSpace + "selectByPrimaryKey", userId);
    }

    public User checkByUsername(String name) {
        return sqlSession.selectOne(name + "checkByUsername", name);
    }

    public User selectByUsernamePassword(User user) {
        return sqlSession.selectOne(nameSpace + "selectByUsernamePassword", user);
    }

    public UserVO getUserInfo(String name) {
        return sqlSession.selectOne(nameSpace + "getUserInfo", name);
    }
}