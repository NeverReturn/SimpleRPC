package com.huanhuan.rpc.demo.client;

import com.huanhuan.rpc.demo.server.RPCTest;
import com.huanhuan.rpc.proxy.ClientProxy;

/**
 * Created by huanhuanjin on 2018/5/29.
 */
public class ClientTest {

    public static void main(String []args) throws Throwable {
        ClientProxy clientProxy = new ClientProxy();
        RPCTest rpcTest = clientProxy.proxy(RPCTest.class);
        String str = rpcTest.reverseString("aaa");
        int a = 10;
    }
}
