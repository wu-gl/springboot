package com.example.distlock;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.ReturnType;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.params.SetParams;

import javax.annotation.PostConstruct;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Component
public class ZkLockUtil {

    private static final String WORKSPACE = "/lock-workspace";
    @Autowired
    private CuratorFramework zkClient;

    private static CuratorFramework client;

    @PostConstruct
    public void init() throws Exception {
        client = zkClient;

        //创建WORKSPCACE
        if (client.checkExists().forPath(WORKSPACE) == null) {
            client.create().creatingParentContainersIfNeeded()
                    .withMode(CreateMode.PERSISTENT)
                    .withACL(ZooDefs.Ids.OPEN_ACL_UNSAFE)
                    .forPath(WORKSPACE);
        }
    }

    public static String getLock(String path) {
        while (true) {
            String lockPath = WORKSPACE + path;
            try {
                if (client.checkExists().forPath(lockPath) == null) {
                    client.create().creatingParentContainersIfNeeded()
                            .withMode(CreateMode.EPHEMERAL)
                            .withACL(ZooDefs.Ids.OPEN_ACL_UNSAFE)
                            .forPath(lockPath);
                    return lockPath;
                } else {
                    listenAndWait(path);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void listenAndWait(String path) throws Exception {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        PathChildrenCache childrenCache = new PathChildrenCache(client, WORKSPACE, true);
        childrenCache.start();
        childrenCache.getListenable().addListener(new PathChildrenCacheListener() {
            @Override
            public void childEvent(CuratorFramework curatorFramework, PathChildrenCacheEvent pathChildrenCacheEvent) throws Exception {
                if (pathChildrenCacheEvent.getType().equals(PathChildrenCacheEvent.Type.CHILD_REMOVED) &&
                        pathChildrenCacheEvent.getData().getPath().contains(path)) {
                    countDownLatch.countDown();
                }
            }
        });
        countDownLatch.await();
    }

    public static void releaseLock(String path) {
        try {
            if (client.checkExists().forPath(path) != null) {
                client.delete().forPath(path);
                System.out.println("releaseLock");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
