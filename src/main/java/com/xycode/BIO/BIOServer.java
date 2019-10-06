package com.xycode.BIO;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BIOServer implements Runnable{
	ExecutorService es=Executors.newCachedThreadPool();
	@Override
	public void run() {
		try {
			ServerSocket serverSocket=new ServerSocket();
			serverSocket.bind(new InetSocketAddress(2233));
			while(true) {
				Socket socket=serverSocket.accept();
				es.submit(new BIOHandler(socket));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	static class BIOHandler implements Runnable{
		Socket client_socket;
		public BIOHandler(Socket socket) {
			super();
			this.client_socket = socket;
		}

		@Override
		public void run() {
			BufferedReader is=null;
			PrintWriter os=null;
			try {
				is=new BufferedReader(new InputStreamReader(client_socket.getInputStream()));
				os=new PrintWriter(client_socket.getOutputStream(),true);//autoFlush=true
				String inputLine=null;
				while((inputLine=is.readLine())!=null) {
					System.out.println("BIOServer recv : "+inputLine+",from "+client_socket.getRemoteSocketAddress());
					String info="BIOServer Echo";
					System.out.println("BIOServer send : "+info+",to "+client_socket.getRemoteSocketAddress());
					os.println(info);
				}
			}catch (IOException e) {
				e.printStackTrace();
			}finally {
				try{
					if(is!=null) is.close();
					if(os!=null) os.close();
					if(client_socket!=null) client_socket.close();
				}catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
	}
	
	public static void main(String[] args) {
		BIOServer bioServer=new BIOServer();
		new Thread(bioServer).start();
	}

}
