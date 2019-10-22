package com.xycode.netty.transferfFile;

import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class HttpDownloadHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    public HttpDownloadHandler() {
        super(false);
    }

    private String filePath = "E:/";

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) {
        String uri = request.uri();
        if (uri.startsWith("/file") && request.method().equals(HttpMethod.GET)) {
            String tmp=uri.replaceFirst("/file/","");
            HttpResponse generalResponse = null;
            File file = new File(filePath+tmp);
            try {
                final RandomAccessFile raf = new RandomAccessFile(file, "r");
                long fileLength = raf.length();
                HttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
                response.headers().set(HttpHeaderNames.CONTENT_LENGTH, fileLength);
                response.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/file");
                response.headers().add(HttpHeaderNames.CONTENT_DISPOSITION, String.format("attachment; filename=\"%s\"", file.getName()));
                ctx.writeAndFlush(response);

                //FileRegion
                ChannelFuture sendFileFuture = ctx.writeAndFlush(new DefaultFileRegion(raf.getChannel(), 0, fileLength), ctx.newProgressivePromise());
                sendFileFuture.addListener(new ChannelProgressiveFutureListener() {
                    @Override
                    public void operationComplete(ChannelProgressiveFuture future)
                            throws Exception {
                        System.out.println(file.getName()+" transfer complete.");
                        raf.close();//因为ctx.write是异步的,所以通过Listener来close
                    }

                    @Override
                    public void operationProgressed(ChannelProgressiveFuture future,
                                                    long progress, long total) throws Exception {
                        if (total < 0) {
                            System.out.println(file.getName()+" transfer progress:"+progress);
                        } else {
                            System.out.println(file.getName()+" transfer progress: "+progress+"KB(total: "+total+"KB)");
                        }
                    }
                });
                ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
            } catch (FileNotFoundException e) {
                System.out.println(file.getPath()+" not found");
                generalResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,HttpResponseStatus.NOT_FOUND, Unpooled.copiedBuffer("file not found!\n".getBytes()));
                try {
                    ctx.writeAndFlush(generalResponse).sync();
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
                ctx.channel().close();
            } catch (IOException e) {
                System.out.println(file.getName()+" has a IOException: " +e.getMessage());
                generalResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,HttpResponseStatus.INTERNAL_SERVER_ERROR,Unpooled.copiedBuffer((e.getMessage()+"\n").getBytes()));
                try {
                    ctx.writeAndFlush(generalResponse).sync();
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
                ctx.channel().close();
            }
        } else {
            ctx.fireChannelRead(request);//传递给下一个handler
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable e) {
        System.out.println(e.getCause());
        ctx.close();

    }
}