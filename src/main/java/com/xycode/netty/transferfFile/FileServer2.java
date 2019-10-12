package com.xycode.netty.transferfFile;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 * ʹ��curl��ģ��ͻ���,��FileServer2����
 */
public class FileServer2 {
    public static void main(String[] args) {
        EventLoopGroup bossGroup=new NioEventLoopGroup();//ֻ������
        EventLoopGroup workerGroup=new NioEventLoopGroup();//ʵ�ʵ�ҵ����
        //ServerBootstrap��netty������,��װ��netty��һЩ������Ϣ
        ServerBootstrap serverBootstrap=new ServerBootstrap();
        serverBootstrap.group(bossGroup,workerGroup)
                .channel(NioServerSocketChannel.class)//����bossGroup��ҵ��
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline=ch.pipeline();
                        pipeline.addLast(new HttpServerCodec());
                        pipeline.addLast(new HttpObjectAggregator(4096*1024));
                        pipeline.addLast(new ChunkedWriteHandler());
                        pipeline.addLast(new HttpDownloadHandler());//����֧��HttpЭ����ļ�����,���ط������ϵ��ļ�
                        //ʹ��curl: curl -X GET http://localhost:2233/file/filename --output filename,���ļ�Ҳ����
                        pipeline.addLast(new HttpUploadHandler());//����֧��HttpЭ����ļ��ϴ�,�������ļ��ϴ���������
                        //ʹ��curl: curl -X POST http://localhost:2233/upload/  -F "file=@filename",�����ô��ļ�
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
