package com.xycode.zerocopy;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class NonZeroServer {

	public static void main(String[] args) throws IOException {
		ServerSocket serverSocket=new ServerSocket(2233);
		while(true) {
			Socket socket=serverSocket.accept();
			DataInputStream dataInputStream=new DataInputStream(socket.getInputStream());
			byte[] byteArray=new byte[4096];
			while(true) {
				int readCnt=dataInputStream.read(byteArray,0,byteArray.length);
				if(readCnt==-1) break;
			}
		}
	}
}
