package com.huanhuan.rpc.server;

import com.alibaba.fastjson.JSONObject;
import com.huanhuan.rpc.codec.ClientDecoder;
import com.huanhuan.rpc.codec.ClientEncoder;
import com.huanhuan.rpc.connector.ConnectorFactory;
import com.huanhuan.rpc.model.SerialTypeEnum;
import com.huanhuan.rpc.netty.RpcRequestHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by huanhuanjin on 2018/5/29.
 */
public class ServerFactory {

    private static Map<Class, Object> classObjectMap = new ConcurrentHashMap<>();

    public static void register(Class clazz, Object object) {
        try {
            initChannel(8081);
            classObjectMap.put(clazz, object);
        } catch (Exception e) {

        }
    }

    public static Object getObject(Class clazz) {
        return classObjectMap.get(clazz);
    }

    private static void initChannel(int port) throws Exception {

        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        System.out.println("准备运行端口：" + port);
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            // server端采用简洁的连写方式，client端才用分段普通写法。
            serverBootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch)
                                throws Exception {
                            ch.pipeline().addLast(new ClientEncoder(SerialTypeEnum.HESSIAN2.getCode()));
                            ch.pipeline().addLast(new ClientDecoder());
                            ch.pipeline().addLast(new RpcRequestHandler());
                        }
                    })
                    .option(ChannelOption.SO_KEEPALIVE , true )
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    .option(ChannelOption.SO_SNDBUF, 1024)
                    .option(ChannelOption.SO_RCVBUF, 2048);

            ChannelFuture f = serverBootstrap.bind(port).sync();
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("ip", "127.0.0.1");
        jsonObject.put("port", 8081);
        ConnectorFactory.register(jsonObject.toJSONString());
    }
}
