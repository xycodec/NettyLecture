package com.xycode.netty.http;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;

public class TestServerInitializer extends ChannelInitializer<SocketChannel>{

	//netty http Server
	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		ChannelPipeline pipeline=ch.pipeline();
		//添加ssl加密协议
		pipeline.addLast("SSLServer",SSLUtils.createServerSslHandler("server.jks","serverPass",ch));

		pipeline.addLast("HttpServerCodec",new HttpServerCodec());
		pipeline.addLast(new HttpObjectAggregator(204800));
		pipeline.addLast(new HttpContentCompressor());//对发送的消息进行压缩
		pipeline.addLast("testHttpServerHandler",new TestHttpServerHandler());
	}

}
