package com.xycode.handler.unpacket;

import java.nio.charset.Charset;

public class PersonProtocol {
	private int length;
	private byte[] content;
	public int getLength() {
		return length;
	}
	public void setLength(int length) {
		this.length = length;
	}
	public byte[] getContent() {
		return content;
	}
	public void setContent(byte[] content) {
		this.content = content;
	}

	public String toString(Charset charset) {
		return "[length :"+length+", content: "+new String(content,charset)+"]";
	}
}
