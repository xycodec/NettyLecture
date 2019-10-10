package com.xycode.netty.chatApp;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;

public class NettyChatServer {
	public static void main(String[] args) {
		EventLoopGroup bossGroup=new NioEventLoopGroup();//只管连接
		EventLoopGroup workerGroup=new NioEventLoopGroup();//实际的业务处理
		//ServerBootstrap是netty启动类,封装了netty的一些配置信息
		ServerBootstrap serverBootstrap=new ServerBootstrap();
		serverBootstrap.group(bossGroup,workerGroup)
		.channel(NioServerSocketChannel.class)//处理bossGroup的业务
		.childHandler(//应对workerGroup的业务处理
				new ChannelInitializer<SocketChannel>() {
					@Override
					protected void initChannel(SocketChannel ch) throws Exception {//childHandler()处理workerGroup的业务
						ChannelPipeline pipeline=ch.pipeline();
						pipeline.addLast(new DelimiterBasedFrameDecoder(2048, Delimiters.lineDelimiter()));
						pipeline.addLast(new StringDecoder(CharsetUtil.UTF_8));
						pipeline.addLast(new StringEncoder(CharsetUtil.UTF_8));
						pipeline.addLast(new ChatServerHandler());
					}
		});
		
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
