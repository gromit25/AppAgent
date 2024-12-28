package com.redeye.appagent.builtins.net;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

import com.redeye.appagent.builtins.io.InputStreamWrapper;
import com.redeye.appagent.logger.ApiType;
import com.redeye.appagent.logger.Log;

class SocketInputStreamWrapper extends InputStreamWrapper {
	
	private Socket socket;
	
	SocketInputStreamWrapper(Socket socket, InputStream is) {
		super(is);
		this.socket = socket;
	}
	
	@Override
	public int read() throws IOException {
		
		Log.writeLog(ApiType.TCP_SOCKET.getName()
				, this.is
				, 0
				, "RFS " + SocketUtil.getSocketStatus(this.socket));
		
		long start = System.currentTimeMillis();
		int read = super.read();
		long end = System.currentTimeMillis();
		
		Log.writeLog(ApiType.TCP_SOCKET.getName()
				, this.is
				, end - start
				, "RCT 1");
		
		return read;
	}
	
	@Override
	public int read(byte[] b) throws IOException {
		
		Log.writeLog(ApiType.TCP_SOCKET.getName()
				, this.is
				, 0
				, "RFS " + SocketUtil.getSocketStatus(this.socket));
		
		long start = System.currentTimeMillis();
		int readCnt = super.read(b);
		long end = System.currentTimeMillis();
		
		Log.writeLog(ApiType.TCP_SOCKET.getName()
				, this.is
				, end - start
				, "RCT " + readCnt);
		
		return readCnt;
	}
	
	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		
		Log.writeLog(ApiType.TCP_SOCKET.getName()
				, this.is
				, 0
				, "RFS " + SocketUtil.getSocketStatus(this.socket));
		
		long start = System.currentTimeMillis();
		int readCnt = super.read(b, off, len);
		long end = System.currentTimeMillis();
		
		Log.writeLog(ApiType.TCP_SOCKET.getName()
				, this.is
				, end - start
				, "RCT " + readCnt);
		
		return readCnt;
	}
}
