package com.xycode.netty.scheduled;

import com.xycode.netty.handler.MyByteToLongDecoder;
import com.xycode.netty.handler.MyLongToByteEncoder;
import com.xycode.netty.handler.TestClientHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class ScheduledTaskClient {
    public static void main(String[] args) {
        EventLoopGroup clientGroup=new NioEventLoopGroup();
        //Bootstrap��netty������,��װ��netty��һЩ������Ϣ
        Bootstrap bootstrap=new Bootstrap();
        bootstrap.group(clientGroup)  
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {//childHandler()����workerGroup��ҵ��
                        ChannelPipeline pipeline=ch.pipeline();
                        pipeline.addLast(new MyByteToLongDecoder());
                        pipeline.addLast(new MyLongToByteEncoder());

                        pipeline.addLast(new TestClientHandler());
                    }
                    @Override
                    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                        cause.printStackTrace();
                        ctx.close();
                    }
                });
        final ChannelFuture ch;
        try {
            ch = bootstrap.connect("localhost", 2233).sync();
            ch.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if(future.isSuccess()) System.out.println(">> Client connect successfully!");
                    else{
                        System.out.println(">> Client fail to connect!");
                        future.cause().printStackTrace();
                    }
                }

            });
            ScheduledFuture<?> future=ch.channel().eventLoop().scheduleAtFixedRate(()-> {
                ch.channel().writeAndFlush((long)System.currentTimeMillis()/1000%1000);
            },0,3, TimeUnit.SECONDS);//3sΪ���ڷ���
            future.get();
            ch.channel().closeFuture().sync();//�����ȴ��ر�ʱ�䵽��
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }finally {
            clientGroup.shutdownGracefully().syncUninterruptibly();
    }

    }
}
