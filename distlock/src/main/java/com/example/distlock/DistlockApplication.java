package com.example.distlock;

import com.example.distlock.zk.ZooKeeperLockUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@SpringBootApplication
@RestController("/")
public class DistlockApplication {


    @RequestMapping(value = "/lockByRedis", method = RequestMethod.GET)
    public boolean lockByRedis() throws Exception {
        boolean r = RedisLockUtil.lock("user:1", 1, TimeUnit.MINUTES);
        return r;
    }

    @RequestMapping(value = "/lockByZk", method = RequestMethod.GET)
    public String lockByZk() throws Exception {
        ZooKeeperLockUtil.mutex("/user/1", () -> {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("hello word");
        }, 5, TimeUnit.MINUTES);
        return "ok";
    }

    @RequestMapping(value = "/lockByZk1", method = RequestMethod.GET)
    public String lockByZk1() throws Exception {
        String keyPath = ZkLockUtil.getLock("/user:2");
        Thread.sleep(10000);
        ZkLockUtil.releaseLock(keyPath);
        return "ok";
    }

    public static void main(String[] args) {
        SpringApplication.run(DistlockApplication.class, args);
    }

}
