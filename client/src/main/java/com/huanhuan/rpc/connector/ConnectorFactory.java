package com.huanhuan.rpc.connector;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryNTimes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by huanhuanjin on 2018/5/29.
 */
public class ConnectorFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConnectorFactory.class);

    private static CuratorFramework client;

    private static final String connectString = "172.18.153.248:2181";
    private static AtomicInteger atomicInteger = new AtomicInteger();

    static {
        try {
            CuratorFrameworkFactory.Builder builder = CuratorFrameworkFactory.builder()
                    .connectString(connectString)
                    .retryPolicy(new RetryNTimes(Integer.MAX_VALUE, 1000)).connectionTimeoutMs(2000)
                    .namespace("/rpc");
            client = builder.build();
        } catch (Exception e) {
            LOGGER.error("create zk client failed!");
        }
    }

    public static Connector getConnector() throws Exception {
        List<String> childList = client.getChildren().forPath("/provider");
        int index = atomicInteger.getAndAdd(1) % childList.size();
        String provider = childList.get(index);
        String data = String.valueOf(client.getData().forPath(provider));
        JSONObject jsonObject = JSON.parseObject(data);
        String ip = jsonObject.getString("ip");
        int port = jsonObject.getInteger("port");
        Connector connector = new Connector(ip, port);
        return connector;
    }
}
