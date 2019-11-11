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
 * ����handler�����Ƿ��Ǵ��е�
 * ����UDPͨ����˵,������������Э��,��������ȫ��һ�����̵߳�handler
 * ���Ƕ���TCPͨ����˵,����TCP������Э��,netty��Handler����ÿ�����Ӷ������һ�������߳�(��Ȼ,���������������workerGroup�Ĵ�������,Ҳ���������)
 */
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
			ChannelFuture channelFuture=serverBootstrap.bind(2233).sync();//�����ȴ�bind�ɹ�
			channelFuture.channel().closeFuture().sync();//��channel���ر���,�tִ�����,�����һֱ���������
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
