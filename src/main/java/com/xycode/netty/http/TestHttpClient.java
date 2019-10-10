package com.xycode.netty.http;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
public class TestHttpClient {
    public static void main(String[] args) {
        EventLoopGroup clientGroup=new NioEventLoopGroup();
        //Bootstrap是netty启动类,封装了netty的一些配置信息
        Bootstrap bootstrap=new Bootstrap();
        bootstrap.group(clientGroup)
                .channel(NioSocketChannel.class)
                .handler(//handler()方法,应对client的业务处理
                        new ChannelInitializer<SocketChannel>() {
                            @Override
                            protected void initChannel(SocketChannel ch) throws Exception {//childHandler()处理workerGroup的业务
                                ChannelPipeline pipeline=ch.pipeline();
                                pipeline.addLast("HttpClientCodec",new HttpClientCodec());
                                pipeline.addLast(new SimpleChannelInboundHandler<HttpObject>(){
                                    @Override
                                    protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {
                                        if(msg instanceof DefaultFullHttpResponse){
                                            DefaultFullHttpResponse response=(DefaultFullHttpResponse)msg;
                                            System.out.println(response.content());
                                        }

                                    }

                                    @Override
                                    public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                        FullHttpRequest request=new DefaultFullHttpRequest
                                                (HttpVersion.HTTP_1_1,HttpMethod.GET,"http://localhost:2233/xycode");
                                        //设置消息头
                                        request.headers().set(HttpHeaderNames.CONTENT_TYPE,"text/plain");
                                        request.headers().set(HttpHeaderNames.CONTENT_LENGTH,request.content().readableBytes());
                                        ctx.writeAndFlush(request);
                                    }
                                });
                            }
                        });

        try {
            Channel channel=bootstrap.connect("localhost", 2233).sync().channel();
            channel.closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            clientGroup.shutdownGracefully().syncUninterruptibly();
        }

    }
}
