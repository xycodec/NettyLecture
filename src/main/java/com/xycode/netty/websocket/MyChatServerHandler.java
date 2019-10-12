package com.xycode.netty.websocket;

import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.util.concurrent.ImmediateEventExecutor;

public class MyChatServerHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {
    private static ChannelGroup channelGroup=new DefaultChannelGroup(ImmediateEventExecutor.INSTANCE);

//    @Override
//    public void userEventTriggered(ChannelHandlerContext ctx,
//                                   Object evt) throws Exception {
//        if (evt == WebSocketServerProtocolHandler
//                .ServerHandshakeStateEvent.HANDSHAKE_COMPLETE) {
//            channelGroup.writeAndFlush(new TextWebSocketFrame(
//                    "Client " + ctx.channel().remoteAddress()+ " joined"));
//            channelGroup.add(ctx.channel());
//        } else {
//            super.userEventTriggered(ctx, evt);
//        }
//    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx,
                             TextWebSocketFrame msg) throws Exception {
        Channel channel=ctx.channel();
        for(Channel ch:channelGroup) {
            if(ch!=channel) {
                ch.writeAndFlush(new TextWebSocketFrame("["+channel.remoteAddress()+"] send: "+msg.text()+"\n"));
            }else {
                ch.writeAndFlush(new TextWebSocketFrame("[me] : "+msg.text()+"\n"));
            }
        }

    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        Channel channel=ctx.channel();
        System.out.println("Client join: "+channel.remoteAddress());
        //向channelGroup中的所有channel发送信息,不用像自己发送,所以add()放到后面
        channelGroup.writeAndFlush(new TextWebSocketFrame("[broadcast]: "+channel.remoteAddress()+" join\n"));
        channelGroup.add(channel);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        Channel channel=ctx.channel();
        System.out.println("Client leave: "+channel.remoteAddress());
		channelGroup.remove(channel);//其实不用remove(),因为channeGroup会自动remove失效的channel
        channelGroup.writeAndFlush(new TextWebSocketFrame("[broadcast]: "+channel.remoteAddress()+" leave\n"));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
