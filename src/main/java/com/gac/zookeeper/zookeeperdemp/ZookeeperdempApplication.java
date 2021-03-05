package com.gac.zookeeper.zookeeperdemp;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.gac.zookeeper.zookeeperdemp.distributedlock.mapper")
@SpringBootApplication
public class ZookeeperdempApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZookeeperdempApplication.class, args);
    }

}
