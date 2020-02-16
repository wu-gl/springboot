package com.example.transaction.service.orm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.Statement;

/**
 * 自己定义的ORM工具类
 */
@Component
public class MyJdbcTemplate {

    @Autowired
    private MySqlSessionFactory mySqlSessionFactory;

    public void execute(String sql) throws Exception {
        Connection connection = mySqlSessionFactory.getConnection();
        Statement statement = connection.createStatement();
        statement.execute(sql);
    }
}
