package com.xycode.netty.handlerTest;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
/**
 * 测试handler处理是否是串行的
 * 对于UDP通信来说,由于是无连接协议,所以它完全是一个单线程的handler
 * 但是对于TCP通信来说,由于TCP是连接协议,netty的Handler对于每个连接都会分配一个处理线程(当然,如果连接数超过了workerGroup的处理能力,也会产生阻塞)
 */
public class TestClient {
    public static void main(String[] args) {
        ExecutorService es= Executors.newFixedThreadPool(5);
        for(int i=0;i<1;++i){
            es.execute(new Runnable() {
                @Override
                public void run() {
                    EventLoopGroup clientGroup=new NioEventLoopGroup();
                    //Bootstrap是netty启动类,封装了netty的一些配置信息
                    Bootstrap bootstrap=new Bootstrap();
                    bootstrap.group(clientGroup)
                            .channel(NioSocketChannel.class)
                            .handler(new ChannelInitializer<SocketChannel>() {
                                @Override
                                protected void initChannel(SocketChannel ch) throws Exception {//childHandler()处理workerGroup的业务
                                    ChannelPipeline pipeline=ch.pipeline();
                                    pipeline.addLast(new DelimiterBasedFrameDecoder(1024*1024, Unpooled.copiedBuffer("@@@".getBytes())));
                                    pipeline.addLast(new StringDecoder(CharsetUtil.UTF_8));
                                    pipeline.addLast(new StringEncoder(CharsetUtil.UTF_8));
                                    pipeline.addLast(new SimpleChannelInboundHandler<String>() {
                                        @Override
                                        protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
                                            System.out.println("[client]recv: "+msg);
                                        }
                                    });

                                }
                                @Override
                                public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                                    cause.printStackTrace();
                                    ctx.close();
                                }
                            });
                    try {
                        final ChannelFuture ch = bootstrap.connect("localhost", 2233).sync();
                        ch.channel().writeAndFlush("client echo"+"@@@");
//                        ch.channel().writeAndFlush("wait"+"@@@");

                        ch.channel().closeFuture().sync();//阻塞等待关闭时间到来
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }finally {
                        clientGroup.shutdownGracefully().syncUninterruptibly();
                    }

                }
            });
        }

    }
}
