package com.xycode.NIO;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NIOChatClient {

	public static void main(String[] args) throws IOException {
		Selector selector=Selector.open();
		SocketChannel socketChannel=SocketChannel.open();
		socketChannel.configureBlocking(false);
		socketChannel.register(selector, SelectionKey.OP_CONNECT);
		socketChannel.connect(new InetSocketAddress(2233));
		while(true) {
			selector.select();
			Set<SelectionKey> selectionKeys=selector.selectedKeys();
			for(SelectionKey selectionKey:selectionKeys) {
				if(selectionKey.isConnectable()) {//对于client来说没有accept这一说,所以这里是connect事件
					SocketChannel clientChannel=(SocketChannel) selectionKey.channel();
					if(clientChannel.isConnectionPending()) {
						clientChannel.finishConnect();
						ByteBuffer writeBuffer=ByteBuffer.allocate(1024);
						writeBuffer.put(("At "+LocalDateTime.now()+" connectd").getBytes());
						writeBuffer.flip();
						clientChannel.write(writeBuffer);//write到Server端
						ExecutorService es=Executors.newCachedThreadPool();
						es.submit(new Runnable() {//因为需要用户输入,所以需要新建一个线程阻塞在这儿
							
							@Override
							public void run() {
								while(true) {
									writeBuffer.clear();
									InputStreamReader input=new InputStreamReader(System.in);
									BufferedReader br=new BufferedReader(input);
									try {
										String sendMessage=br.readLine();
										writeBuffer.put(sendMessage.getBytes());
										writeBuffer.flip();
										clientChannel.write(writeBuffer);//write to Server
									} catch (IOException e) {
										e.printStackTrace();
									}
									
								}
							}
						});
						clientChannel.register(selector, SelectionKey.OP_READ);
					}
				}else if(selectionKey.isReadable()) {
					SocketChannel clientChannel = (SocketChannel) selectionKey.channel();
					ByteBuffer readBuffer=ByteBuffer.allocate(1024);
					int count=clientChannel.read(readBuffer);
					if(count>0) {
						readBuffer.flip();
						Charset charset=Charset.forName("utf8");
						String receivedMessage=charset.decode(readBuffer).toString();
						
						System.out.println("received: "+receivedMessage);
						
					}
				}
				
			}
//			selectionKeys.clear();
		}
		
	}

}
