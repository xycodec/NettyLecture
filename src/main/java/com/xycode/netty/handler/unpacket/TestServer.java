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
		EventLoopGroup bossGroup=new NioEventLoopGroup();//只管连接
		EventLoopGroup workerGroup=new NioEventLoopGroup();//实际的业务处理
		//ServerBootstrap是netty启动类,封装了netty的一些配置信息
		ServerBootstrap serverBootstrap=new ServerBootstrap();
		serverBootstrap.group(bossGroup,workerGroup)
		.channel(NioServerSocketChannel.class)//反射构建,处理bossGroup的业务
		.childHandler(//应对workerGroup的业务处理
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
