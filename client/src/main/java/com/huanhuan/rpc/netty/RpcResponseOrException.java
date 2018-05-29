package com.huanhuan.rpc.netty;

import com.huanhuan.rpc.model.RpcResponse;

/**
 * Created by junhaozhang on 15-8-31.
 */
public class RpcResponseOrException {
    public final RpcResponse response;
    public final Exception exception;

    public RpcResponseOrException(RpcResponse response) {
        this.response = response;
        this.exception = null;
    }

    public RpcResponseOrException(Exception exception) {
        this.response = null;
        this.exception = exception;
    }
}
