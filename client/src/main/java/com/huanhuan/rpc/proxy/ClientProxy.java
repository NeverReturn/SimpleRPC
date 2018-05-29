package com.huanhuan.rpc.proxy;

import com.huanhuan.rpc.connector.Connector;
import com.huanhuan.rpc.connector.ConnectorFactory;
import com.huanhuan.rpc.model.RpcRequest;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by huanhuanjin on 2018/5/29.
 */
public class ClientProxy implements InvocationHandler {

    private static AtomicInteger atomicInteger = new AtomicInteger();

    private Connector connector = null;

    public <T> T proxy(Class<T> interfaceClass) throws Throwable {
        if (!interfaceClass.isInterface()) {
            throw new IllegalArgumentException(interfaceClass.getName()
                    + " is not an interface");
        }
        connector = ConnectorFactory.getConnector();
        return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(),
                new Class<?>[] { interfaceClass }, this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        RpcRequest rpcRequest = new RpcRequest();
        Class[] clazz = proxy.getClass().getInterfaces();
        rpcRequest.setClassName(clazz[0].getName());
        rpcRequest.setMethodName(method.getName());
        List<Class> typeList = new ArrayList<>();
        for (Object object : args) {
            typeList.add(object.getClass());
        }
        rpcRequest.setParams(args);
        rpcRequest.setParamTypes(typeList.toArray(new Class[0]));
        rpcRequest.setRequestId(String.valueOf(atomicInteger.addAndGet(1)));
        connector.invoke(rpcRequest);
        return "test";
    }
}
