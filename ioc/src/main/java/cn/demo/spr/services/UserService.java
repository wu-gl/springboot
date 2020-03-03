package cn.demo.spr.services;


import cn.demo.spr.di.annotation.MyComponent;

@MyComponent
public class UserService {

    public String getUserInfo(String userId) {
        return userId + "Hello Word";
    }

}
