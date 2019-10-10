package com.xycode.netty.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

import java.net.URI;

public class TestHttpServerHandler extends SimpleChannelInboundHandler<HttpObject>{

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {
		System.out.println("remote ip: "+ctx.channel().remoteAddress());
		if(msg instanceof HttpRequest) {
			HttpRequest httpRequest=(HttpRequest) msg;
			System.out.println("request method name:"+httpRequest.method().name());
			
			URI uri=new URI(httpRequest.uri());
			String responseMsg="hello!\n";
			if(uri.getPath().equals("/favicon.ico")){//��������Զ�����ͼ��
				System.out.println("request favicon.ico");
				responseMsg+="request favicon.ico\n";

			}
			if(uri.getPath().equals("/xycode")){// curl -X GET http://localhost:2233/xycode
				System.out.println("welcome,xycode");
				responseMsg+="welcome,xycode\n";
			}
			
			//ȡ��ByteBuf��Ϊ����java.nio��ByteBuffer������
			ByteBuf content=Unpooled.copiedBuffer(responseMsg,CharsetUtil.UTF_8);
			FullHttpResponse response=new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK,content);
			//������Ϣͷ
			response.headers().set(HttpHeaderNames.CONTENT_TYPE,"text/plain");
			response.headers().set(HttpHeaderNames.CONTENT_LENGTH, content.readableBytes());

			//����...
			ctx.writeAndFlush(response);
		}
	}

//	@Override
//	public void channelActive(ChannelHandlerContext ctx) throws Exception {
//		System.out.println("channel active");
//		super.channelActive(ctx);
//	}
//
//	@Override
//	public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
//		System.out.println("channel registered");
//		super.channelRegistered(ctx);
//	}
//
//	//��������������Ӹ��û���,channelInactive(),channelUnregistered���ܲ������������������ִ��
//	@Override
//	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
//		System.out.println("channel inactive");
//		super.channelInactive(ctx);
//	}
//
//	@Override
//	public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
//		System.out.println("channel unregistered");
//		super.channelUnregistered(ctx);
//	}
//
//
//	@Override
//	public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
//		System.out.println("handler added");
//		super.handlerAdded(ctx);
//	}
	
	
}
