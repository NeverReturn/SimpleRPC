package com.huanhuan.rpc.netty;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import com.huanhuan.rpc.model.RpcResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by junhaozhang on 15-8-7.
 */
public class RpcResponseHandler extends SimpleChannelInboundHandler<RpcResponse> {
    private static final Logger LOGGER = LoggerFactory.getLogger("ARTS");
    private final ConcurrentHashMap<String, AtomicReference<RpcResponseOrException>> responseMap;
    private final ConcurrentHashMap<Channel, ConcurrentHashMap<String, Boolean>> channelRequests;

    public RpcResponseHandler(ConcurrentHashMap<String, AtomicReference<RpcResponseOrException>> responseMap,
                              ConcurrentHashMap<Channel, ConcurrentHashMap<String, Boolean>> channelRequests) {
        this.responseMap = responseMap;
        this.channelRequests = channelRequests;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcResponse rpcResponse) throws Exception {
        String requestId = rpcResponse.getRequestId();
        AtomicReference<RpcResponseOrException> ref = responseMap.get(requestId);
        if (ref == null) {
            return;
        }

        synchronized (ref) {
            ref.set(new RpcResponseOrException(rpcResponse));
            ref.notify();
        }

        Channel channel = ctx.channel();
        synchronized(channel) {
            ConcurrentHashMap<String, Boolean> requests = channelRequests.get(channel);
            if (requests != null) {
                requests.remove(requestId);
            }
        }
    }

    private void removeChannelRequests(Channel channel, Exception e) {
        Set<String> requests = null;
        synchronized(channel) {
            ConcurrentHashMap<String, Boolean> entries = channelRequests.remove(channel);
            if (entries == null) {
                return;
            }
            requests = entries.keySet();
        }

        for (String requestId : requests) {
            AtomicReference<RpcResponseOrException> ref = responseMap.get(requestId);
            if (ref == null) {
                continue;
            }

            synchronized (ref) {
                ref.set(new RpcResponseOrException(e));
                ref.notify();
            }
         }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        removeChannelRequests(ctx.channel(), new Exception("Channel already closed!"));
        super.channelInactive(ctx);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ConcurrentHashMap<String, Boolean> requests = new ConcurrentHashMap<String, Boolean>();
        channelRequests.put(ctx.channel(), requests);
        super.channelActive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        LOGGER.warn("Netty error: ", cause);
        removeChannelRequests(ctx.channel(), new Exception(cause));
        ctx.close();
    }
}