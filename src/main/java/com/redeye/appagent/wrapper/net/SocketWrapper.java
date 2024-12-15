package com.redeye.appagent.wrapper.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import com.redeye.appagent.logger.ApiType;
import com.redeye.appagent.logger.Log;

/**
 * 
 * @author jmsohn
 */
class SocketWrapper extends Socket {
	
	public SocketWrapper() {
		super();
	}
	
	public SocketWrapper(String host, int port) throws UnknownHostException, IOException {
		super(host, port);
	}
	
	public SocketWrapper(InetAddress address, int port) throws UnknownHostException, IOException {
		super(address, port);
	}
	
	public SocketWrapper(Proxy proxy) throws UnknownHostException, IOException {		
		super(proxy);
	}
	
	@Override
	public void connect(SocketAddress endpoint) throws IOException {
		
		if(endpoint instanceof InetSocketAddress) {
			Log.writeLog(ApiType.TCP_SOCKET.getApiTypeName()
					, this, 0
					, "TTC RIP=%s RPT=%d"
					, ((InetSocketAddress) endpoint).getHostName()
					, ((InetSocketAddress) endpoint).getPort());
		}
		
		long start = System.currentTimeMillis();
		super.connect(endpoint);
		long end = System.currentTimeMillis();
		
		if(endpoint instanceof InetSocketAddress) {
			Log.writeLog(ApiType.TCP_SOCKET.getApiTypeName()
					, this, end - start
					, "CNT " + SocketUtil.getSocketStatus(this));
		}
	}
	
	@Override
	public void connect(SocketAddress endpoint, int timeout) throws IOException {
		
		if(endpoint instanceof InetSocketAddress) {
			Log.writeLog(ApiType.TCP_SOCKET.getApiTypeName()
					, this, 0
					, "TTC RIP=%s RPT=%d"
					, ((InetSocketAddress) endpoint).getHostName()
					, ((InetSocketAddress) endpoint).getPort());
		}
		
		long start = System.currentTimeMillis();
		super.connect(endpoint, timeout);
		long end = System.currentTimeMillis();

		if(endpoint instanceof InetSocketAddress) {
			Log.writeLog(ApiType.TCP_SOCKET.getApiTypeName()
					, this
					, end - start
					, "CNT " + SocketUtil.getSocketStatus(this));
		}
	}
	
	@Override
	public void bind(SocketAddress bindpoint) throws IOException {
		super.bind(bindpoint);
	}
	
	@Override
	public OutputStream getOutputStream() throws IOException {
		
		OutputStream os = new SocketOutputStreamWrapper(this, super.getOutputStream());
		return os;
	}

	
	@Override
	public InputStream getInputStream() throws IOException {
		
		InputStream is = new SocketInputStreamWrapper(this, super.getInputStream());
		return is;
	}
	
	@Override
	public void setSoTimeout(int timeout) throws SocketException {
		super.setSoTimeout(timeout);
	}
	
	@Override
	public void close() throws IOException {
		Log.writeLog(ApiType.TCP_SOCKET.getApiTypeName()
				, this
				, 0
				, "CLS");
		super.close();
	}
}
