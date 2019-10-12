package com.xycode.netty.transferfFile;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.stream.ChunkedStream;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.net.URI;
public class FileServer {
    public static String getFilecodec(String fileName) throws Exception {
        BufferedInputStream bin = new BufferedInputStream(new FileInputStream(fileName));
        int p = (bin.read() << 8) + bin.read();
        bin.close();
        String code = null;

        switch (p) {
            case 0xefbb:
                code = "UTF-8";
                break;
            case 0xfffe:
                code = "Unicode";
                break;
            case 0xfeff:
                code = "UTF-16BE";
                break;
            default:
                code = "GBK";
        }

        return code;
    }
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
                        pipeline.addLast(new HttpObjectAggregator(204800));
                        pipeline.addLast(new SimpleChannelInboundHandler<HttpObject>() {
                            @Override
                            protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {
                                if(msg instanceof HttpRequest) {
                                    HttpRequest httpRequest=(HttpRequest) msg;
                                    System.out.println("request method name:"+httpRequest.method().name());

                                    URI uri=new URI(httpRequest.uri());
                                    if(uri.getPath().equals("/favicon.ico")){//浏览器会自动请求图标
                                        System.out.println("request favicon.ico");
                                        return;

                                    }
                                    if(uri.getPath().equals("/file")){// curl -X GET http://localhost:2233/file
                                        System.out.println("request file");
                                        //发送...
                                        String fileName="E:/数模2019/附件.zip";
                                        String fileCodec=getFilecodec(fileName);
                                        ChunkedStream chunkedStream=new ChunkedStream(new FileInputStream(fileName),8196000);
                                        ByteBufAllocator byteBufAllocator= UnpooledByteBufAllocator.DEFAULT;
                                        while(!chunkedStream.isEndOfInput()){
                                            ByteBuf content=chunkedStream.readChunk(byteBufAllocator);
                                            FullHttpResponse response=new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK,content);
                                            //设置消息头
                                            response.headers().set(HttpHeaderNames.CONTENT_TYPE,"text/plain");
                                            response.headers().set("fileCodec",fileCodec);
                                            response.headers().set(HttpHeaderNames.CONTENT_LENGTH, content.readableBytes());
                                            //发送...
                                            ctx.writeAndFlush(response);

                                            //已发送的
                                            System.out.println(chunkedStream.transferredBytes());
                                        }
                                    }

                                }
                            }
                        });
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
