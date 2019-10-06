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
		EventLoopGroup bossGroup=new NioEventLoopGroup();//ֻ������
		EventLoopGroup workerGroup=new NioEventLoopGroup();//ʵ�ʵ�ҵ����
		//ServerBootstrap��netty������,��װ��netty��һЩ������Ϣ
		ServerBootstrap serverBootstrap=new ServerBootstrap();
		serverBootstrap.group(bossGroup,workerGroup)  
		.channel(NioServerSocketChannel.class)//���乹��,����bossGroup��ҵ��
		.childHandler(//Ӧ��workerGroup��ҵ����
				new ChannelInitializer<SocketChannel>() {
					@Override
					protected void initChannel(SocketChannel ch) throws Exception {//childHandler()����workerGroup��ҵ��
						ChannelPipeline pipeline=ch.pipeline();
						pipeline.addLast(new MyByteToLongDecoder());
						pipeline.addLast(new MessageToMessageDecoder<Long>() {//MessageToMessageDecoder,ֻ��֤һ��, ����Client������Ϣ

							@Override
							protected void decode(ChannelHandlerContext ctx, Long msg, List<Object> out)
									throws Exception {
								System.out.println("MyMessageToMessageDecoder decoder invoked!");
								System.out.println(String.valueOf(msg));
								out.add(msg);//������һ��Handler
							}
						
							
						});
						pipeline.addLast(new MyLongToByteEncoder());

						pipeline.addLast(new TestServerHandler());
					}
		});
		
		
		try {
			ChannelFuture channelFuture=serverBootstrap.bind(2233).sync();//�����ȴ�bind�ɹ�
			channelFuture.channel().closeFuture().sync();//��channel���ر���,�tִ�����,�����һֱ���������,
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
