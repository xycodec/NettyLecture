package com.xycode.netty.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class TestClientHandler extends SimpleChannelInboundHandler<Long>{
	private int cnt=0;
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Long msg) throws Exception {
		System.out.println("recv[Client]: "+(++cnt)+"th "+", from "+ctx.channel().remoteAddress());
	}
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		ctx.writeAndFlush(Long.MAX_VALUE);
		ctx.writeAndFlush(Long.MAX_VALUE);
		ctx.writeAndFlush(Long.MAX_VALUE);
		ctx.writeAndFlush(Long.MAX_VALUE);
		ctx.writeAndFlush(Long.MAX_VALUE);
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		ctx.close();
	}
}
