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
	
	/**
	 * 접속 주소 로깅
	 * 
	 * @param addr 접속 주소 
	 */
	private static void loggingAddr(SocketAddress addr) {
		
		if(addr != null) {
			
			if(addr instanceof InetSocketAddress) {
				
				String hostName = ((InetSocketAddress)addr).getHostName();
				int port = ((InetSocketAddress)addr).getPort();
				
				Log.write("SCH", null, "hostname: \"%s\", port:%d", hostName, port);
			}
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
		
		loggingAddr(addr);
		
		SocketChannel channel = SocketChannel.open();
		return channel;
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
		
		loggingAddr(addr);
		
		boolean result = channel.connect(addr);
		return result;
	}
}
