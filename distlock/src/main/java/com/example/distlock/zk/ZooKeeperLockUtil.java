package com.example.distlock.zk;

import com.example.distlock.zk.finterface.ZkLockCallback;
import com.example.distlock.zk.finterface.ZkLockException;
import com.example.distlock.zk.finterface.ZkVoidCallBack;
import com.sun.corba.se.impl.orbutil.closure.Constant;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.ErrorListenerPathable;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

@Component
public class ZooKeeperLockUtil {

    @Autowired
    private CuratorFramework zkClient;

    private static CuratorFramework client;

    @PostConstruct
    public void init() {
        client = zkClient;
    }

    public static  <T> T mutex(String path, ZkLockCallback<T> zkLockCallback) throws ZkLockException {
        return mutex(path, zkLockCallback, 100L, TimeUnit.MILLISECONDS);
    }

    public static <T> T mutex(String path, ZkLockCallback<T> zkLockCallback, long time, TimeUnit timeUnit) throws ZkLockException {
        String finalPath = getLockPath(path);
        InterProcessMutex mutex = new InterProcessMutex(client, finalPath);

        try {
            if (!mutex.acquire(time, timeUnit)) {
                throw new ZkLockException("acquire zk lock return false");
            }
        } catch (Exception var13) {
            throw new ZkLockException("acquire zk lock failed.", var13);
        }

        T var8;
        try {
            var8 = zkLockCallback.doInLock();
        } finally {
            releaseLock(finalPath, mutex);
        }

        return var8;
    }

    private static void releaseLock(String finalPath, InterProcessMutex mutex) {
        try {
            mutex.release();
            deleteInternal(finalPath);
        } catch (Exception var4) {
            var4.printStackTrace();
//            LogUtil.error(this.logger, "dlock", "release lock failed, path:{}", new Object[]{finalPath, var4});
        }

    }

    public static void mutex(String path, ZkVoidCallBack zkLockCallback, long time, TimeUnit timeUnit) throws ZkLockException {
        String finalPath = getLockPath(path);
        InterProcessMutex mutex = new InterProcessMutex(client, finalPath);

        try {
            if (!mutex.acquire(time, timeUnit)) {
                throw new ZkLockException("acquire zk lock return false");
            }
        } catch (Exception var13) {
            throw new ZkLockException("acquire zk lock failed.", var13);
        }

        try {
            zkLockCallback.response();
        } finally {
            releaseLock(finalPath, mutex);
        }

    }

    public static String getLockPath(String customPath) {
        if (!StringUtils.startsWithIgnoreCase(customPath, "/")) {
            return "/" + customPath;
        }
        return customPath;
    }

    private static void deleteInternal(String finalPath) {
        try {
            ((ErrorListenerPathable) client.delete().inBackground()).forPath(finalPath);
        } catch (Exception var3) {
            var3.printStackTrace();
        }

    }

    public static void del(String customPath) {
        String lockPath = "";

        try {
            lockPath = getLockPath(customPath);
            ((ErrorListenerPathable) client.delete().inBackground()).forPath(lockPath);
        } catch (Exception var4) {
            var4.printStackTrace();
        }

    }
}