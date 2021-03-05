package com.gac.zookeeper.zookeeperdemp.curator;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.data.Stat;
import org.junit.After;
import org.junit.Before;

import java.util.concurrent.TimeUnit;

/**
 * @author gzj
 * @date 2021/3/4 15:28
 */
@Slf4j
public abstract class CuratorStandaloneBase {

    private final static String CONNECT_STR = "192.168.60.120:2181";

    private final static Integer SESSION_TIMEOUT = 30 * 1000;

    private final static Integer CONNECTION_TIMEOUT = 5000;

    private static CuratorFramework curatorFramework;

    @Before
    public void init() {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(5000, 30);

        curatorFramework = CuratorFrameworkFactory.builder().connectString(getConnectStr())
                .retryPolicy(retryPolicy)
                .sessionTimeoutMs(SESSION_TIMEOUT)
                .connectionTimeoutMs(CONNECTION_TIMEOUT)
                .canBeReadOnly(true)
                .build();

        curatorFramework.getConnectionStateListenable().addListener((client, newState) -> {
            if (newState == ConnectionState.CONNECTED) {
                log.info("连接成功");
            }
        });

        log.info("连接中。。。");
        curatorFramework.start();
    }

    @After
    public void after() {
        try {
            TimeUnit.SECONDS.sleep(Integer.MAX_VALUE);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void createIfNeed(String path) throws Exception {
        Stat stat = curatorFramework.checkExists().forPath(path);
        if (null == stat) {
            String s = curatorFramework.create().forPath(path);
            log.info("path {}, created !", s);
        }
    }

    protected String getConnectStr() {
        return CONNECT_STR;
    }

    public static CuratorFramework getCuratorFramework() {
        return curatorFramework;
    }
}
