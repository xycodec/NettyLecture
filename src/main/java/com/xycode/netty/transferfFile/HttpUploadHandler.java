package com.xycode.netty.transferfFile;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.multipart.*;
import io.netty.util.ReferenceCountUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class HttpUploadHandler extends SimpleChannelInboundHandler<HttpObject> {

    public HttpUploadHandler() {
        super(false);
    }

    private static final HttpDataFactory factory = new DefaultHttpDataFactory(true);
    private static final String FILE_UPLOAD = "E:/Upload_test/";
    private static final String URI = "/upload";
    private HttpPostRequestDecoder httpDecoder;
    private HttpRequest request;

    @Override
    protected void channelRead0(final ChannelHandlerContext ctx, final HttpObject httpObject)
            throws Exception {
        if (httpObject instanceof HttpRequest) {
            request = (HttpRequest) httpObject;
            if (request.uri().startsWith(URI) && request.method().equals(HttpMethod.POST)) {
                httpDecoder = new HttpPostRequestDecoder(factory, request);
                httpDecoder.setDiscardThreshold(0);
            } else {
                //传递给下一个Handler
                ctx.fireChannelRead(httpObject);
            }
        }
        if (httpObject instanceof HttpContent) {
            if (httpDecoder != null) {
                final HttpContent chunk = (HttpContent) httpObject;
                httpDecoder.offer(chunk);//接收file chunk
                if (chunk instanceof LastHttpContent) {//接收完了
                    writeChunk(ctx);
                    //关闭httpDecoder,回收资源
                    httpDecoder.destroy();
                    httpDecoder = null;
                }
                ReferenceCountUtil.release(httpObject);//回收资源,其实默认已经自动回收了
            } else {
                ctx.fireChannelRead(httpObject);
            }
        }

    }

    private void writeChunk(ChannelHandlerContext ctx) throws IOException {
        while (httpDecoder.hasNext()) {
            InterfaceHttpData data = httpDecoder.next();
            if (data != null && InterfaceHttpData.HttpDataType.FileUpload.equals(data.getHttpDataType())) {
                final FileUpload fileUpload = (FileUpload) data;
                final File file = new File(FILE_UPLOAD + fileUpload.getFilename());
                System.out.println("upload file: "+file);
                try (FileChannel inputChannel = new FileInputStream(fileUpload.getFile()).getChannel();
                     FileChannel outputChannel = new FileOutputStream(file).getChannel()) {
                    outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
                    ctx.writeAndFlush(new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,HttpResponseStatus.OK, Unpooled.copiedBuffer("upload finished!\n".getBytes()))).sync();
                    ctx.channel().close();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println(cause);
        ctx.channel().close();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        if (httpDecoder != null) {
            httpDecoder.cleanFiles();
        }
    }

}
