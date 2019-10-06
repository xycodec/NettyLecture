package com.xycode.handler.stickyPacket;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
/**
 * ճ������չʾ,��ȻClient��������10����Ϣ,��������ճ������,Server�Ὣ��10����Ϣ����һ����Ϣ
 * @author xycode
 *
 */
public class TestClient {

	public static void main(String[] args) {
		EventLoopGroup clientGroup=new NioEventLoopGroup();
		//ServerBootstrap��netty������,��װ��netty��һЩ������Ϣ
		Bootstrap bootstrap=new Bootstrap();
		bootstrap.group(clientGroup)
		.channel(NioSocketChannel.class)
		.handler(//handler()����,Ӧ��client��ҵ����     
				new ChannelInitializer<SocketChannel>() {
					@Override
					protected void initChannel(SocketChannel ch) throws Exception {//childHandler()����workerGroup��ҵ��
						ChannelPipeline pipeline=ch.pipeline();
						
						pipeline.addLast(new TestClientHandler());
					}
					@Override
					public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
						cause.printStackTrace();
						ctx.close();
					}
		});
		
		try {
			ChannelFuture channelFuture=bootstrap.connect("localhost", 2233).sync();//�����ȴ����ӽ����ɹ�
			channelFuture.channel().closeFuture().sync();//�����ȴ��ر�ʱ�䵽��
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		}finally {
			clientGroup.shutdownGracefully();
		}
	}

}
