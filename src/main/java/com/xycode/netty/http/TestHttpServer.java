package com.xycode.netty.http;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class TestHttpServer {

	public static void main(String[] args) {
		EventLoopGroup bossGroup=new NioEventLoopGroup();//只管连接
		EventLoopGroup workerGroup=new NioEventLoopGroup();//实际的业务处理
		//ServerBootstrap是netty启动类,封装了netty的一些配置信息
		ServerBootstrap serverBootstrap=new ServerBootstrap();
		serverBootstrap.group(bossGroup,workerGroup)
                .channel(NioServerSocketChannel.class)//处理bossGroup的业务
                .childHandler(new TestServerInitializer());//childHandler()处理workerGroup的业务

		ChannelFuture channelFuture=serverBootstrap.bind(2233);
		try {
			channelFuture.channel().closeFuture().sync();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}finally {
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}

	}

}
