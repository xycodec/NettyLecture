package com.xycode.handler.unpacket;

import java.nio.charset.Charset;
import java.util.UUID;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class TestClientHandler extends SimpleChannelInboundHandler<PersonProtocol>{
	private int cnt=0;
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, PersonProtocol msg) throws Exception {
		System.out.println("recv[Client]: "+(++cnt)+"th "+msg.toString(Charset.forName("utf-8"))+", from "+ctx.channel().remoteAddress());
		
	}
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		for(int i=0;i<1;++i) {
			PersonProtocol personProtocol=new PersonProtocol();
			String response=UUID.randomUUID().toString();
			personProtocol.setLength(response.length());
			personProtocol.setContent(response.getBytes("utf-8"));
			ctx.writeAndFlush(personProtocol);
		}
		
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		ctx.close();
	}
}
