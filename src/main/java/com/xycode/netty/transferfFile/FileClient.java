package com.xycode.netty.transferfFile;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.stream.ChunkedFile;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.util.CharsetUtil;

import java.io.File;
import java.io.PrintWriter;
import java.nio.charset.Charset;

public class FileClient {
    public static void main(String[] args) {
        EventLoopGroup clientGroup = new NioEventLoopGroup();
        //Bootstrap是netty启动类,封装了netty的一些配置信息
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(clientGroup)
                .channel(NioSocketChannel.class)
                .handler(//handler()方法,应对client的业务处理
                        new ChannelInitializer<SocketChannel>() {
                            @Override
                            protected void initChannel(SocketChannel ch) throws Exception {//childHandler()处理workerGroup的业务
                                ChannelPipeline pipeline = ch.pipeline();
                                pipeline.addLast(new HttpClientCodec());
                                pipeline.addLast(new HttpObjectAggregator(4096*1024));//Http消息聚合,否则收到两条消息
                                //一条是header,一条是content
                                pipeline.addLast(new SimpleChannelInboundHandler<HttpObject>() {
                                    @Override
                                    protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {
                                        if(msg instanceof FullHttpResponse) {
                                            FullHttpResponse response = (FullHttpResponse) msg;
                                            System.out.println("The client receive response of http header is : \n"
                                                    + response.headers().toString());
//                                            System.out.println("The client receive response of http body is : \n"
//                                                    + response.content().toString(CharsetUtil.UTF_8));
                                            PrintWriter printWriter=new PrintWriter("附件.zip");
                                            ByteBuf byteBuf=response.content();
                                            while(byteBuf.isReadable()){
                                                printWriter.print(byteBuf.readByte());
                                            }
                                            printWriter.close();
                                            System.out.println("received!");

                                        }
                                    }

                                    @Override
                                    public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                        FullHttpRequest request = new DefaultFullHttpRequest
                                                (HttpVersion.HTTP_1_1, HttpMethod.GET, "http://localhost:2233/file");
                                        //设置消息头
                                        request.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain");
                                        request.headers().set(HttpHeaderNames.CONTENT_LENGTH, request.content().readableBytes());
                                        ctx.writeAndFlush(request);
                                    }
                                });
                            }
                        });

        try {
            ChannelFuture channelFuture = bootstrap.connect("localhost", 2233).sync();//阻塞等待连接建立成功
            channelFuture.channel().closeFuture().sync();//阻塞等待关闭时间到来
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            clientGroup.shutdownGracefully();
        }

    }
}
