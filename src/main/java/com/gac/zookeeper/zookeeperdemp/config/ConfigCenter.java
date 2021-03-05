package com.gac.zookeeper.zookeeperdemp.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author gzj
 * @date 2021/3/4 10:22
 */
@Slf4j
public class ConfigCenter {

    private final static String CONNECT_STR = "192.168.60.120:2181";

    private final static Integer SESSION_TIMEOUT = 30 * 1000;

    private static ZooKeeper zooKeeper = null;

    // zk 会开启两个守护线程，countDownLatch 这里控制执行顺序

    private static CountDownLatch countDownLatch = new CountDownLatch(1);

    public static void main(String[] args) throws IOException, InterruptedException, KeeperException {

        zooKeeper = new ZooKeeper(CONNECT_STR, SESSION_TIMEOUT, new Watcher() {
            @Override
            public void process(WatchedEvent event) {

                if (event.getType() == Event.EventType.None && event.getState() == Event.KeeperState.SyncConnected) {
                    log.info("连接成功");
                    countDownLatch.countDown();
                }
            }
        });

        countDownLatch.await();

        MyConfig myConfig = new MyConfig("anyKey", "anyName");

        ObjectMapper mapper = new ObjectMapper();
        byte[] bytes = mapper.writeValueAsBytes(myConfig);

        String str = zooKeeper.create("/myConfig", bytes, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        // 输出：create str is /myConfig
        log.info("create str is {}", str);

        Watcher watcher = new Watcher() {

            @SneakyThrows
            @Override
            public void process(WatchedEvent event) {
                if (event.getType() == Event.EventType.NodeDataChanged && event.getPath() != null
                && "/myConfig".equals(event.getPath())) {
                    log.info("PATH {} , 发生数据变化", event.getPath());

                    // 注意下面的参数，this 实现循环监听，很关键

                    byte[] data = zooKeeper.getData("/myConfig", this, null);
                    MyConfig newConfig = mapper.readValue(new String(data), MyConfig.class);

                    log.info("数据发生变化：{}", newConfig);
                }
            }
        };

        // 这里获取数据并添加对 watcher 监听

        byte[] data = zooKeeper.getData("/myConfig", watcher, null);
        MyConfig originalConfig = mapper.readValue(new String(data), MyConfig.class);
        log.info("原始数据：{}", originalConfig);

        TimeUnit.SECONDS.sleep(Integer.MAX_VALUE);
    }

}
