package com.xycode.zerocopy;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class ZeroCopyServer {

	public static void main(String[] args) throws IOException {
		ServerSocketChannel serverSocketChannel=ServerSocketChannel.open();
		ServerSocket serverSocket=serverSocketChannel.socket();
		serverSocket.setReuseAddress(true);
		
		serverSocket.bind(new InetSocketAddress(2233));
		
		ByteBuffer byteBuffer=ByteBuffer.allocate(4096);
		while(true) {
			SocketChannel socketChannel=serverSocketChannel.accept();
			socketChannel.configureBlocking(true);
			int readCnt=0;
			while(readCnt!=-1) {
				try {
					readCnt=socketChannel.read(byteBuffer);//接受client的数据
				}catch (IOException e) {
					e.printStackTrace();
					byteBuffer.clear();
					break;
				}
				byteBuffer.rewind();//相比flip(),不修改limit值,limit始终与capacity值一致
			}
		}
	}

}
