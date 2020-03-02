package com.example.transaction.service.orm;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Component;

import java.sql.Connection;

/**
 * <!-- 事务管理器-->
 *     <bean id="transactionManager" class="org.springframework.jdbc.datasource.DataSourceTransactionManager">
 *         <property name="dataSource" ref="dataSource"/>
 *     </bean>
 */
@Component
@Aspect
public class MyDataSourceTransactionManager {
    @Autowired
    private MySqlSessionFactory mySqlSessionFactory;

    @Around("@annotation(MyTransactional)")
    public Object process(ProceedingJoinPoint point) throws Throwable {
        Connection connection = mySqlSessionFactory.getConnection();
        connection.setAutoCommit(false);
        Object d = null;
        try {
            d = point.proceed();
            connection.commit();
        } catch (Exception ex) {
            connection.rollback();
            ex.printStackTrace();
            throw ex;
        }
        return d;
    }
}
