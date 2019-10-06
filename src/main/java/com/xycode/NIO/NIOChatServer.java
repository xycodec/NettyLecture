package com.xycode.NIO;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class NIOChatServer {

	public static void main(String[] args) throws IOException {
		Selector selector=Selector.open();		
		ServerSocketChannel serverSocketChannel=ServerSocketChannel.open();
		serverSocketChannel.configureBlocking(false);
		ServerSocket serverSocket=serverSocketChannel.socket();
		serverSocket.bind(new InetSocketAddress(2233));
		
		serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);//serverSocketChannel注册到selector,注册事件类型为OP_ACCEPT
		System.out.println("listening port: "+2233);
			
		Map<SocketChannel,String> clientChannelMap=new HashMap<>();
		while(true) {
			selector.select();//select,阻塞方法, Selects a set of keys whose corresponding channels are ready for I/O operations. 
			Set<SelectionKey> selectionKeys=selector.selectedKeys();//selector绑定的key
			Iterator<SelectionKey> iter=selectionKeys.iterator();
			SocketChannel clientChannel;
			while(iter.hasNext()) {
				SelectionKey selectionKey=iter.next();//开始迭代key
				if(selectionKey.isAcceptable()) {//处理accept事件
					serverSocketChannel=(ServerSocketChannel) selectionKey.channel();
					clientChannel=serverSocketChannel.accept();//获得真正处理业务(此处为accept事件)的通道
					clientChannel.configureBlocking(false);
					
					clientChannel.register(selector, SelectionKey.OP_READ);//注册下一个事件
					String key="["+UUID.randomUUID()+"]";
					clientChannelMap.put(clientChannel,key);
					
					System.out.println("connected :"+clientChannel);
				}else if(selectionKey.isReadable()) {//处理read事件
					clientChannel=(SocketChannel) selectionKey.channel();
					ByteBuffer readBuffer=ByteBuffer.allocate(1024);
					int bytesRead=clientChannel.read(readBuffer);//从channel读取数据到buffer
					if(bytesRead>0) {//确切的收到消息了
						readBuffer.flip();
						Charset charset=Charset.forName("utf8");
						String receivedMessage=charset.decode(readBuffer).toString();
						System.out.println(clientChannel+"[server] : "+receivedMessage);
						
						String senderID=null;
						for(SocketChannel socketChannel:clientChannelMap.keySet()) {
							if(socketChannel==clientChannel) {
								senderID=clientChannelMap.get(socketChannel);
							}
						}
						//接下来将收到的消息转发到其它client(已连接的)
						for(SocketChannel socketChannel:clientChannelMap.keySet()) {
							if(!socketChannel.isConnected()) continue;//脱离连接的就不用发送了
							ByteBuffer writeBuffer=ByteBuffer.allocate(1024);
							if(clientChannelMap.get(socketChannel).equals(senderID))
								writeBuffer.put((senderID+"[me]: "+receivedMessage).getBytes());
							else
								writeBuffer.put((senderID+"[client]: "+receivedMessage).getBytes());
							writeBuffer.flip();
							socketChannel.write(writeBuffer);
						}
					}
				}
				iter.remove();//移出当前已处理完的事件
			}
		}
	}

}
