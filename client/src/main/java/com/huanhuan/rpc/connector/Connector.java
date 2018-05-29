package com.huanhuan.rpc.connector;

import com.huanhuan.rpc.codec.ClientDecoder;
import com.huanhuan.rpc.codec.ClientEncoder;
import com.huanhuan.rpc.model.RpcRequest;
import com.huanhuan.rpc.model.SerialTypeEnum;
import com.huanhuan.rpc.netty.RpcResponseHandler;
import com.huanhuan.rpc.netty.RpcResponseOrException;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by huanhuanjin on 2018/5/29.
 */
public class Connector {
    private static final EventLoopGroup workerGroup;
    private static final ConcurrentHashMap<String, AtomicReference<RpcResponseOrException>> responseMap;
    private static final ConcurrentHashMap<Channel, ConcurrentHashMap<String, Boolean>> channelRequests;
    protected static final int DEFAULT_CONNECTION_TIMEOUT = 100;

    private String ip;
    private int port;
    private Channel channel;

    static {
        workerGroup = new NioEventLoopGroup(10);
        responseMap = new ConcurrentHashMap<String, AtomicReference<RpcResponseOrException>>();
        channelRequests = new ConcurrentHashMap<Channel, ConcurrentHashMap<String, Boolean>>();
    }

    public Connector(String ip, int port) {
        this.ip = ip;
        this.port = port;
        initChannel();
    }

    private void initChannel() {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(workerGroup).channel(NioSocketChannel.class).option(ChannelOption.CONNECT_TIMEOUT_MILLIS, DEFAULT_CONNECTION_TIMEOUT)
                .handler(new ChannelInitializer<SocketChannel>() {

                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new ClientEncoder(SerialTypeEnum.HESSIAN2.getCode()));
                        ch.pipeline().addLast(new ClientDecoder(1024*1024));
                        ch.pipeline().addLast(new RpcResponseHandler(responseMap, channelRequests));
                    }
                });

        ChannelFuture future = bootstrap.connect(new InetSocketAddress(ip, port));
        future.addListener(new ChannelFutureListener(){
            @Override
            public void operationComplete(ChannelFuture cfuture) throws Exception {
                channel = cfuture.channel();
            }
        });
    }

    public RpcResponseOrException invoke(RpcRequest rpcRequest) throws Exception {
        AtomicReference<RpcResponseOrException> ref = new AtomicReference();
        responseMap.put(rpcRequest.getRequestId(), ref);
        channel.writeAndFlush(rpcRequest);
        ref.wait();
        return ref.get();
    }
}
