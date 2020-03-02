package com.example.transaction.service.orm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;


/**
 * 将创建数据库的连接同一出来，整合spring的时候，需要交给spring
 * <p>
 * <!-- SqlSessionFactory配置 -->
 * <bean id="sqlSessionFactory" class="org.mybatis.spring.SqlSessionFactoryBean">
 * <property name="dataSource" ref="dataSource" />
 * <!-- 加载mybatis的全局配置文件 -->
 * <property name="mapperLocations" value="classpath:resource/userMapper.xml" />
 * </bean>
 */
@Component
public class MySqlSessionFactory {
    @Autowired
    private DataSource dataSource;


    ThreadLocal<Connection> connectionThreadLocal = new ThreadLocal<Connection>();
    private Object connectionLock = new Object();

    public Connection getConnection() throws Exception {
        if (connectionThreadLocal.get() == null) {
            synchronized (connectionLock) {
                if (connectionThreadLocal.get() == null) {
                    Connection connection = dataSource.getConnection();
                    connectionThreadLocal.set(connection);
                }
            }
        }
        return connectionThreadLocal.get();
    }
}
