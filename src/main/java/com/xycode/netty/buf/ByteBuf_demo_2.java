package com.xycode.netty.buf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.Unpooled;

public class ByteBuf_demo_2 {
	public static void main(String[] args) {
		CompositeByteBuf compositeByteBuf=Unpooled.compositeBuffer();//不指定长度的话,Netty中实际上是一种自适应长度的Buffer
		ByteBuf heapBuf=Unpooled.buffer(10);
		ByteBuf directBuf=Unpooled.directBuffer(20);
		compositeByteBuf.addComponents(heapBuf,directBuf);//组合不同的Buffer
		
		compositeByteBuf.forEach(System.out::println);
		
		
	}
}
