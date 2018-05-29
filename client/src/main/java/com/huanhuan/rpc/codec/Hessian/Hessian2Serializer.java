package com.huanhuan.rpc.codec.Hessian;

import com.huanhuan.rpc.codec.Serializer;
import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;

import java.io.*;

/**
 * Created by huanhuanjin on 2018/5/25.
 */
public class Hessian2Serializer implements Serializer {
    @Override
    public Object decode(ByteBuf byteBuf) throws Exception {
        if (byteBuf == null) {
            throw new NullPointerException();
        }
        ByteBufInputStream is = new ByteBufInputStream(byteBuf);
        HessianInput in = new HessianInput(is);
        in.close();
        is.close();
        return in.readObject();
    }

    @Override
    public void encode(ByteBuf byteBuf, Object obj) throws IOException {
        if(obj == null) {
            throw new NullPointerException();
        }
        ByteBufOutputStream os = new ByteBufOutputStream(byteBuf);
        HessianOutput out = new HessianOutput(os);
        out.writeObject(obj);
        out.close();
        os.close();
        return ;
    }
}
