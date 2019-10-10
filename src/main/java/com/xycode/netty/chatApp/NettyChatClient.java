package com.xycode.netty.chatApp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;

public class NettyChatClient {

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
						pipeline.addLast(new DelimiterBasedFrameDecoder(2048, Delimiters.lineDelimiter()));
						pipeline.addLast(new StringDecoder(CharsetUtil.UTF_8));
						pipeline.addLast(new StringEncoder(CharsetUtil.UTF_8));
						pipeline.addLast(new ChatClientHandler());
					}
		});
		
		BufferedReader br=new BufferedReader(new InputStreamReader(System.in));
		try {
			Channel channel=bootstrap.connect("localhost", 2233).sync().channel();
			while(true) {
				channel.writeAndFlush(br.readLine()+"\r\n");
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			clientGroup.shutdownGracefully();
		}

	}

}
