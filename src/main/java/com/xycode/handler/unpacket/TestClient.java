package com.xycode.handler.unpacket;

import java.util.List;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.handler.codec.ReplayingDecoder;
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
