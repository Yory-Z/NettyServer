package com.yoryz.netty.controller;

import com.yoryz.netty.annotation.Get;
import com.yoryz.netty.util.MyResponse;

/**
 * TODO
 *
 * @author Yory
 * @version 1.0
 * @date 2019/5/17 15:31
 */
public class AdminController {

    private String myName;

    @Get("xixi")
    public MyResponse xixi(Integer wawa, String name) {
        return MyResponse.okNoData("Admin controller. " + wawa + ", " + name);
    }

    public void print() {
        System.out.println(myName);
    }
}
