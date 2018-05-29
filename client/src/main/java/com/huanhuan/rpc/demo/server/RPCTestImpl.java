package com.huanhuan.rpc.demo.server;

import com.huanhuan.rpc.server.ServerFactory;

import javax.annotation.PostConstruct;

/**
 * Created by huanhuanjin on 2018/5/29.
 */
public class RPCTestImpl implements RPCTest {

    @PostConstruct
    public void init() {
        ServerFactory.register(RPCTest.class, this);
    }

    @Override
    public String reverseString(String str) {
        return new StringBuffer(str).reverse().toString();
    }
}
