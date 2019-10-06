package com.xycode.netty.scheduled;

import com.xycode.netty.handler.MyByteToLongDecoder;
import com.xycode.netty.handler.MyLongToByteEncoder;
import com.xycode.netty.handler.TestServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.List;

public class TestServer {

	public static void main(String[] args) {
		EventLoopGroup bossGroup=new NioEventLoopGroup();//只管连接
		EventLoopGroup workerGroup=new NioEventLoopGroup();//实际的业务处理
		//ServerBootstrap是netty启动类,封装了netty的一些配置信息
		ServerBootstrap serverBootstrap=new ServerBootstrap();
		serverBootstrap.group(bossGroup,workerGroup)  
		.channel(NioServerSocketChannel.class)//反射构建,处理bossGroup的业务
		.childHandler(//应对workerGroup的业务处理
				new ChannelInitializer<SocketChannel>() {
					@Override
					protected void initChannel(SocketChannel ch) throws Exception {//childHandler()处理workerGroup的业务
						ChannelPipeline pipeline=ch.pipeline();
						pipeline.addLast(new MyByteToLongDecoder());
						pipeline.addLast(new MessageToMessageDecoder<Long>() {//MessageToMessageDecoder,只验证一下, 不向Client发送信息

							@Override
							protected void decode(ChannelHandlerContext ctx, Long msg, List<Object> out)
									throws Exception {
								System.out.println("MyMessageToMessageDecoder decoder invoked!");
								System.out.println(String.valueOf(msg));
								out.add(msg);//传给下一个Handler
							}
						
							
						});
						pipeline.addLast(new MyLongToByteEncoder());

						pipeline.addLast(new TestServerHandler());
					}
		});
		
		
		try {
			ChannelFuture channelFuture=serverBootstrap.bind(2233).sync();//阻塞等待bind成功
			channelFuture.channel().closeFuture().sync();//若channel被关闭了,則执行完毕,否则会一直阻塞在这儿,
			//closeFuture(): Returns the ChannelFuture which will be notified when this channel is closed. This method always returns the same future instance.
			//sync(): sync():Waits for this future until it is done, and rethrows the cause of the failure if this future failed.
		} catch (InterruptedException e) {
			e.printStackTrace();
		}finally {
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}

}
