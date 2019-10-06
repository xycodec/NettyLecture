package com.xycode.zerocopy;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class NonZeroCopyClient {

	public static void main(String[] args) throws UnknownHostException, IOException {
		Socket socket=new Socket("localhost",2233);
		String fileName="e:/onos-tutorial-1.0.0r161-ovf.zip";//文件大小: 2.44GB
		InputStream inputStream=new FileInputStream(new File(fileName));
		DataOutputStream dataOutputStream=new DataOutputStream(socket.getOutputStream());
		byte[] buffer=new byte[4096];
		long readCnt=0;
		long total=0;
		long startTime=System.currentTimeMillis();
		while((readCnt=inputStream.read(buffer))!=-1) {
			total+=readCnt;
			dataOutputStream.write(buffer);
		}
		System.out.println("send "+total/1000.0+" KB,"+"cost "+(System.currentTimeMillis()-startTime)+" ms");
		
		dataOutputStream.close();
		socket.close();
		inputStream.close();
	}

}
