package com.xycode.NIO;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class BufferDemo_1 {

	public static void main(String[] args) throws IOException {
		System.out.println(System.getProperty("user.dir"));
		
		FileInputStream inStream=new FileInputStream("input.txt");
		FileOutputStream outStream=new FileOutputStream("output.txt");
		//NIO��ʽ���ļ���д
		//1.��ȡChannel
		FileChannel inChannel=inStream.getChannel();
		FileChannel outChannel=outStream.getChannel();
		
		//2.��ʼ��Buffer
		ByteBuffer buf=ByteBuffer.allocate(1024);
		
		//3.�����ݴ�Channel��ȡ��Buffer��
		while(true) {
			buf.clear();
			int read=inChannel.read(buf);
			System.out.println("read "+(read==-1?0:read)+" bytes");
			if(read==-1) break;
			buf.flip();
			outChannel.write(buf);
		}
		
	}

}
