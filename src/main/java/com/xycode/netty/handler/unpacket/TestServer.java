package com.xycode.netty.handler.unpacket;

import java.util.List;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.handler.codec.ReplayingDecoder;

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

					//ChannelInitializer�ڱ�����֮��,���Զ�remove
					@Override
					public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
						super.handlerRemoved(ctx);
						System.out.println("ChannelInitializer Handler removed!");
					}

					@Override
					protected void initChannel(SocketChannel ch) throws Exception {//childHandler()����workerGroup��ҵ��
						ChannelPipeline pipeline=ch.pipeline();
						pipeline.addLast(new MessageToByteEncoder<PersonProtocol>() {

							@Override
							protected void encode(ChannelHandlerContext ctx, PersonProtocol msg, ByteBuf out)
									throws Exception {
								System.out.println("PersonProtocolEncoder invoked!");
								out.writeInt(msg.getLength());
								out.writeBytes(msg.getContent());
							}
							
						});
						pipeline.addLast(new ReplayingDecoder<Void>() {

							@Override
							protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out)
									throws Exception {
								System.out.println("PersonProtocolDecoder invoked!");
								//��ʼ���
								int length=in.readInt();
								byte[] content=new byte[length];
								in.readBytes(content);//��Ϊ��ReplayingDecoder,���ý��г��ȵ��ж�,��պ�д��content
								
								//����һ����Ϣ
								PersonProtocol personProtocol=new PersonProtocol();
								personProtocol.setLength(length);
								personProtocol.setContent(content);
								out.add(personProtocol);
							}
						});
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
