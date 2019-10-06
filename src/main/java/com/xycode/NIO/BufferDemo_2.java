package com.xycode.NIO;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class BufferDemo_2 {
	public static void main(String[] args) throws IOException {
//		System.out.println(System.getProperty("user.dir"));
		
		RandomAccessFile randomAccessFile=new RandomAccessFile("input.txt", "rw");
		FileChannel fileChannel=randomAccessFile.getChannel();
		//从index=0开始,映射10个字节
		MappedByteBuffer mappedByteBuffer=fileChannel.map(FileChannel.MapMode.READ_WRITE, 0, 10);
		
		mappedByteBuffer.put(0,(byte)'A');
		mappedByteBuffer.put(7,(byte)'A');
		mappedByteBuffer.put(9,(byte)'A');
	}
}
