package com.redeye.appagent.wrapper.net;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Proxy;
import java.net.Socket;
import java.net.UnknownHostException;

import com.redeye.appagent.log.ApiType;
import com.redeye.appagent.log.Log;

/**
 * 
 * @author jmsohn
 */
public class SocketConstructorWrapper {
	
	public static Socket init() {
		
		SocketWrapper socket = new SocketWrapper();
		return socket;
	}
	
	public static Socket init(String host, int port) throws UnknownHostException, IOException {
		
		Log.writeLog(ApiType.TCP_SOCKET.getApiTypeName()
				, "CREATE", 0
				, "TTC RIP=%s RPT=%d"
				, host
				, port);
		
		long start = System.currentTimeMillis();
		SocketWrapper socket = new SocketWrapper(host, port);
		long end = System.currentTimeMillis();
		
		Log.writeLog(ApiType.TCP_SOCKET.getApiTypeName()
				, socket
				, end - start
				, "CNT " + SocketUtil.getSocketStatus(socket));
		
		return socket;
	}
	
	public static Socket init(InetAddress address, int port) throws UnknownHostException, IOException {
		
		Log.writeLog(ApiType.TCP_SOCKET.getApiTypeName()
				, "CREATE", 0
				, "TTC RIP=%s RPT=%d"
				, address.getHostName()
				, port);
		
		long start = System.currentTimeMillis();
		SocketWrapper socket = new SocketWrapper(address, port);
		long end = System.currentTimeMillis();
		
		Log.writeLog(ApiType.TCP_SOCKET.getApiTypeName()
				, socket
				, end - start
				, "CNT " + SocketUtil.getSocketStatus(socket));
		
		return socket;
	}
	
	public static Socket init(Proxy proxy) throws UnknownHostException, IOException {
	
		SocketWrapper socket = new SocketWrapper(proxy);
		return socket;
	}

}
