package com.xycode.netty.demo_1;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;

public class TestServerInitializer extends ChannelInitializer<SocketChannel>{

	//netty http Server
	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		ChannelPipeline pipeline=ch.pipeline();
		pipeline.addLast("HttpServerCodec",new HttpServerCodec());
		pipeline.addLast("testHttpServerHandler",new TestHttpServerHandler());
	}

}
