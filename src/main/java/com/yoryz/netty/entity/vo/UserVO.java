package com.yoryz.netty.entity.vo;

/**
 * TODO
 *
 * @author Yory
 * @version 1.0
 * @date 2019/4/23 17:27
 */
public class UserVO {
    private String userId;

    private String username;

    private String portrait;

    private String nickname;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
