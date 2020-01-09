package cn.demo.mvc.controller;

import cn.demo.mvc.annotation.MyController;
import cn.demo.mvc.annotation.MyMapping;
import cn.demo.mvc.annotation.MyParam;

@MyController
@MyMapping("/user")
public class UserController {

    @MyMapping("/login")
    public String login(@MyParam("user") String user, @MyParam("password") String password) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("user:");
        stringBuilder.append(user);
        stringBuilder.append("<br>");
        stringBuilder.append("password:");
        stringBuilder.append(password);
        return stringBuilder.toString();
    }

    @MyMapping("/send")
    public void send() {

    }

}
