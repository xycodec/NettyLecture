package com.xycode.netty.demo_1;

import java.net.URI;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.CharsetUtil;

public class TestHttpServerHandler extends SimpleChannelInboundHandler<HttpObject>{

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {
		System.out.println("remote ip: "+ctx.channel().remoteAddress());
		if(msg instanceof HttpRequest) {
			HttpRequest httpRequest=(HttpRequest) msg;
			System.out.println("requeset method name:"+httpRequest.method().name());
			
			URI uri=new URI(httpRequest.uri());
			if(uri.getPath().equals("/favicon.ico")){//浏览器会自动请求图标
				System.out.println("request favicon.ico");
				return;
			}
			if(uri.getPath().equals("/xycode")){// curl -X GET http://localhost:2233/xycode
				System.out.println("welcome,xycode");
			}
			
			//ByteBuf为了与java.nio的ByteBuffer区别开来
			ByteBuf content=Unpooled.copiedBuffer("hello xycode\n",CharsetUtil.UTF_8);
			FullHttpResponse response=new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK,content);
			response.headers().set(HttpHeaderNames.CONTENT_TYPE,"text/plain");
			response.headers().set(HttpHeaderNames.CONTENT_LENGTH, content.readableBytes());
			
			ctx.writeAndFlush(response);
			ctx.channel().close();
		}
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		System.out.println("channel active");
		super.channelActive(ctx);
	}
	
	@Override
	public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
		System.out.println("channel registered");
		super.channelRegistered(ctx);
	}

	//由于浏览器的连接复用机制,channelInactive(),channelUnregistered可能不会在请求结束后立即执行
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		System.out.println("channel inactive");
		super.channelInactive(ctx);
	}
	
	@Override
	public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
		System.out.println("channel unregistered");
		super.channelUnregistered(ctx);
	}
	
	
	@Override
	public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
		System.out.println("handler added");
		super.handlerAdded(ctx);
	}
	
	
}
