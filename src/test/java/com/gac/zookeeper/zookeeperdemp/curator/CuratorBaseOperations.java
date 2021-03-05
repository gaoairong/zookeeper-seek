package com.gac.zookeeper.zookeeperdemp.curator;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author gzj
 * @date 2021/3/4 15:55
 */
@Slf4j
public class CuratorBaseOperations extends CuratorStandaloneBase {

    // 递归创建，节点内数据是本机ip ?
    @Test
    public void testCreateWithParent() throws Exception {
        CuratorFramework curatorFramework = getCuratorFramework();
        String pathWithParent = "/node-parent/sub-node-2";
        String path = curatorFramework.create().creatingParentsIfNeeded().forPath(pathWithParent);
        log.info("curator create node :{}  successfully.", path);
    }


    // protection 模式，防止由于异常原因，导致僵尸节点
    @Test
    public void testCreate() throws Exception {
        CuratorFramework curatorFramework = getCuratorFramework();
        String forPath = curatorFramework
                .create()
                .withProtection()
                .withMode(CreateMode.EPHEMERAL_SEQUENTIAL)
                .forPath("/curator-node", "some-data".getBytes());
        log.info("curator create node :{}  successfully.", forPath);
    }

    @Test
    public void testGetData() throws Exception {
        CuratorFramework curatorFramework = getCuratorFramework();
        byte[] bytes = curatorFramework.getData().forPath("/curator-node");
        log.info("get data from node :{}  successfully.", new String(bytes));
    }

    @Test
    public void testSetData() throws Exception {
        CuratorFramework curatorFramework = getCuratorFramework();
        curatorFramework.setData().forPath("/curator-node", "changed!".getBytes());
        byte[] bytes = curatorFramework.getData().forPath("/curator-node");
        log.info("get data from node /curator-node :{}  successfully.", new String(bytes));
    }

    @Test
    public void testDelete() throws Exception {
        CuratorFramework curatorFramework = getCuratorFramework();
        String pathWithParent = "/node-parent";
        curatorFramework.delete().guaranteed().deletingChildrenIfNeeded().forPath(pathWithParent);
    }

    // 注意，返回的是节点名称，非节点内数据
    // 节点 /discovery/example 有4个子节点，/discovery/example/a、/discovery/example/b、/discovery/example/c、/discovery/example/d
    // 打印结果是: a b c d
    @Test
    public void testListChildren() throws Exception {
        CuratorFramework curatorFramework = getCuratorFramework();
        String pathWithParent = "/discovery/example";
        List<String> strings = curatorFramework.getChildren().forPath(pathWithParent);
        strings.forEach(System.out::println);
    }

    @Test
    public void testThreadPool() throws Exception {

        CuratorFramework curatorFramework = getCuratorFramework();
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        String ZK_NODE="/zk-node";
        curatorFramework.getData().inBackground((client, event) -> {
            log.info(" background: {}", event);
        }, executorService).forPath(ZK_NODE);
    }
}
