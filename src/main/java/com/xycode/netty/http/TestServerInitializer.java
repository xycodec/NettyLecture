package com.xycode.netty.http;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslHandler;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;


public class TestServerInitializer extends ChannelInitializer<SocketChannel>{

	//netty http Server
	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		ChannelPipeline pipeline=ch.pipeline();
		//添加ssl加密协议
		pipeline.addLast("SSLServer",SSLUtils.createServerSslHandler("server.jks","sNetty",ch));

		pipeline.addLast("HttpServerCodec",new HttpServerCodec());
		pipeline.addLast(new HttpObjectAggregator(204800));
		pipeline.addLast("testHttpServerHandler",new TestHttpServerHandler());
	}

}
