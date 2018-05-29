package com.huanhuan.rpc.server;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by huanhuanjin on 2018/5/29.
 */
public class ServerFactory {

    private static Map<Class, Object> classObjectMap = new ConcurrentHashMap<>();

    public static void register(Class clazz, Object object) {
        classObjectMap.put(clazz, object);
    }

    public static Object getObject(Class clazz) {
        return classObjectMap.get(clazz);
    }
}
