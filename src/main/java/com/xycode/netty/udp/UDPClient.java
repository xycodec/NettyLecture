package com.xycode.netty.udp;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;

public class UDPClient {
    public static void main(String[] args) {
        Bootstrap bootstrap=new Bootstrap();
        EventLoopGroup clientGroup=new NioEventLoopGroup();
        bootstrap
                .group(clientGroup)
                .channel(NioDatagramChannel.class)
                .handler(
                        new SimpleChannelInboundHandler<DatagramPacket>() {
                            @Override
                            protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket msg) throws Exception {
                                System.out.println("[client]recv: "+msg.content());
                            }

                            @Override
                            public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                System.out.println("[client]: "+ctx.channel().remoteAddress()+" connected!");
                                DatagramPacket msg=new DatagramPacket(Unpooled.copiedBuffer("hello,this a echo from client!"
                                        .getBytes(Charset.forName("utf-8"))),
                                        (InetSocketAddress) ctx.channel().remoteAddress());

                                ctx.writeAndFlush(msg);
                            }
                        }
                );
        //client «connect()
        ChannelFuture future=bootstrap.connect(new InetSocketAddress("localhost",2233));
        future.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if(future.isSuccess()) System.out.println("[client]Channel bound!");
                else{
                    System.err.println("[client]Bind attempt failed");
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
            clientGroup.shutdownGracefully().syncUninterruptibly();
        }
    }
}
