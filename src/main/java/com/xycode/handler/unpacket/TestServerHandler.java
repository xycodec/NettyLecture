package com.xycode.handler.unpacket;

import java.nio.charset.Charset;
import java.util.UUID;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class TestServerHandler extends SimpleChannelInboundHandler<PersonProtocol>{
	private int cnt=0;
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, PersonProtocol msg) throws Exception {
		System.out.println("recv[Server]: "+(++cnt)+"th "+msg.toString(Charset.forName("utf-8"))+", from "+ctx.channel().remoteAddress());
		//想Client发送一条消息
		PersonProtocol personProtocol=new PersonProtocol();
		String response=UUID.randomUUID().toString();
		personProtocol.setLength(response.length());
		personProtocol.setContent(response.getBytes("utf-8"));
		ctx.writeAndFlush(personProtocol);
		
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		ctx.close();
	}
}
