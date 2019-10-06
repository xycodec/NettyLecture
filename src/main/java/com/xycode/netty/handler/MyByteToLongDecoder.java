package com.xycode.netty.handler;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
public class MyByteToLongDecoder extends ByteToMessageDecoder {

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		System.out.println("decoder invoked!");
		System.out.println(in.readableBytes());
		if(in.readableBytes()>=8) {
			out.add(in.readLong());// in (byte)-> out(long),是一个解码的过程, 属于InBoundHandler,存储到out中,以便下一个handler调用
		}
		
	}

}
