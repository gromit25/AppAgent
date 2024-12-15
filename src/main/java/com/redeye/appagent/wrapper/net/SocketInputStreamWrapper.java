package com.redeye.appagent.wrapper.net;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

import com.redeye.appagent.log.ApiType;
import com.redeye.appagent.log.Log;
import com.redeye.appagent.wrapper.io.InputStreamWrapper;

class SocketInputStreamWrapper extends InputStreamWrapper {
	
	private Socket socket;
	
	SocketInputStreamWrapper(Socket socket, InputStream is) {
		super(is);
		this.socket = socket;
	}
	
	@Override
	public int read() throws IOException {
		
		Log.writeLog(ApiType.TCP_SOCKET.getApiTypeName()
				, this.is
				, 0
				, "RFS " + SocketUtil.getSocketStatus(this.socket));
		
		long start = System.currentTimeMillis();
		int read = super.read();
		long end = System.currentTimeMillis();
		
		Log.writeLog(ApiType.TCP_SOCKET.getApiTypeName()
				, this.is
				, end - start
				, "RCT 1");
		
		return read;
	}
	
	@Override
	public int read(byte[] b) throws IOException {
		
		Log.writeLog(ApiType.TCP_SOCKET.getApiTypeName()
				, this.is
				, 0
				, "RFS " + SocketUtil.getSocketStatus(this.socket));
		
		long start = System.currentTimeMillis();
		int readCnt = super.read(b);
		long end = System.currentTimeMillis();
		
		Log.writeLog(ApiType.TCP_SOCKET.getApiTypeName()
				, this.is
				, end - start
				, "RCT " + readCnt);
		
		return readCnt;
	}
	
	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		
		Log.writeLog(ApiType.TCP_SOCKET.getApiTypeName()
				, this.is
				, 0
				, "RFS " + SocketUtil.getSocketStatus(this.socket));
		
		long start = System.currentTimeMillis();
		int readCnt = super.read(b, off, len);
		long end = System.currentTimeMillis();
		
		Log.writeLog(ApiType.TCP_SOCKET.getApiTypeName()
				, this.is
				, end - start
				, "RCT " + readCnt);
		
		return readCnt;
	}
}
