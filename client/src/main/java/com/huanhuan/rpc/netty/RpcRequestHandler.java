package com.huanhuan.rpc.netty;

import com.huanhuan.rpc.model.RpcRequest;
import com.huanhuan.rpc.server.ServerFactory;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by junhaozhang on 15-8-7.
 */
public class RpcRequestHandler extends SimpleChannelInboundHandler<RpcRequest> {
    private static final Logger LOGGER = LoggerFactory.getLogger("ARTS");

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequest rpcRequest) throws Exception {
        String className = rpcRequest.getClassName();
        Class clazz = Class.forName(className);
        Object object = ServerFactory.getObject(clazz);
        if (object == null) {
            return ;
        }
        String methodName = rpcRequest.getMethodName();
        Class[] paramTypes = rpcRequest.getParamTypes();
        Object[] args = rpcRequest.getParams();
        Method method = clazz.getMethod(methodName, paramTypes);
        Object res = method.invoke(object, rpcRequest.getParams());
        ctx.writeAndFlush(res);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ConcurrentHashMap<String, Boolean> requests = new ConcurrentHashMap<String, Boolean>();
        super.channelActive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        LOGGER.warn("Netty error: ", cause);
        ctx.close();
    }
}