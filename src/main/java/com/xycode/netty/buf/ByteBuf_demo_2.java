package com.xycode.netty.buf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.Unpooled;

public class ByteBuf_demo_2 {
	public static void main(String[] args) {
		CompositeByteBuf compositeByteBuf=Unpooled.compositeBuffer();//��ָ�����ȵĻ�,Netty��ʵ������һ������Ӧ���ȵ�Buffer
		ByteBuf heapBuf=Unpooled.buffer(10);
		ByteBuf directBuf=Unpooled.directBuffer(20);
		compositeByteBuf.addComponents(heapBuf,directBuf);//��ϲ�ͬ��Buffer
		
		compositeByteBuf.forEach(System.out::println);
		
		
	}
}
