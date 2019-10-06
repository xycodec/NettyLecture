package com.xycode.BIO;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

public class BIOClient {

	public static void main(String[] args) throws IOException {
		Socket client=null;
		PrintWriter writer=null;
		BufferedReader reader=null;
		try {
			client=new Socket();
			client.connect(new InetSocketAddress(2233));
			writer=new PrintWriter(client.getOutputStream(),true);
			reader=new BufferedReader(new InputStreamReader(client.getInputStream()));
			String inputLine=null;
			String info="BIOClient hello";
			writer.println(info);
			System.out.println("Client send : "+info);
			if((inputLine=reader.readLine())!=null) {
				System.out.println("Client recv : "+inputLine);
			}

		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			try {
				if(writer!=null) writer.close();
				if(reader!=null) reader.close();
				if(client!=null) client.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
