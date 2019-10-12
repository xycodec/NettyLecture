package com.xycode.netty.transferfFile;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 * 使用curl来模拟客户端,与FileServer2交互
 */
public class FileServer2 {
    public static void main(String[] args) {
        EventLoopGroup bossGroup=new NioEventLoopGroup();//只管连接
        EventLoopGroup workerGroup=new NioEventLoopGroup();//实际的业务处理
        //ServerBootstrap是netty启动类,封装了netty的一些配置信息
        ServerBootstrap serverBootstrap=new ServerBootstrap();
        serverBootstrap.group(bossGroup,workerGroup)
                .channel(NioServerSocketChannel.class)//处理bossGroup的业务
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline=ch.pipeline();
                        pipeline.addLast(new HttpServerCodec());
                        pipeline.addLast(new HttpObjectAggregator(4096*1024));
                        pipeline.addLast(new ChunkedWriteHandler());
                        pipeline.addLast(new HttpDownloadHandler());//用于支持Http协议的文件下载,下载服务器上的文件
                        //使用curl: curl -X GET http://localhost:2233/file/filename --output filename,大文件也适用
                        pipeline.addLast(new HttpUploadHandler());//用于支持Http协议的文件上传,将本地文件上传至服务器
                        //使用curl: curl -X POST http://localhost:2233/upload/  -F "file=@filename",不适用大文件
                    }
                });

        ChannelFuture channelFuture=serverBootstrap.bind(2233);
        try {
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }

    }
}
