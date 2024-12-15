package com.redeye.appagent.wrapper.net;

import java.net.Socket;

class SocketUtil {
	
	static String getSocketStatus(final Socket socket) {
		
		if(socket == null) {
			return "socket is null.";
		}
		
		StringBuilder socketStatusBuilder = new StringBuilder("");
		
		try {
			
			socketStatusBuilder
				.append(" RIP=").append(socket.getInetAddress().getHostAddress())
				.append(" RPT=").append(socket.getPort())
				.append(" LPT=").append(socket.getLocalPort())
				.append(" BO=").append(toShort(socket.isBound()))
				.append(" CL=").append(toShort(socket.isClosed()))
				.append(" CO=").append(toShort(socket.isConnected()))
				.append(" IS=").append(toShort(socket.isInputShutdown()))
				.append(" OS=").append(toShort(socket.isOutputShutdown()))
				.append(" KA=").append(toShort(socket.getKeepAlive()))
				.append(" TO=").append(socket.getSoTimeout());
			
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		
		return socketStatusBuilder.toString();
	}
	
	private static String toShort(final boolean b) {
		if(b == true) {
			return "T";
		} else {
			return "F";
		}
	}
	
}
