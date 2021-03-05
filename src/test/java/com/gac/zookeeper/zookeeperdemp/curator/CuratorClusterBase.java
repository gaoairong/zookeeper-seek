package com.gac.zookeeper.zookeeperdemp.curator;


public  class CuratorClusterBase extends CuratorStandaloneBase {

    private final static  String CLUSTER_CONNECT_STR="192.168.60.120:2181,192.168.60.120:2182,192.168.60.120:2183,192.168.60.120:2184";

    public String getConnectStr() {
        return CLUSTER_CONNECT_STR;
    }
}
