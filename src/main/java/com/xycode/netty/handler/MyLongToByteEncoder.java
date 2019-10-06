package com.xycode.netty.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class MyLongToByteEncoder extends MessageToByteEncoder<Long> {

	@Override
	protected void encode(ChannelHandlerContext ctx, Long msg, ByteBuf out) throws Exception {
		System.out.println("encoder invoked!");
		System.out.println(msg);
		out.writeLong(msg);//msg(long) -> out(byte),是一个编码的过程,属于OutBoundHandler,写入到buf,以便下一个Handler使用
	}

}
