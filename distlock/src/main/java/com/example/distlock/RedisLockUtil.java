package com.example.distlock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.ReturnType;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.params.SetParams;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

@Component
public class RedisLockUtil {

    @Autowired
    private RedisTemplate redisTemplate;

    private static RedisTemplate redisTemplateStatic;

    @PostConstruct
    public void init() {
        redisTemplateStatic = redisTemplate;
    }

    private static final String LOCK_SUCCESS = "OK";
    private static final Long RELEASE_LOCK_SUCCESS_RESULT = 1L;

    private static final String requestId = "1";

    public static boolean lock(String key, long expTime, TimeUnit timeUnit) {
        return lock(key, requestId, expTime, timeUnit);
    }

    public static boolean lock(String key, String requestId, long expTime, TimeUnit timeUnit) {
        return (boolean) redisTemplateStatic.execute((RedisCallback<Object>) connection -> {
            Jedis jedis = (Jedis) connection.getNativeConnection();
            return LOCK_SUCCESS.equals(jedis.set(key, requestId, SetParams.setParams().nx().px(timeUnit.toMillis(expTime))));
        });
    }

    /**
     * 解锁
     *
     * @param key
     */
    public static boolean unlock(String key) {
        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end ";
        return redisTemplateStatic.execute((RedisConnection connection) -> connection.eval(script.getBytes(), ReturnType.INTEGER, 1, key.getBytes(), requestId.getBytes())).equals(RELEASE_LOCK_SUCCESS_RESULT);
    }

    /**
     * 解锁
     *
     * @param key
     */
    public static boolean unlock(String key, String requestId) {
        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end ";
        return redisTemplateStatic.execute((RedisConnection connection) -> connection.eval(script.getBytes(), ReturnType.INTEGER, 1, key.getBytes(), requestId.getBytes())).equals(RELEASE_LOCK_SUCCESS_RESULT);
    }

}
