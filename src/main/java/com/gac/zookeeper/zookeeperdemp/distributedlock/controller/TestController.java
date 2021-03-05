package com.gac.zookeeper.zookeeperdemp.distributedlock.controller;

import com.gac.zookeeper.zookeeperdemp.distributedlock.service.OrderService;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;


/***
 * @author seek
 */
@RestController
public class TestController {


    @Autowired
    private OrderService orderService;

    @Value("${server.port}")
    private String port;

    @Autowired
    CuratorFramework curatorFramework;

    @PostMapping("/stock/deduct")
    public Object reduceStock(Integer id) throws Exception {

        InterProcessMutex interProcessMutex = new InterProcessMutex(curatorFramework, "/product_" + id);

        try {
            // ...
            interProcessMutex.acquire();
            orderService.reduceStock(id);

        } catch (Exception e) {
            if (e instanceof RuntimeException) {
                throw e;
            }
        } finally {
            interProcessMutex.release();
        }
        return "ok:" + port;
    }
}
