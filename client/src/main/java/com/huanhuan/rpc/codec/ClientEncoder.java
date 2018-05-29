package com.huanhuan.rpc.codec;

import com.huanhuan.rpc.codec.Hessian.Hessian2Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import com.huanhuan.rpc.model.SerialTypeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

/**
 * Created by xxhua on 15/9/17.
 */
public class ClientEncoder extends MessageToByteEncoder<Serializable> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClientEncoder.class);

    protected SerialTypeEnum serializeType ;
    private static Hessian2Serializer hessian2Serializer = new Hessian2Serializer();

    public ClientEncoder(int serializeType){
        this.serializeType = SerialTypeEnum.codeOf(serializeType);
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Serializable msg, ByteBuf out) throws Exception {
        if (msg == null) {
            return;
        }
        switch (serializeType){
            case HESSIAN2:
                hessian2Serializer.encode(out, msg);
                break;
            default:
        }
    }

}
