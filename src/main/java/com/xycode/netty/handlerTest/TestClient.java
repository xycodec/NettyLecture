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
 * ����handler�����Ƿ��Ǵ��е�
 * ����UDPͨ����˵,������������Э��,��������ȫ��һ�����̵߳�handler
 * ���Ƕ���TCPͨ����˵,����TCP������Э��,netty��Handler����ÿ�����Ӷ������һ�������߳�(��Ȼ,���������������workerGroup�Ĵ�������,Ҳ���������)
 */
public class TestClient {
    public static void main(String[] args) {
        ExecutorService es= Executors.newFixedThreadPool(5);
        for(int i=0;i<1;++i){
            es.execute(new Runnable() {
                @Override
                public void run() {
                    EventLoopGroup clientGroup=new NioEventLoopGroup();
                    //Bootstrap��netty������,��װ��netty��һЩ������Ϣ
                    Bootstrap bootstrap=new Bootstrap();
                    bootstrap.group(clientGroup)
                            .channel(NioSocketChannel.class)
                            .handler(new ChannelInitializer<SocketChannel>() {
                                @Override
                                protected void initChannel(SocketChannel ch) throws Exception {//childHandler()����workerGroup��ҵ��
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

                        ch.channel().closeFuture().sync();//�����ȴ��ر�ʱ�䵽��
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
