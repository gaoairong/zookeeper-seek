package com.gac.zookeeper.zookeeperdemp.client;

import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.junit.Test;

/**
 * @author gzj
 * @date 2021/3/4 13:40
 */
@Slf4j
public class BaseOperations extends StandaloneBase {

    private String first_node = "/firstNode";


    // 注意，是 org.junit.Test 包下的 @Test 注解

    @Test
    public void testCreate() throws KeeperException, InterruptedException {

        ZooKeeper zooKeeper = getZooKeeper();

        String str = zooKeeper.create(first_node, "first".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE,
                CreateMode.EPHEMERAL);

        log.info("create : {}", str);

    }

    @Test
    public void testSetData() throws KeeperException, InterruptedException {

        ZooKeeper zooKeeper = getZooKeeper();
//        byte[] data = zooKeeper.getData(first_node, false, new Stat());
//        int version = new Stat().getVersion();
        zooKeeper.setData(first_node, "sec".getBytes(), 0);
    }

    @Test
    public void testGetData() {

        Watcher watcher = new Watcher(){
            @Override
            public void process(WatchedEvent event) {
                if (event.getPath() != null && event.getPath().equals(first_node)
                && event.getType() != null && event.getType().equals(Event.EventType.NodeDataChanged)) {

                    try {
                        log.info("节点 [{}]，数据发生变化", event.getPath());
                        byte[] data = getZooKeeper().getData(first_node, this, null);
                        log.info("获取数据：{}", new String(data));
                    } catch (KeeperException | InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        try {
            byte[] data = getZooKeeper().getData(first_node, watcher, null);
            log.info("原始数据：[{}]", new String(data));
        } catch (KeeperException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testDelete() throws KeeperException, InterruptedException {

        // -1，匹配所有版本并删除
        // 大于-1，指定版本删除
        getZooKeeper().delete(first_node, -1);
    }


    /**
     * 日志打印结果：
     * Thread Name : main-EventThread，
     * rc:0, path:/test, ctx:gcc, data:[97, 97, 97],
     * stat:61,61,1614840183414,1614840183414,0,0,0,72057750097756162,3,0,61
     */
    @Test
    public void asyncTest() {
        // 参数：路径、监听、回调函数、上下文
        getZooKeeper().getData("/test", false, (rc, path, ctx, data, stat) -> {
            Thread thread = Thread.currentThread();
            log.info("Thread Name : {}，rc:{}, path:{}, ctx:{}, data:{}, stat:{}", thread.getName(), rc, path, ctx, data, stat);
        }, "gcc");
        log.info("over this.");
    }
}