package com.redeye.appagent.builtins.socket;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.SocketChannel;

import com.redeye.appagent.annotation.TargetClass;
import com.redeye.appagent.annotation.TargetMethod;
import com.redeye.appagent.logger.Log;

/**
 * 소켓 채널 Wrapper
 *
 * @author jmsohn
 */
@TargetClass(type="SOCKET_CHANNEL", cls="java/nio/channels/SocketChannel")
public class SocketChannelWrapper {
	
	/** 채널 타입 */
	private static final String API_TYPE = "SCH";
	
	/**
	 * 접속 주소 로깅
	 * 
	 * @param addr 접속 주소 
	 */
	private static void logAddr(SocketAddress addr) {
		
		if(addr != null) {
			
			if(addr instanceof InetSocketAddress) {
				
				String hostName = ((InetSocketAddress)addr).getHostName();
				int port = ((InetSocketAddress)addr).getPort();
				
				Log.write(API_TYPE, null, "hostname: \"%s\", port:%d", hostName, port);
			}
		}
	}
	
	/**
	 * 접속 실패시 로깅
	 * 
	 * @param addr 접속 주소
	 * @param ioex 예외 객체
	 */
	private static void logConnFail(SocketAddress addr, IOException ioex) {
		if(addr != null) {
			Log.write(API_TYPE, null, "fail to connect addr(" + addr + "):" + ioex.getMessage());
		} else {
			Log.write(API_TYPE, null, "addr is null.");
		}
	}

	/**
	 * open 메소드 wrapper
	 *
	 * @param addr 연결 주소
	 * @return 생성된 소켓 채널
	 */
	@TargetMethod("open(Ljava/net/SocketAddress;)Ljava/nio/channels/SocketChannel;")
	public static SocketChannel open(SocketAddress addr) throws IOException {
		
		logAddr(addr);
		
		try {
			
			SocketChannel channel = SocketChannel.open();
			return channel;
			
		} catch(IOException ioex) {

			logConnFail(addr, ioex);
			throw ioex;
		}
	}

	/**
	 * connect 메소드 wrapper
	 *
	 * @param channel 소켓 채널
	 * @param addr 연결 주소
	 * @return 연결 성공 여부
	 */
	@TargetMethod("connect(Ljava/net/SocketAddress;)Z")
	public static boolean connect(SocketChannel channel, SocketAddress addr) throws IOException {
		
		logAddr(addr);
		
		try {
			
			boolean result = channel.connect(addr);
			return result;
			
		} catch(IOException ioex) {
			
			logConnFail(addr, ioex);
			throw ioex;
		}
	}
}
