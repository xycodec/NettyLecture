package com.xycode.handler.stickyPacket;

import java.nio.charset.Charset;
import java.util.UUID;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class TestServerHandler extends SimpleChannelInboundHandler<ByteBuf>{
	private int cnt=0;
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
		System.out.println("recv[Server]: "+(++cnt)+"th "+msg.toString(Charset.forName("utf-8"))+", from "+ctx.channel().remoteAddress());
		ctx.writeAndFlush(Unpooled.copiedBuffer(UUID.randomUUID().toString(),Charset.forName("utf-8")));
		
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		ctx.close();
	}
}
