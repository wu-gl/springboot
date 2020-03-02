package com.example.transaction.service.impl;

import com.example.transaction.service.UserService;
import com.example.transaction.service.orm.MyJdbcTemplate;
import com.example.transaction.service.orm.MyTransactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Transactional(rollbackFor = Exception.class)
    public void addUser(String name, String email, String pwd) {
        jdbcTemplate.execute(String.format("insert into user(name,email,password) values(%s,%s,%s)", name, email, pwd));
        //int a = 1 / 0;
    }

    @Autowired
    private MyJdbcTemplate myJdbcTemplate;

    @Override
    @MyTransactional
    public void deleteUser(String id) throws Exception {
        myJdbcTemplate.execute("delete from user where id =" + id);
        int a = 3 / 0;
    }
}
