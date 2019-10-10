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
        //Bootstrap��netty������,��װ��netty��һЩ������Ϣ
        Bootstrap bootstrap=new Bootstrap();
        bootstrap.group(clientGroup)
                .channel(NioSocketChannel.class)
                .handler(//handler()����,Ӧ��client��ҵ����
                        new ChannelInitializer<SocketChannel>() {
                            @Override
                            protected void initChannel(SocketChannel ch) throws Exception {//childHandler()����workerGroup��ҵ��
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
                                        //������Ϣͷ
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
