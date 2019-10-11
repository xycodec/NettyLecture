package com.xycode.netty.http;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

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
                                //添加ssl加密协议
                                pipeline.addLast("SSLClient",SSLUtils.createClientSslHandler("client.jks","clientPass",ch));

                                pipeline.addLast("HttpClientCodec",new HttpClientCodec());
                                pipeline.addLast(new HttpObjectAggregator(204800));//Http消息聚合,否则收到两条消息
                                //一条是header,一条是content
                                pipeline.addLast(new SimpleChannelInboundHandler<HttpObject>(){
                                    @Override
                                    protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {
                                        if(msg instanceof FullHttpResponse) {
                                            FullHttpResponse response = (FullHttpResponse) msg;
                                            System.out.println("The client receive response of http header is : \n"
                                                    + response.headers().toString());
                                            System.out.println("The client receive response of http body is : \n"
                                                    + response.content().toString(CharsetUtil.UTF_8));
                                        }
                                    }

                                    @Override
                                    public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                        FullHttpRequest request=new DefaultFullHttpRequest
                                                (HttpVersion.HTTP_1_1,HttpMethod.GET,"wss://localhost:2233/xycode");
                                        //设置消息头
                                        request.headers().set(HttpHeaderNames.CONTENT_TYPE,"text/plain");
                                        request.headers().set(HttpHeaderNames.CONTENT_LENGTH,request.content().readableBytes());
                                        ctx.writeAndFlush(request);
                                    }
                                });
                            }
                        });

        try {
            ChannelFuture channelFuture=bootstrap.connect("localhost", 2233).sync();//阻塞等待连接建立成功
            channelFuture.channel().closeFuture().sync();//阻塞等待关闭时间到来
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            clientGroup.shutdownGracefully();
        }

    }
}
