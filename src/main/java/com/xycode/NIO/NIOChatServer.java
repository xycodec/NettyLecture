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
		
		serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);//serverSocketChannelע�ᵽselector,ע���¼�����ΪOP_ACCEPT
		System.out.println("listening port: "+2233);
			
		Map<SocketChannel,String> clientChannelMap=new HashMap<>();
		while(true) {
			selector.select();//select,��������, Selects a set of keys whose corresponding channels are ready for I/O operations. 
			Set<SelectionKey> selectionKeys=selector.selectedKeys();//selector�󶨵�key
			Iterator<SelectionKey> iter=selectionKeys.iterator();
			SocketChannel clientChannel;
			while(iter.hasNext()) {
				SelectionKey selectionKey=iter.next();//��ʼ����key
				if(selectionKey.isAcceptable()) {//����accept�¼�
					serverSocketChannel=(ServerSocketChannel) selectionKey.channel();
					clientChannel=serverSocketChannel.accept();//�����������ҵ��(�˴�Ϊaccept�¼�)��ͨ��
					clientChannel.configureBlocking(false);
					
					clientChannel.register(selector, SelectionKey.OP_READ);//ע����һ���¼�
					String key="["+UUID.randomUUID()+"]";
					clientChannelMap.put(clientChannel,key);
					
					System.out.println("connected :"+clientChannel);
				}else if(selectionKey.isReadable()) {//����read�¼�
					clientChannel=(SocketChannel) selectionKey.channel();
					ByteBuffer readBuffer=ByteBuffer.allocate(1024);
					int bytesRead=clientChannel.read(readBuffer);//��channel��ȡ���ݵ�buffer
					if(bytesRead>0) {//ȷ�е��յ���Ϣ��
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
						//���������յ�����Ϣת��������client(�����ӵ�)
						for(SocketChannel socketChannel:clientChannelMap.keySet()) {
							if(!socketChannel.isConnected()) continue;//�������ӵľͲ��÷�����
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
				iter.remove();//�Ƴ���ǰ�Ѵ�������¼�
			}
		}
	}

}
