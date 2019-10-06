package com.xycode.netty.handler;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class TestClient {

	public static void main(String[] args) {
		EventLoopGroup clientGroup=new NioEventLoopGroup();
		//Bootstrap是netty启动类,封装了netty的一些配置信息
		Bootstrap bootstrap=new Bootstrap();
		bootstrap.group(clientGroup)
		.channel(NioSocketChannel.class)
		.handler(//handler()方法,应对client的业务处理     
				new ChannelInitializer<SocketChannel>() {
					@Override
					protected void initChannel(SocketChannel ch) throws Exception {//childHandler()处理workerGroup的业务
						ChannelPipeline pipeline=ch.pipeline();
						pipeline.addLast(new MyByteToLongDecoder());
						pipeline.addLast(new MyLongToByteEncoder());

						pipeline.addLast(new TestClientHandler());
					}
					@Override
					public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
						cause.printStackTrace();
						ctx.close();
					}
		});
		
		try {
			ChannelFuture channelFuture=bootstrap.connect("localhost", 2233).sync();//阻塞等待连接建立成功
			channelFuture.channel().closeFuture().sync();//阻塞等待关闭时间到来
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		}finally {
			clientGroup.shutdownGracefully();
		}
	}

}
