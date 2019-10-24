package com.xycode.netty.udp;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.util.CharsetUtil;

public class UDPServer {
    public static void main(String[] args) {
        Bootstrap bootstrap=new Bootstrap();
        EventLoopGroup serverGroup=new NioEventLoopGroup();
        bootstrap
                .group(serverGroup)
                .channel(NioDatagramChannel.class)
                //.option(ChannelOption.SO_BROADCAST, true)
                .handler(
                        new ChannelInitializer<NioDatagramChannel>() {
                            @Override
                            protected void initChannel(NioDatagramChannel ch) throws Exception {
                                ChannelPipeline pipeline=ch.pipeline();
                                pipeline.addLast(new SimpleChannelInboundHandler<DatagramPacket>() {
                                    @Override
                                    protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket msg) throws Exception {
                                        System.out.println("[server] recv: "+msg.content().toString(CharsetUtil.UTF_8)+", from "+msg.sender());
                                        ctx.writeAndFlush(new DatagramPacket(
                                                Unpooled.copiedBuffer("this is a echo from server!".getBytes()),
                                                msg.sender()));
                                    }
                                });
                            }
                        }

                );

        ChannelFuture future=bootstrap.bind(2233);
        try {
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            serverGroup.shutdownGracefully().syncUninterruptibly();
        }

    }
}
