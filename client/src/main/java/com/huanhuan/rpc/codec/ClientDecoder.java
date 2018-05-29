package com.huanhuan.rpc.codec;

import com.huanhuan.rpc.codec.Hessian.Hessian2Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.TooLongFrameException;
import com.huanhuan.rpc.model.SerialTypeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by xxhua on 15/9/17.
 */
public class ClientDecoder extends LengthFieldBasedFrameDecoder {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClientDecoder.class);

    private static final int MaxFrameLength = 1024 * 1024;
    protected static final int LengthFieldOffset = 1;
    protected static final int LengthFieldLength = 4;
    protected static final int LengthAdjustment = 0;
    protected static final int InitialBytesToStrip = 0;
    private static Hessian2Serializer hessian2Serializer = new Hessian2Serializer();

    public ClientDecoder() {
        this(MaxFrameLength);
    }
    public ClientDecoder(int maxFrameLength){
        super(maxFrameLength, LengthFieldOffset, LengthFieldLength, LengthAdjustment, InitialBytesToStrip);
    }
    @Override
    public Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        ByteBuf frame = null;
        Object object = null;
        try {
            frame = (ByteBuf) super.decode(ctx, in);
            if (frame == null) {
                return null;
            }
            SerialTypeEnum serialType = SerialTypeEnum.codeOf( frame.readByte());
            if(serialType == null){
                return null;
            }
            frame.readInt();

            switch (serialType) {
                case HESSIAN2:
                    object = hessian2Serializer.decode(frame);
                    break;
                default:
            }
        } catch (TooLongFrameException e){
            LOGGER.error("Error ocurred during decode frame:", e);
        }finally {
            if(frame!=null){
                frame.release();
            }
        }
        return object;
    }
}
