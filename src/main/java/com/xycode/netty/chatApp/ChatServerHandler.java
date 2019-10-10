package com.xycode.netty.chatApp;


import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

public class ChatServerHandler extends SimpleChannelInboundHandler<String>{
	private static ChannelGroup channelGroup=new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
		Channel channel=ctx.channel();
		for(Channel ch:channelGroup) {
			if(ch!=channel) {
				ch.writeAndFlush("["+channel.remoteAddress()+"] send: "+msg+"\n");
			}else {
				ch.writeAndFlush("[me] : "+msg+"\n");
			}
		}
	}
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		Channel channel=ctx.channel();
		channelGroup.writeAndFlush("[broadcast]: "+channel.remoteAddress()+" online\n");
	}
	
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		Channel channel=ctx.channel();
		channelGroup.writeAndFlush("[broadcast]: "+channel.remoteAddress()+" offline\n");
	}
	
	@Override
	public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
		System.out.println("[broadcast]: "+ctx.channel().remoteAddress()+" unregistered");
	}
	
	@Override
	public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
		System.out.println("[broadcast]: "+ctx.channel().remoteAddress()+" registered");
	}
	
	@Override
	public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
		Channel channel=ctx.channel();
		//��channelGroup�е�����channel������Ϣ,�������Լ�����,����add()�ŵ�����
		channelGroup.writeAndFlush("[broadcast]: "+channel.remoteAddress()+" join\n");
		channelGroup.add(channel);
	}
	
	@Override
	public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
		Channel channel=ctx.channel();
//		channelGroup.remove(channel);//��ʵ����remove(),��ΪchanneGroup���Զ�removeʧЧ��channel
		channelGroup.writeAndFlush("[broadcast]: "+channel.remoteAddress()+" leave\n");
	}
	
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		ctx.close();
	}
}
