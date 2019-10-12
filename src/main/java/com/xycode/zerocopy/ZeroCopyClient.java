package com.xycode.zerocopy;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;

public class ZeroCopyClient {

	public static void main(String[] args) throws UnknownHostException, IOException {
		SocketChannel socketChannel=SocketChannel.open();
		socketChannel.connect(new InetSocketAddress("localhost",2233));
		socketChannel.configureBlocking(true);
		String fileName="e:/onos-tutorial-1.0.0r161-ovf.zip";
		FileChannel fileChannel=new FileInputStream(new File(fileName)).getChannel();
	
		long startTime=System.currentTimeMillis();
		long total=0;
		long transferCnt=0;
		while(total<fileChannel.size()) {
			transferCnt=fileChannel.transferTo(total, fileChannel.size(), socketChannel);//transferTo()会调用操作系统的零拷贝
			total+=transferCnt;
		}
		
		System.out.println("send "+total/1000.0+"KB,"+"cost "+(System.currentTimeMillis()-startTime)+" ms");

//		System.out.println(fileChannel.size());
		fileChannel.close();
	
	}

}
