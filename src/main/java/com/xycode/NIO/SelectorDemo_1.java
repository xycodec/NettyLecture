package com.xycode.NIO;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;


public class SelectorDemo_1 {
	
	public static void main(String[] args) throws IOException {
		/*
		 * SelectorProvider.class
		 * 
		     public static SelectorProvider provider() {
		        synchronized (lock) {
		            if (provider != null)
		                return provider;
		            return AccessController.doPrivileged(
		                new PrivilegedAction<SelectorProvider>() {
		                    public SelectorProvider run() {
		                            if (loadProviderFromProperty())
		                                return provider;
		                            if (loadProviderAsService())
		                                return provider;
		                                
		                            provider = sun.nio.ch.DefaultSelectorProvider.create();
		                            return provider;
		                            
		                        }
		                    });
		        }
		    }

		 */
		Selector selector=Selector.open();
		System.out.println(selector.provider().getClass());//class sun.nio.ch.WindowsSelectorProvider,��OS�й�
		
		int[] ports=new int[10];
		for(int i=0;i<ports.length;++i) {
			ports[i]=10000+i;
			ServerSocketChannel serverSocketChannel=ServerSocketChannel.open();
			serverSocketChannel.configureBlocking(false);
			ServerSocket serverSocket=serverSocketChannel.socket();
			serverSocket.bind(new InetSocketAddress(ports[i]));
			
			serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);//serverSocketChannelע�ᵽselector,ע���¼�����ΪOP_ACCEPT
			System.out.println("listening port: "+ports[i]);
			
		}
		
		while(true) {
//			int numbers=selector.select();//select,������key������
//			System.out.println("key numbers: "+numbers);
			
			selector.select();//select,��������, Selects a set of keys whose corresponding channels are ready for I/O operations. 
			Set<SelectionKey> selectionKeys=selector.selectedKeys();//selector�󶨵�key
			
			Iterator<SelectionKey> iter=selectionKeys.iterator();
			while(iter.hasNext()) {
				SelectionKey selectionKey=iter.next();//��ʼ����key
				if(selectionKey.isAcceptable()) {//����accept�¼�
					ServerSocketChannel serverSocketChannel=(ServerSocketChannel) selectionKey.channel();
//					serverSocketChannel.configureBlocking(false);//ʵ���ϲ�����������,��Ϊ��������������ACCPT���Ѿ����ù���
					SocketChannel socketChannel=serverSocketChannel.accept();//�����������ҵ��(�˴�Ϊaccept�¼�)��ͨ��
					socketChannel.configureBlocking(false);
					
					socketChannel.register(selector, SelectionKey.OP_READ);//ע����һ���¼�
					
					System.out.println("connected :"+socketChannel);
				}else if(selectionKey.isReadable()) {//����read�¼�
					SocketChannel socketChannel=(SocketChannel) selectionKey.channel();
//					socketChannel.configureBlocking(false);//ʵ���ϲ�����������,��Ϊ��������������ACCPT���Ѿ����ù���
					int bytesRead=0;
					ByteBuffer byteBuffer=ByteBuffer.allocate(1024);
					while(true) {
						byteBuffer.clear();
						int read=socketChannel.read(byteBuffer);//��channel��ȡ���ݵ�buffer
						if(read<=0) break;
						byteBuffer.flip();
						socketChannel.write(byteBuffer);//client write to server

						bytesRead+=read;
					}
					System.out.println("read "+bytesRead+" bytes, from "+socketChannel);
				}
				iter.remove();//�Ƴ���ǰ�Ѵ������ʱ��
			}
		}
	}

}
