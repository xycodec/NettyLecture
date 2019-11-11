package com.xycode.netty.handlerTest;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;

import java.util.concurrent.TimeUnit;

/**
 * 测试handler处理是否是串行的
 * 对于UDP通信来说,由于是无连接协议,所以它完全是一个单线程的handler
 * 但是对于TCP通信来说,由于TCP是连接协议,netty的Handler对于每个连接都会分配一个处理线程(当然,如果连接数超过了workerGroup的处理能力,也会产生阻塞)
 */
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
								pipeline.addLast(new DelimiterBasedFrameDecoder(1024*1024, Unpooled.copiedBuffer("@@@".getBytes())));
								pipeline.addLast(new StringDecoder(CharsetUtil.UTF_8));
								pipeline.addLast(new StringEncoder(CharsetUtil.UTF_8));
								pipeline.addLast(new SimpleChannelInboundHandler<String>() {
									@Override
									protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
										if(msg.equals("wait")){
											System.out.println(Thread.currentThread().getName()+" waiting...");
											TimeUnit.SECONDS.sleep(5);
											System.out.println(Thread.currentThread().getName()+" done");
										}else{
											System.out.println("[server]recv: "+msg);
										}
									}
								});
							}
						});

		try {
			ChannelFuture channelFuture=serverBootstrap.bind(2233).sync();//阻塞等待bind成功
			channelFuture.channel().closeFuture().sync();//若channel被关闭了,則执行完毕,否则会一直阻塞在这儿
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
