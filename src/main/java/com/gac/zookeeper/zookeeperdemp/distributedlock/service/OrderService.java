package com.gac.zookeeper.zookeeperdemp.distributedlock.service;

import com.gac.zookeeper.zookeeperdemp.distributedlock.entity.Order;
import com.gac.zookeeper.zookeeperdemp.distributedlock.entity.Product;
import com.gac.zookeeper.zookeeperdemp.distributedlock.mapper.OrderMapper;
import com.gac.zookeeper.zookeeperdemp.distributedlock.mapper.ProductMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author seek
 */
@Service
public class OrderService {

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private OrderMapper orderMapper;



     public void reduceStock(Integer id){
        // 1.    获取库存
        Product product = productMapper.getProduct(id);
        // 模拟耗时业务处理, 其他业务处理
        sleep( 500);

        if (product.getStock() <=0 ) {
            throw new RuntimeException("out of stock");
        }
        // 2.    减库存
        int i = productMapper.deductStock(id);
        if (i==1){
            Order order = new Order();
            order.setUserId(UUID.randomUUID().toString());
            order.setPid(id);
            orderMapper.insert(order);
        }else{
            throw new RuntimeException("deduct stock fail, retry.");
        }
    }

    /**
     * 模拟耗时业务处理
     * @param wait
     */
   public void sleep(long  wait){
       try {
           TimeUnit.MILLISECONDS.sleep( wait );
       } catch (InterruptedException e) {
           e.printStackTrace();
       }
   }

}
