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
 * 粘包问题展示,虽然Client连续发送10条消息,但是由于粘包问题,Server会将这10条消息看出一条消息
 * @author xycode
 *
 */
public class TestClient {

	public static void main(String[] args) {
		EventLoopGroup clientGroup=new NioEventLoopGroup();
		//ServerBootstrap是netty启动类,封装了netty的一些配置信息
		Bootstrap bootstrap=new Bootstrap();
		bootstrap.group(clientGroup)
		.channel(NioSocketChannel.class)
		.handler(//handler()方法,应对client的业务处理     
				new ChannelInitializer<SocketChannel>() {
					//ChannelInitializer在被调用之后,会自动remove
					@Override
					public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
						super.handlerRemoved(ctx);
						System.out.println("ChannelInitializer Handler removed!");
					}
					@Override
					protected void initChannel(SocketChannel ch) throws Exception {//childHandler()处理workerGroup的业务
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
								//开始拆包
								int length=in.readInt();
								byte[] content=new byte[length];
								in.readBytes(content);//因为是ReplayingDecoder,不用进行长度的判断,会刚好写满content
								
								//发送一条消息
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
			ChannelFuture channelFuture=bootstrap.connect("localhost", 2233).sync();//阻塞等待连接建立成功
			channelFuture.channel().closeFuture().sync();//阻塞等待关闭时间到来
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		}finally {
			clientGroup.shutdownGracefully();
		}
	}

}
