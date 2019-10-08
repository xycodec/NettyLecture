package com.xycode.netty.udp;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;

public class UDPServer {
    public static void main(String[] args) {
        Bootstrap bootstrap=new Bootstrap();
        EventLoopGroup servertGroup=new NioEventLoopGroup();
        bootstrap
                .group(servertGroup)
                .channel(NioDatagramChannel.class)
                .handler(
                        new SimpleChannelInboundHandler<DatagramPacket>() {
                            @Override
                            protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket msg) throws Exception {

                                System.out.println("[server]recv: "+msg.content().toString(Charset.forName("utf-8")));
                            }
                        }
                );
        //server «bind()
        ChannelFuture future=bootstrap.bind(new InetSocketAddress("localhost",2233));
        future.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if(future.isSuccess()) System.out.println("[server]Channel bound!");
                else{
                    System.err.println("[server]Bind attempt failed");
                    future.cause().printStackTrace();
                }
            }
        });
        try {
            future.sync();
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            servertGroup.shutdownGracefully().syncUninterruptibly();
        }


    }
}
