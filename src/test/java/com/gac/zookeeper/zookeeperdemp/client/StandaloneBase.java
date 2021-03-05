package com.gac.zookeeper.zookeeperdemp.client;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.junit.After;
import org.junit.Before;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author gzj
 * @date 2021/3/4 11:31
 */
@Slf4j
public abstract class StandaloneBase {

    private final static String CONNECT_STR = "192.168.60.120:2181";

    private final static Integer SESSION_TIMEOUT = 30 * 1000;

    private static ZooKeeper zooKeeper = null;

    // zk 会开启两个守护线程，countDownLatch 这里控制执行顺序

    private static CountDownLatch countDownLatch = new CountDownLatch(1);

    private Watcher watcher = new Watcher() {

        @SneakyThrows
        @Override
        public void process(WatchedEvent event) {
            if (event.getType() == Event.EventType.None && event.getState() == Event.KeeperState.SyncConnected) {
                log.info("连接成功");
                countDownLatch.countDown();
            }
        }
    };

    @Before
    public void init() {
        try {
            log.info("开始连接zk服务：{}", getConnectStr());
            zooKeeper = new ZooKeeper(getConnectStr(), getSessionTimeout(), watcher);
            log.info("连接中");
            countDownLatch.await();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @After
    public void after() {
        try {
            TimeUnit.SECONDS.sleep(Integer.MAX_VALUE);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    protected String getConnectStr() {
        return CONNECT_STR;
    }

    protected Integer getSessionTimeout() {
        return SESSION_TIMEOUT;
    }

    public static ZooKeeper getZooKeeper() {
        return zooKeeper;
    }
}
