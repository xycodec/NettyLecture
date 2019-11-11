package com.xycode.netty.udp;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.util.CharsetUtil;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;

public class UDPClient {
    public static void main(String[] args) {
        Bootstrap bootstrap=new Bootstrap();
        EventLoopGroup clientGroup=new NioEventLoopGroup();
        bootstrap
                .group(clientGroup)
                .channel(NioDatagramChannel.class)
                //.option(ChannelOption.SO_BROADCAST, true)
                .handler(new ChannelInitializer<DatagramChannel>() {
                    @Override
                    protected void initChannel(DatagramChannel ch) throws Exception {
                        ChannelPipeline pipeline=ch.pipeline();
                        pipeline.addLast(new SimpleChannelInboundHandler<DatagramPacket>() {
                            @Override
                            protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket msg) throws Exception {
                                System.out.println("[client] recv: "+msg.content().toString(CharsetUtil.UTF_8)+", from "+msg.sender());
                            }

                        });
                    }
                });

        ChannelFuture future=bootstrap.bind(0);//这里端口号填0,实际上表示随机分配端口号
        future.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if(future.isSuccess()) System.out.println("[client]: Channel bound!");
                else{
                    System.err.println("[client]: Bind attempt failed");
                    future.cause().printStackTrace();
                }
            }
        });
        DatagramPacket msg=new DatagramPacket(Unpooled.copiedBuffer("hello, this a echo from client!"
                .getBytes(CharsetUtil.UTF_8)),
                new InetSocketAddress("localhost",2233));
        future.channel().writeAndFlush(msg);
        msg=new DatagramPacket(Unpooled.copiedBuffer("wait"
                .getBytes(CharsetUtil.UTF_8)),
                new InetSocketAddress("localhost",2233));
        future.channel().writeAndFlush(msg);
        try {
            future.sync();
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            clientGroup.shutdownGracefully().syncUninterruptibly();
        }
    }
}
