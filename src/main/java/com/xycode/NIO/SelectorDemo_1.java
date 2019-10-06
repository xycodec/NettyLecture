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
		System.out.println(selector.provider().getClass());//class sun.nio.ch.WindowsSelectorProvider,与OS有关
		
		int[] ports=new int[10];
		for(int i=0;i<ports.length;++i) {
			ports[i]=10000+i;
			ServerSocketChannel serverSocketChannel=ServerSocketChannel.open();
			serverSocketChannel.configureBlocking(false);
			ServerSocket serverSocket=serverSocketChannel.socket();
			serverSocket.bind(new InetSocketAddress(ports[i]));
			
			serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);//serverSocketChannel注册到selector,注册事件类型为OP_ACCEPT
			System.out.println("listening port: "+ports[i]);
			
		}
		
		while(true) {
//			int numbers=selector.select();//select,并返回key的数量
//			System.out.println("key numbers: "+numbers);
			
			selector.select();//select,阻塞方法, Selects a set of keys whose corresponding channels are ready for I/O operations. 
			Set<SelectionKey> selectionKeys=selector.selectedKeys();//selector绑定的key
			
			Iterator<SelectionKey> iter=selectionKeys.iterator();
			while(iter.hasNext()) {
				SelectionKey selectionKey=iter.next();//开始迭代key
				if(selectionKey.isAcceptable()) {//处理accept事件
					ServerSocketChannel serverSocketChannel=(ServerSocketChannel) selectionKey.channel();
//					serverSocketChannel.configureBlocking(false);//实际上并不用再设置,因为在这条处理链的ACCPT段已经设置过了
					SocketChannel socketChannel=serverSocketChannel.accept();//获得真正处理业务(此处为accept事件)的通道
					socketChannel.configureBlocking(false);
					
					socketChannel.register(selector, SelectionKey.OP_READ);//注册下一个事件
					
					System.out.println("connected :"+socketChannel);
				}else if(selectionKey.isReadable()) {//处理read事件
					SocketChannel socketChannel=(SocketChannel) selectionKey.channel();
//					socketChannel.configureBlocking(false);//实际上并不用再设置,因为在这条处理链的ACCPT段已经设置过了
					int bytesRead=0;
					ByteBuffer byteBuffer=ByteBuffer.allocate(1024);
					while(true) {
						byteBuffer.clear();
						int read=socketChannel.read(byteBuffer);//从channel读取数据到buffer
						if(read<=0) break;
						byteBuffer.flip();
						socketChannel.write(byteBuffer);//client write to server

						bytesRead+=read;
					}
					System.out.println("read "+bytesRead+" bytes, from "+socketChannel);
				}
				iter.remove();//移出当前已处理完的时间
			}
		}
	}

}
