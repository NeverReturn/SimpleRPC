package com.huanhuan.rpc.codec;

import io.netty.buffer.ByteBuf;

import java.io.IOException;

/**
 * Created by huanhuanjin on 2018/5/25.
 */
public interface Serializer {

    public Object decode(ByteBuf byteBuf) throws Exception;
    public void encode(ByteBuf byteBuf, Object msg) throws IOException;

}
