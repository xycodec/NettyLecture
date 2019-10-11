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
        //Bootstrap��netty������,��װ��netty��һЩ������Ϣ
        Bootstrap bootstrap=new Bootstrap();
        bootstrap.group(clientGroup)
                .channel(NioSocketChannel.class)
                .handler(//handler()����,Ӧ��client��ҵ����
                        new ChannelInitializer<SocketChannel>() {
                            @Override
                            protected void initChannel(SocketChannel ch) throws Exception {//childHandler()����workerGroup��ҵ��
                                ChannelPipeline pipeline=ch.pipeline();
                                //���ssl����Э��
                                pipeline.addLast("SSLClient",SSLUtils.createClientSslHandler("client.jks","clientPass",ch));

                                pipeline.addLast("HttpClientCodec",new HttpClientCodec());
                                pipeline.addLast(new HttpObjectAggregator(204800));//Http��Ϣ�ۺ�,�����յ�������Ϣ
                                //һ����header,һ����content
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
                                        //������Ϣͷ
                                        request.headers().set(HttpHeaderNames.CONTENT_TYPE,"text/plain");
                                        request.headers().set(HttpHeaderNames.CONTENT_LENGTH,request.content().readableBytes());
                                        ctx.writeAndFlush(request);
                                    }
                                });
                            }
                        });

        try {
            ChannelFuture channelFuture=bootstrap.connect("localhost", 2233).sync();//�����ȴ����ӽ����ɹ�
            channelFuture.channel().closeFuture().sync();//�����ȴ��ر�ʱ�䵽��
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            clientGroup.shutdownGracefully();
        }

    }
}
