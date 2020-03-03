package cn.demo.spr.controller;

import cn.demo.spr.di.annotation.MyAutowired;
import cn.demo.spr.mv.MyController;
import cn.demo.spr.mv.MyMapping;
import cn.demo.spr.mv.MyParam;
import cn.demo.spr.services.UserService;

@MyController
@MyMapping("/user")
public class UserController {

    @MyAutowired
    UserService userService;

    @MyMapping("/getUserInfo")
    public String getUserInfo(@MyParam("userId") String userId) {
        return userService.getUserInfo(userId);
    }

}
